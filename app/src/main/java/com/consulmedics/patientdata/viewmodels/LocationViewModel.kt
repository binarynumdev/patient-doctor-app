package com.consulmedics.patientdata.viewmodels

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.data.api.request.FetchLocationRequest
import com.consulmedics.patientdata.data.api.request.LoginRequest
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.data.api.response.FetchLocationResponse
import com.consulmedics.patientdata.data.api.response.LoginResponse
import com.consulmedics.patientdata.data.model.Address
import com.consulmedics.patientdata.repository.AddressRepository
import com.consulmedics.patientdata.repository.LocationRepository
import com.consulmedics.patientdata.repository.UserRepository
import com.consulmedics.patientdata.utils.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class LocationViewModel(private val addressRepo: AddressRepository) : ViewModel() {
    val locationRepo = LocationRepository()
    val fetchResponseResult: MutableLiveData<BaseResponse<FetchLocationResponse>> = MutableLiveData()

    fun getAddressFromLatLng(latitude: Double, longitude: Double,apiKey: String) {

        viewModelScope.launch {
            try {
                val fetchLocationRequest = FetchLocationRequest(
                    latlng = "${latitude},${longitude}",
                    apKey = apiKey
                )

                val response = locationRepo.fetchAddressFromLatLang(fetchLocationRequest)

                if (response?.code() == 200) {
                    if(response.body()?.results?.count()!! > 0){

                    }
                    fetchResponseResult.value = BaseResponse.Success(response.body())
                } else {
                    fetchResponseResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                Log.e("APIRESULT", "${ex.message}")
                fetchResponseResult.value = BaseResponse.Error(ex.message)
//                loginResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun saveAddress(address: Address) = viewModelScope.launch(Dispatchers.IO){
        addressRepo.insert(address)
    }

}