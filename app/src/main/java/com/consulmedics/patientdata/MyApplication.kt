package com.consulmedics.patientdata

import android.app.Application

class MyApplication : Application() {
    companion object {
        var patientDetailsActivityRequestCode:Int = 1001
    }

    override fun onCreate() {
        super.onCreate()
    }
}