package com.consulmedics.patientdata.models

import androidx.lifecycle.LiveData
import com.consulmedics.patientdata.dao.PatientDao

class PatientRepository(private val patientDao: PatientDao)  {
    val allPatients: LiveData<List<Patient>> = patientDao.getAll()

    // on below line we are creating an insert method
    // for adding the note to our database.
    suspend fun insert(patient: Patient) {
        patientDao.insertAll(patient)
    }

    // on below line we are creating a delete method
    // for deleting our note from database.
    suspend fun delete(patient: Patient){
        patientDao.delete(patient)
    }

    // on below line we are creating a update method for
    // updating our note from database.
    suspend fun update(patient: Patient){
        patientDao.updatePatient(patient)
    }
}