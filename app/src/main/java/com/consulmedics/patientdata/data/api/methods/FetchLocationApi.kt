package com.consulmedics.patientdata.data.api.methods

import com.consulmedics.patientdata.data.api.ApiClient
import com.consulmedics.patientdata.data.api.request.FetchLocationRequest
import com.consulmedics.patientdata.data.api.request.LoginRequest
import com.consulmedics.patientdata.data.api.response.FetchLocationResponse
import com.consulmedics.patientdata.data.api.response.LoginResponse
import com.consulmedics.patientdata.utils.AppConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface FetchLocationApi {
    @GET(AppConstants.FETCH_ADDRESS_FROM_LOCATION_ENDPOINT)
    suspend fun fetch(@Query("latlng") latLng:String, @Query("key") apiKey: String): Response<FetchLocationResponse>

    companion object {
        fun getApi(): FetchLocationApi? {
            return ApiClient.googleMapApiClient?.create(FetchLocationApi::class.java)
        }
    }
}