package com.consulmedics.patientdata.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.consulmedics.patientdata.data.api.request.LoginRequest
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.data.api.response.LoadShiftApiResponse
import com.consulmedics.patientdata.data.api.response.LoginResponse
import com.consulmedics.patientdata.repository.UserRepository
import kotlinx.coroutines.launch

class ShiftViewModel(application: Application) : AndroidViewModel(application)  {
    val userRepo = UserRepository()
    val loadShiftResult: MutableLiveData<BaseResponse<LoadShiftApiResponse>> = MutableLiveData()
    fun loadShiftDetails(){
        loadShiftResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.loadShiftDetails()
                if (response?.code() == 200) {
                    loadShiftResult.value = BaseResponse.Success(response.body())
                } else {
                    loadShiftResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                loadShiftResult.value = BaseResponse.Error(ex.message)
            }
        }
    }
}