package com.consulmedics.patientdata.dao

import androidx.room.*
import com.consulmedics.patientdata.models.Patient

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg patient: Patient)
    @Delete
    fun delete(patient: Patient)
    @Query("select * from patient")
    fun getAll(): List<Patient>
    @Update
    fun updatePatient(vararg  patient: Patient)
}