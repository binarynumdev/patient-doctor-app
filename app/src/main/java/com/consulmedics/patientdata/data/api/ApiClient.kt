package com.consulmedics.patientdata.data.api

import android.util.Log
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.DateDeserializer
import com.consulmedics.patientdata.utils.DateSerializer
import com.consulmedics.patientdata.utils.SessionManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import okhttp3.Request

object ApiClient {
    var mHttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    var mOkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(mHttpLoggingInterceptor)
        .build()

    var mRetrofit: Retrofit? = null
    var gRetrofit: Retrofit? = null
    var authRetrofit: Retrofit? = null

    val gson = GsonBuilder().registerTypeAdapter(Date::class.java, DateSerializer()).registerTypeAdapter(Date::class.java, DateDeserializer()).create()
    fun setBearerToken(token: String) {
        Log.e("API_CLIENT_TOKEN", token)
        val authInterceptor = OkHttpClient.Builder().addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Authorization", "Bearer $token")
            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()
        mOkHttpClient = authInterceptor
        Log.e("it contains BearToken", hasBearerToken(mOkHttpClient).toString())
    }


    fun hasBearerToken(client: OkHttpClient): Boolean {
        // Create a dummy request to test the interceptors
        val testRequest = Request.Builder().url("http://example.com").build()

        for (interceptor in client.interceptors) {
            val modifiedRequest = interceptor.intercept(object : okhttp3.Interceptor.Chain {
                override fun request(): Request = testRequest
                override fun proceed(request: Request): okhttp3.Response {
                    return okhttp3.Response.Builder()
                        .request(request)
                        .protocol(okhttp3.Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .build()
                }
                override fun connection() = null
                override fun call() = client.newCall(testRequest)
                override fun connectTimeoutMillis() = 0
                override fun withConnectTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit) = this
                override fun readTimeoutMillis() = 0
                override fun withReadTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit) = this
                override fun writeTimeoutMillis() = 0
                override fun withWriteTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit) = this
            }).request

            val authHeader = modifiedRequest.header("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return true
            }
        }

        return false
    }

    val client: Retrofit?
        get() {
            mRetrofit = Retrofit.Builder()
                .baseUrl(AppConstants.BACKEND_BASE_URL)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            return mRetrofit
        }

    val googleMapApiClient: Retrofit?
        get() {
            if(gRetrofit == null){
                gRetrofit = Retrofit.Builder()
                    .baseUrl(AppConstants.GOOGLE_MAP_API_ENDPOINT)
                    .client(mOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return gRetrofit
        }
}