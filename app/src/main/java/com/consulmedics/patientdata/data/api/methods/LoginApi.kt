package com.consulmedics.patientdata.data.api.methods

import com.consulmedics.patientdata.data.api.ApiClient
import com.consulmedics.patientdata.data.api.request.LoginRequest
import com.consulmedics.patientdata.data.api.response.LoginResponse
import com.consulmedics.patientdata.utils.AppConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST(AppConstants.LOGIN_API_ENDPOINT)
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    companion object {
        fun getApi(): LoginApi? {
            return ApiClient.client?.create(LoginApi::class.java)
        }
    }
}