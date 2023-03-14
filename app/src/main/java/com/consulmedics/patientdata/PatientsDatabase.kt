package com.consulmedics.patientdata

import android.content.Context
import androidx.room.*
import com.consulmedics.patientdata.dao.PatientDao
import com.consulmedics.patientdata.data.model.Patient

@Database(entities = [Patient::class], version = 2)
@TypeConverters(Converters::class)
abstract  class PatientsDatabase: RoomDatabase() {
    abstract fun patientDao(): PatientDao
    companion object {

        @Volatile
        private var INSTANCE: PatientsDatabase? = null

        fun getDatabase(context: Context): PatientsDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): PatientsDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                PatientsDatabase::class.java,
                "patients_database"
            ).allowMainThreadQueries().build()
        }
    }
}