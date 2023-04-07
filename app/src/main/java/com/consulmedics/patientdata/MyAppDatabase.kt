package com.consulmedics.patientdata

import android.content.Context
import androidx.room.*
import com.consulmedics.patientdata.dao.HotelDao
import com.consulmedics.patientdata.dao.PatientDao
import com.consulmedics.patientdata.data.model.Hotel
import com.consulmedics.patientdata.data.model.Patient

@Database(entities = [Patient::class, Hotel::class], version = 2)
@TypeConverters(Converters::class)
abstract  class MyAppDatabase: RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun hotelDao(): HotelDao
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
                "consulmedics_database"
            ).allowMainThreadQueries().build()
        }
    }
}