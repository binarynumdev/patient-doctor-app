package com.consulmedics.patientdata

import android.content.Context
import com.consulmedics.patientdata.models.Patient
import com.consulmedics.patientdata.scardlib.SCard
import com.consulmedics.patientdata.scardlib.WinDefs
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.zip.GZIPInputStream

class SCardExt{
    var initialized: Boolean = false
    var scard: SCard = SCard()
    var deviceList: ArrayList<String> = ArrayList<String>()
    var readers: Array<CharSequence>? = null
    fun USBRequestPermission(applicationContext: Context?): Long {
        return scard.USBRequestPermission(applicationContext)
    }
    fun SCardEstablishContext(applicationContext: Context?): Long{
        return scard.SCardEstablishContext(applicationContext)
    }
    fun SCardListReaders(applicationContext: Context?): Long{

        var status  = scard.SCardListReaders(applicationContext, deviceList)
        if(status != 0L){
            return status
        }
        else{
            readers = deviceList.toArray() as Array<CharSequence>?;
            initialized = true
            return status
        }

    }
    fun SCardConnect(): Long {
        return scard.SCardConnect(
            readers?.get(0) as String?, WinDefs.SCARD_SHARE_EXCLUSIVE,
            WinDefs.SCARD_PROTOCOL_TX.toInt()
        )
    }


    private fun sendSelectHCARequest(): String? {
        val inbuf = byteArrayOf(
            0x00,
            0xa4.toByte(),
            0x04,
            0x0c,
            0x06,
            0xd2.toByte(),
            0x76,
            0x00,
            0x00,
            0x01,
            0x02
        )
        val transmit = scard.SCardIOBuffer()
        transmit.setnInBufferSize(inbuf.size)
        transmit.abyInBuffer = inbuf
        transmit.setnOutBufferSize(0x8000)
        transmit.abyOutBuffer = ByteArray(0x8000)
        scard.SCardTransmit(transmit)
        var rstr = ""
        for (k in 0 until transmit.getnBytesReturned()) {
            val temp = transmit.abyOutBuffer[k].toInt() and 0xFF
            rstr = if (temp < 16) {
                rstr.uppercase(Locale.getDefault()) + "0" + Integer.toHexString(
                    transmit.abyOutBuffer[k].toInt()
                )
            } else {
                rstr.uppercase(Locale.getDefault()) + Integer.toHexString(temp)
            }
        }
        return rstr
    }

    @Throws(IOException::class)
    fun toUnzippedByteArray(zippedBytes: ByteArray?): ByteArray {
        var baos: ByteArrayOutputStream? = null
        return try {
            var size: Int
            val memstream = ByteArrayInputStream(
                zippedBytes
            )
            val gzip = GZIPInputStream(memstream)
            val buffSize = 256
            val tempBuffer = ByteArray(buffSize)
            baos = ByteArrayOutputStream()
            while (gzip.read(tempBuffer, 0, buffSize).also { size = it } != -1) {
                baos.write(tempBuffer, 0, size)
            }
            printBytes(baos.toByteArray())
            baos.toByteArray()
        } finally {
            if (baos != null) {
                baos.close()
            }
        }
    }

    @Throws(IOException::class)
    private fun readPd(): String? {
        val inbuf = byteArrayOf(0x00, 0xb0.toByte(), (0x80 + 0x01).toByte(), 0x00, 0x00, 0x00, 0x00)
        val transmit = scard.SCardIOBuffer()
        transmit.setnInBufferSize(inbuf.size)
        transmit.abyInBuffer = inbuf
        transmit.setnOutBufferSize(0x8000)
        transmit.abyOutBuffer = ByteArray(0x8000)
        scard.SCardTransmit(transmit)
        var rstr = ""
        val transResult = ByteArray(transmit.getnBytesReturned() - 2)
        for (k in 0 until transmit.getnBytesReturned()) {
            val temp = transmit.abyOutBuffer[k].toInt() and 0xFF
            if (k < transmit.getnBytesReturned() - 2) transResult[k] = temp.toByte()
            rstr = if (temp < 16) {
                rstr.uppercase(Locale.getDefault()) + "0" + Integer.toHexString(
                    transmit.abyOutBuffer[k].toInt()
                )
            } else {
                rstr.uppercase(Locale.getDefault()) + Integer.toHexString(temp)
            }
        }
        val pdLength = transResult[0].toInt() and 0xff shl 8 or (transResult[1].toInt() and 0xff)
        val pdDataCompressed = ByteArray(pdLength)
        System.arraycopy(transResult, 2, pdDataCompressed, 0, pdLength)
        val decompressed = toUnzippedByteArray(pdDataCompressed)
        val str = String(decompressed, StandardCharsets.UTF_8)
        return rstr
    }

    @Throws(IOException::class)
    private fun readVd(): String? {
        val inbuf = byteArrayOf(0x00, 0xb0.toByte(), (0x80 + 0x02).toByte(), 0x00, 0x00, 0x00, 0x00)
        val transmit = scard.SCardIOBuffer()
        transmit.setnInBufferSize(inbuf.size)
        transmit.abyInBuffer = inbuf
        transmit.setnOutBufferSize(0x8000)
        transmit.abyOutBuffer = ByteArray(0x8000)
        scard.SCardTransmit(transmit)
        var rstr = ""
        var vdDataOnly = ""
        val transResult = ByteArray(transmit.getnBytesReturned() - 2)
        for (k in 0 until transmit.getnBytesReturned()) {
            val temp = transmit.abyOutBuffer[k].toInt() and 0xFF
            if (k < transmit.getnBytesReturned() - 2) {
                transResult[k] = temp.toByte()
                vdDataOnly = if (temp < 16) {
                    vdDataOnly.uppercase(Locale.getDefault()) + "0" + Integer.toHexString(
                        transmit.abyOutBuffer[k].toInt()
                    )
                } else {
                    vdDataOnly.uppercase(Locale.getDefault()) + Integer.toHexString(temp)
                }
            }
            rstr = if (temp < 16) {
                rstr.uppercase(Locale.getDefault()) + "0" + Integer.toHexString(
                    transmit.abyOutBuffer[k].toInt()
                )
            } else {
                rstr.uppercase(Locale.getDefault()) + Integer.toHexString(temp)
            }
        }
        val offsetStartVD =
            transResult[0].toInt() and 0xff shl 8 or (transResult[1].toInt() and 0xff)
        val offsetEndVD = transResult[2].toInt() and 0xff shl 8 or (transResult[3].toInt() and 0xff)
        val vdDataCompressed = ByteArray(offsetEndVD - offsetStartVD + 1)
        System.arraycopy(transResult, offsetStartVD, vdDataCompressed, 0, vdDataCompressed.size)
        printBytes(vdDataCompressed)
        val decompressed = toUnzippedByteArray(vdDataCompressed)
        val str = String(decompressed, StandardCharsets.UTF_8)

        return rstr
    }
    fun printBytes(data: ByteArray) {
        val result_escape = StringBuilder()
        for (bb in data) {
            result_escape.append(String.format(" %02X", bb))
        }
    }
    private fun sendSelectRootRequest(): String {
        val inbuf = byteArrayOf(
            0x00,
            0xa4.toByte(),
            0x04,
            0x0c,
            0x07,
            0xd2.toByte(),
            0x76,
            0x00,
            0x01,
            0x44,
            0x80.toByte(),
            0x00
        )
        val transmit = scard.SCardIOBuffer()
        transmit.setnInBufferSize(inbuf.size)
        transmit.abyInBuffer = inbuf
        transmit.setnOutBufferSize(0x8000)
        transmit.abyOutBuffer = ByteArray(0x8000)
        scard.SCardTransmit(transmit)
        var rstr = ""
        for (k in 0 until transmit.getnBytesReturned()) {
            val temp = transmit.abyOutBuffer[k].toInt() and 0xFF
            rstr = if (temp < 16) {
                rstr.uppercase(Locale.getDefault()) + "0" + Integer.toHexString(
                    transmit.abyOutBuffer[k].toInt()
                )
            } else {
                rstr.uppercase(Locale.getDefault()) + Integer.toHexString(temp)
            }
        }
        return rstr
    }
    fun getPatientData(): Patient {
        val parient = Patient()

        val selectRootResponse: String = sendSelectRootRequest()
        if(selectRootResponse.equals("9000")){
            val selectHCAResponse = sendSelectHCARequest()
            var readPDResponse:String? = "";
            var readVDResponse: String? = "";
            try {
                readPDResponse = readPd()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                readVDResponse = readVd()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            parient.loadFrom(readPDResponse, readVDResponse)
        }
        return parient
    }


}