package com.consulmedics.patientdata.repository

import androidx.lifecycle.LiveData
import com.consulmedics.patientdata.dao.PatientDao
import com.consulmedics.patientdata.dao.PatientShiftDao
import com.consulmedics.patientdata.data.api.response.LoadShiftApiResponse
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.data.model.PatientShift

class PatientShiftRepository(private val patientShiftDao: PatientShiftDao) {
    suspend fun saveShiftDetails(shiftList: List<LoadShiftApiResponse.ShiftDetails>) {
        shiftList.forEach{
            val patientShift = PatientShift(it.id)
            patientShift.loadFromApiResponse(it)
            this.insertOrUpdate(patientShift)
        }
    }
    suspend fun insertOrUpdate(patientShift: PatientShift) {
        patientShiftDao.insertOrUpdate(patientShift)
    }

    val pastShiftList: LiveData<List<PatientShift>> = patientShiftDao.getPastShifts()
    val upcomingShiftList: LiveData<List<PatientShift>> = patientShiftDao.getUpcoming()
    val allPatients: LiveData<List<PatientShift>> = patientShiftDao.getAll()
}