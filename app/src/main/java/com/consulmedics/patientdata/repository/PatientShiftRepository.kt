package com.consulmedics.patientdata.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.consulmedics.patientdata.dao.PatientShiftDao
import com.consulmedics.patientdata.data.api.methods.LoadShiftApi
import com.consulmedics.patientdata.data.api.methods.UploadShiftApi
import com.consulmedics.patientdata.data.api.request.UploadShiftRequest
import com.consulmedics.patientdata.data.api.response.LoadShiftApiResponse
import com.consulmedics.patientdata.data.api.response.UploadShiftApiResponse
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.data.model.PatientShift
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.utils.SessionManager.getDoctorID
import retrofit2.Response

class PatientShiftRepository(private val patientShiftDao: PatientShiftDao) {
    suspend fun saveShiftDetails(shiftList: List<LoadShiftApiResponse.ShiftDetails>, context: Context) {
        shiftList.forEach{
            val patientShift = PatientShift(it.id)
            patientShift.loadFromApiResponse(it)
            getDoctorID(context).let {
                if(it?.isNotEmpty() == true){
                    Log.e(TAG_NAME, "Doctor ID: ${it}")
                    patientShift.doctorID = it.toInt()

                }
            }
            this.insertOrUpdate(patientShift)
        }
    }
    fun insertOrUpdate(patientShift: PatientShift) {
        patientShiftDao.insertOrUpdate(patientShift)
    }

    fun upcomingShiftList(mContext: Context?): LiveData<List<PatientShift>> {
        if(mContext != null){
            return patientShiftDao.getUpcoming( getDoctorID(mContext))
        }
        else{
            return patientShiftDao.getAll()
        }

    }

    fun pastShiftList(mContext: Context?): LiveData<List<PatientShift>> {
        if(mContext != null){
            Log.e(TAG_NAME, "LOAD PATIENT SHIFTS")
            return patientShiftDao.getPastShifts( getDoctorID(mContext))
        }
        else{
            return patientShiftDao.getAll()
        }
    }

//    val pastShiftList: LiveData<List<PatientShift>> = patientShiftDao.getPastShifts()
//    val upcomingShiftList: LiveData<List<PatientShift>> = patientShiftDao.getUpcoming(getDoctorID(mContext))
    val allPatients: LiveData<List<PatientShift>> = patientShiftDao.getAll()

    suspend fun uploadShiftDetail(loginRequest: UploadShiftRequest): Response<UploadShiftApiResponse>? {
        Log.e(TAG_NAME, "TRACK POINT 1")
        return UploadShiftApi.getApi()?.upload(loginRequest)
    }

    fun delete(patientShift: PatientShift){
        patientShiftDao.delete(patientShift)
    }

    fun updatePatientShift(patientShift: PatientShift){
        patientShiftDao.updatePatientShift(patientShift)
    }
    fun updatedShiftList(mContext: Context?): LiveData<List<PatientShift>> {
            return patientShiftDao.getUploadedAll()
    }
}