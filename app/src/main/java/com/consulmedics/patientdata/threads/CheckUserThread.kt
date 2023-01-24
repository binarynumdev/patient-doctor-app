package com.consulmedics.patientdata.threads

import android.content.Context
import android.os.Environment.*
import android.util.Log
import com.consulmedics.patientdata.utils.AESEncyption
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import java.io.*

class CheckUserThread(appContext: Context): Thread() {
    private var certFile:File = File(getExternalStorageDirectory(),"Consulmedics/userinfo.cert")
    public override fun run() {
        if(!certFile.exists()){

            Log.e(TAG_NAME, "No Certificate File")
            Log.e(TAG_NAME, certFile.path)
            try {
                val dir = File(getExternalStorageDirectory().getAbsolutePath() + "/Consulmedics")
                dir.mkdirs();
                certFile = File(dir,"userinfo.cert")
                val fileOutPutStream = FileOutputStream(certFile)
                val encrypted: String? = AESEncyption.encrypt("This is sample text")
                fileOutPutStream.write(encrypted!!.toByteArray())
                fileOutPutStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        else{
            var fileInputStream = FileInputStream(certFile)
            var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()
            var text: String? = null
            while ({ text = bufferedReader.readLine(); text }() != null) {
                stringBuilder.append(text)
            }

            val decrypted:String? = AESEncyption.decrypt(stringBuilder.toString())
            Log.e(TAG_NAME, "$decrypted")
            fileInputStream.close()
        }
        super.run()
    }
}