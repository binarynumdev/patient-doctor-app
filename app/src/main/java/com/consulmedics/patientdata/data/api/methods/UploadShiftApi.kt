package com.consulmedics.patientdata.data.api.methods

import android.util.Log
import com.consulmedics.patientdata.data.api.ApiClient
import com.consulmedics.patientdata.data.api.request.LoginRequest
import com.consulmedics.patientdata.data.api.request.UploadShiftRequest
import com.consulmedics.patientdata.data.api.response.LoginResponse
import com.consulmedics.patientdata.data.api.response.UploadShiftApiResponse
import com.consulmedics.patientdata.utils.AppConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UploadShiftApi {
    @POST(AppConstants.UPLOAD_SHIFT_ENDPOINT)
    suspend fun upload(@Body loginRequest: UploadShiftRequest): Response<UploadShiftApiResponse>
    companion object {
        fun getApi(): UploadShiftApi? {
            Log.e("Api", "arrived")
            var temp :  UploadShiftApi? = null
            try {
                temp = ApiClient.client?.create(UploadShiftApi::class.java)
            }
            catch (e: AssertionError) {
                Log.e("Upload", "Failed")
            }
            return temp
        }
    }
}