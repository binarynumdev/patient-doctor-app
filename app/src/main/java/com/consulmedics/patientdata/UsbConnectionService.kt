package com.consulmedics.patientdata

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.IBinder
import android.util.Log


class UsbConnectionService: Service() {
    val scardLib = SCardExt()
    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    // Handle USB device attached event
                    Log.e("USB", "ACTION_USB_DEVICE_ATTACHED")
                    val sttus = scardLib.USBRequestPermission(applicationContext)
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    // Handle USB device detached event
                    Log.e("USB", "ACTION_USB_DEVICE_DETACHED")
                }
                "com.android.scard.USB_PERMISSION" ->{
                    Log.e("USB", "USB_PERMISSION")
                }

                // Handle any other actions you want to monitor here
            }
        }
    }
    override fun onCreate() {

        val filter = IntentFilter()
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        // Add any other actions you want to monitor here
        Log.e("DDDD", "CREATE RECEIVER")
        registerReceiver(usbReceiver, filter)
        super.onCreate()
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbReceiver)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}