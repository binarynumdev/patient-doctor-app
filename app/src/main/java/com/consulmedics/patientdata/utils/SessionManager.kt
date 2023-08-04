package com.consulmedics.patientdata.utils

import android.content.Context
import android.content.SharedPreferences
import com.consulmedics.patientdata.R

object SessionManager {
    const val API_TOKEN   = "api_token"
    const val USER_ID     = "user_id"
    const val DOCTOR_ID   = "doctor_id"
    const val FIRST_NAME = "first_name"
    const val LAST_NAME = "last_name"
    const val PRIVATE_KEY = "private_key"

    fun savePrivateKey(context: Context, privateKey: String){
        saveString(context, PRIVATE_KEY, privateKey)
    }
    fun getPrivateKey(context: Context): String? {
        return getString(context, PRIVATE_KEY)
    }

    fun saveFirstName(context: Context, firstName: String){
        saveString(context, FIRST_NAME, firstName)
    }
    fun getFirstName(context: Context): String?{
        return getString(context, FIRST_NAME)
    }

    fun saveLastName(context: Context, lastName: String){
        saveString(context, LAST_NAME, lastName)
    }
    fun getLastName(context: Context): String?{
        return getString(context, LAST_NAME)
    }
    fun saveAuthToken(context: Context, token: String){
        saveString(context, API_TOKEN, token)
    }
    fun getToken(context: Context): String? {
        return getString(context, API_TOKEN)
    }

    fun saveUserID(context: Context, userID: String){
        saveString(context, USER_ID, userID)
    }
    fun saveDoctorID(context: Context, doctorID: String){
        saveString(context, DOCTOR_ID, doctorID)
    }
    fun getUserID(context: Context): String?{
        return getString(context, USER_ID)
    }
    fun getDoctorID(context: Context): String?{
        return getString(context, DOCTOR_ID)
    }
    fun saveString(context: Context, key: String, value: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()

    }

    fun getString(context: Context, key: String): String? {
        val prefs: SharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
        return prefs.getString(key, null)
    }

    fun clearData(context: Context){
        val editor = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()
    }


}