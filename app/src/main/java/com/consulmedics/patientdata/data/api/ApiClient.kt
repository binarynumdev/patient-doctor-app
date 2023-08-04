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
    }
    val client: Retrofit?
        get() {
            if(mRetrofit == null){
                mRetrofit = Retrofit.Builder()
                    .baseUrl(AppConstants.BACKEND_BASE_URL)
                    .client(mOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
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