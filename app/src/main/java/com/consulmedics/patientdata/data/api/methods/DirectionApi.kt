package com.consulmedics.patientdata.data.api.methods

import com.consulmedics.patientdata.data.api.ApiClient
import com.consulmedics.patientdata.data.api.response.DirectionsApiResponse
import com.consulmedics.patientdata.data.api.response.FetchLocationResponse
import com.consulmedics.patientdata.utils.AppConstants
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionApi {
    @GET(AppConstants.FETCH_DIRECTION_API_ENDPOINT)
    fun fetch(@Query("origin") origin:String, @Query("destination") destination:String,@Query("key") apiKey: String): Call<DirectionsApiResponse>

    companion object {
        fun getApi(): DirectionApi? {
            return ApiClient.googleMapApiClient?.create(DirectionApi::class.java)
        }
    }
}