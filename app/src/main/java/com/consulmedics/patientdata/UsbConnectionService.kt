package com.consulmedics.patientdata

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager


abstract class UsbConnectionService: Service() {
    val scardLib = SCardExt()
    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    // Handle USB device attached event
                    val sttus = scardLib.USBRequestPermission(applicationContext)
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    // Handle USB device detached event
                }
                // Handle any other actions you want to monitor here
            }
        }
    }
    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter()
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        // Add any other actions you want to monitor here
        registerReceiver(usbReceiver, filter)
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbReceiver)
    }
}