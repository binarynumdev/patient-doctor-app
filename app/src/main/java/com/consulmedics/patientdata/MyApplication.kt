package com.consulmedics.patientdata

import android.app.Application
import android.content.Context
import com.consulmedics.patientdata.repository.HotelRepository
import com.consulmedics.patientdata.repository.PatientRepository

class MyApplication : Application() {
    companion object {
        var patientDetailsActivityRequestCode:Int = 1001
        var patientRepository: PatientRepository? = null
        var hotelRepository: HotelRepository? = null
    }

    override fun onCreate() {
        super.onCreate()
        val patientDao = MyAppDatabase.getDatabase(this).patientDao()
        patientRepository = PatientRepository(patientDao)
        val hotelDao = MyAppDatabase.getDatabase(this).hotelDao()
        hotelRepository = HotelRepository(hotelDao)

    }
}