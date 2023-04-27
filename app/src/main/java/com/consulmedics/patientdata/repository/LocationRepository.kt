package com.consulmedics.patientdata.repository

import android.app.Application
import android.location.Geocoder
import android.util.Log
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.dao.HotelDao
import com.consulmedics.patientdata.data.api.methods.FetchLocationApi
import com.consulmedics.patientdata.data.api.methods.LoginApi
import com.consulmedics.patientdata.data.api.request.FetchLocationRequest
import com.consulmedics.patientdata.data.api.request.LoginRequest
import com.consulmedics.patientdata.data.api.response.FetchLocationResponse
import com.consulmedics.patientdata.data.api.response.LoginResponse
import com.consulmedics.patientdata.utils.AppConstants
import retrofit2.Response
import java.util.*

class LocationRepository{
    suspend fun fetchAddressFromLatLang(fetchLocationRequest: FetchLocationRequest): Response<FetchLocationResponse>?{
        return FetchLocationApi.getApi()?.fetch(latLng = fetchLocationRequest.latlng, apiKey = fetchLocationRequest.apKey)
    }
    suspend fun fetchAddressFromString(fetchLocationRequest: FetchLocationRequest): Response<FetchLocationResponse>?{
        return FetchLocationApi.getApi()?.fetchFromString(address = fetchLocationRequest.address, apiKey = fetchLocationRequest.apKey)
    }
    fun getAddressFromLatLng(application: Application, latitude: Double, longitude: Double) {
//        try{
//            val geocoder = Geocoder(application, Locale.getDefault())
//            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
//            if (addresses.isNotEmpty()) {
//                val address = addresses[0]
//                val sb = StringBuilder()
//                for (i in 0..address.maxAddressLineIndex) {
//                    Log.e(AppConstants.TAG_NAME,address.getAddressLine(i) )
//
//                    Log.e(AppConstants.TAG_NAME, address.adminArea)
//                    Log.e(AppConstants.TAG_NAME, address.locality) // city
//                    Log.e(AppConstants.TAG_NAME, address.featureName) // house number
//                    Log.e(AppConstants.TAG_NAME, address.postalCode)
//                    Log.e(AppConstants.TAG_NAME, address.thoroughfare)
//                    sb.append(address.getAddressLine(i)).append("\n")
//                }
//            }
//        }
//        catch (e: java.lang.Exception){
//            e.printStackTrace()
//        }

    }

}