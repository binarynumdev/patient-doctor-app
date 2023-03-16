package com.consulmedics.patientdata.repository

import com.consulmedics.patientdata.data.api.methods.LoginApi
import com.consulmedics.patientdata.data.api.request.LoginRequest
import com.consulmedics.patientdata.data.api.response.LoginResponse
import retrofit2.Response

class UserRepository {
    suspend fun loginUser(loginRequest: LoginRequest): Response<LoginResponse>?{
        return LoginApi.getApi()?.login(loginRequest = loginRequest)
    }
}