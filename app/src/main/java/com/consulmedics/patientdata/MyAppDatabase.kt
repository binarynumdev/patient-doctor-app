package com.consulmedics.patientdata

import android.content.Context
import androidx.room.*
import com.consulmedics.patientdata.dao.AddressDao
import com.consulmedics.patientdata.dao.HotelDao
import com.consulmedics.patientdata.dao.PatientDao
import com.consulmedics.patientdata.data.model.Address
import com.consulmedics.patientdata.data.model.Hotel
import com.consulmedics.patientdata.data.model.Patient

@Database(entities = [Patient::class, Hotel::class, Address::class], version = 4)
@TypeConverters(Converters::class)
abstract  class MyAppDatabase: RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun hotelDao(): HotelDao
    abstract fun addressDao(): AddressDao
    companion object {

        @Volatile
        private var INSTANCE: MyAppDatabase? = null

        fun getDatabase(context: Context): MyAppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): MyAppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MyAppDatabase::class.java,
                "consulmedics_database_6"
            ).allowMainThreadQueries().build()
        }
    }
}