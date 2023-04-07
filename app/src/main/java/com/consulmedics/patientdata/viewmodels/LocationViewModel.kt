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
import com.consulmedics.patientdata.repository.LocationRepository
import com.consulmedics.patientdata.repository.UserRepository
import com.consulmedics.patientdata.utils.AppConstants
import kotlinx.coroutines.launch
import java.util.*

class LocationViewModel(application: Application) : AndroidViewModel(application) {
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
//                loginResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getAddressUsingGeoCode(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val geocoder = Geocoder(getApplication(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    Log.e(AppConstants.TAG_NAME, address.getAddressLine(i))
                    Log.e(AppConstants.TAG_NAME, address.adminArea)
                    Log.e(AppConstants.TAG_NAME, address.locality) // city
                    Log.e(AppConstants.TAG_NAME, address.featureName) // house number
                    Log.e(AppConstants.TAG_NAME, address.postalCode)
                    Log.e(AppConstants.TAG_NAME, address.thoroughfare)
                    sb.append(address.getAddressLine(i)).append("\n")
                }
            }
        }
    }

}