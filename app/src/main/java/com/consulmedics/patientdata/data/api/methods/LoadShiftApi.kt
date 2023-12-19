package com.consulmedics.patientdata.data.api.methods

import com.consulmedics.patientdata.data.api.ApiClient
import com.consulmedics.patientdata.data.api.response.LoadShiftApiResponse
import com.consulmedics.patientdata.utils.AppConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

interface LoadShiftApi {
    @GET(AppConstants.LOAD_MISSIONS_ENDPOINT)
    suspend fun load(): Response<LoadShiftApiResponse>

    companion object {
        fun getApi(): LoadShiftApi? {
            return ApiClient.client?.create(LoadShiftApi::class.java)
        }
    }
}