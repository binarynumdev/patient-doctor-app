package com.consulmedics.patientdata

import android.app.Application
import android.content.Context
import android.provider.Settings.Global
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.consulmedics.patientdata.repository.AddressRepository
import com.consulmedics.patientdata.repository.HotelRepository
import com.consulmedics.patientdata.repository.PatientRepository
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME

class MyApplication : Application() {

    lateinit var userPreferences: UserPreferenceRepository
    companion object {
        var patientDetailsActivityRequestCode:Int = 1001
        var patientRepository: PatientRepository? = null
        var hotelRepository: HotelRepository? = null
        var addressRepository: AddressRepository? = null

        private val TAG: String = "MyTag " + MyApplication::class.java.simpleName
        @get:Synchronized
        lateinit var instance: MyApplication private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        userPreferences = UserPreferenceRepository.getInstance(this)

        val theme = userPreferences.appTheme
        ThemeChanger().invoke(theme)

        val patientDao = MyAppDatabase.getDatabase(this).patientDao()
        patientRepository = PatientRepository(patientDao)
        val hotelDao = MyAppDatabase.getDatabase(this).hotelDao()
        hotelRepository = HotelRepository(hotelDao)
        val addressDao = MyAppDatabase.getDatabase(this).addressDao()
        addressRepository = AddressRepository(addressDao)
    }
}