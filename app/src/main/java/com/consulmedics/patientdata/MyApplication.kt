package com.consulmedics.patientdata

import android.app.Application
import com.consulmedics.patientdata.repository.PatientRepository

class MyApplication : Application() {
    companion object {
        var patientDetailsActivityRequestCode:Int = 1001
        var repository: PatientRepository? = null
    }

    override fun onCreate() {
        super.onCreate()
        val dao = PatientsDatabase.getDatabase(this).patientDao()
        repository = PatientRepository(dao)
    }
}