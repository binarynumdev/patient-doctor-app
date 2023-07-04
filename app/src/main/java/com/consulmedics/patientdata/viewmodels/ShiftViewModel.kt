package com.consulmedics.patientdata.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.consulmedics.patientdata.MyAppDatabase
import com.consulmedics.patientdata.data.api.request.LoginRequest
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.data.api.response.LoadShiftApiResponse
import com.consulmedics.patientdata.data.api.response.LoginResponse
import com.consulmedics.patientdata.data.model.PatientShift
import com.consulmedics.patientdata.repository.PatientRepository
import com.consulmedics.patientdata.repository.PatientShiftRepository
import com.consulmedics.patientdata.repository.UserRepository
import kotlinx.coroutines.launch

class ShiftViewModel(application: Application) : AndroidViewModel(application)  {
    val pastShiftList:  LiveData<List<PatientShift>>
    val upcomingShiftList: LiveData<List<PatientShift>>
    val allShiftList: LiveData<List<PatientShift>>
    val userRepo = UserRepository()
    val repository : PatientShiftRepository
    val loadShiftResult: MutableLiveData<BaseResponse<LoadShiftApiResponse>> = MutableLiveData()
    init {
        val dao = MyAppDatabase.getDatabase(application).patientShiftDao()
        repository = PatientShiftRepository(dao)
        allShiftList = repository.allPatients
        upcomingShiftList = repository.upcomingShiftList
        pastShiftList = repository.pastShiftList
    }
    fun loadShiftDetails(){
        loadShiftResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.loadShiftDetails()
                if (response?.code() == 200) {
                    loadShiftResult.value = BaseResponse.Success(response.body())
                    response.body()?.let { repository.saveShiftDetails(it.shiftList) }
                } else {
                    loadShiftResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                loadShiftResult.value = BaseResponse.Error(ex.message)
            }
        }
    }
}