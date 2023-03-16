package com.consulmedics.patientdata.utils

import android.content.Context
import android.content.SharedPreferences
import com.consulmedics.patientdata.R

object SessionManager {
    const val API_TOKEN   = "api_token"

    fun saveAuthToken(context: Context, token: String){
        saveString(context, API_TOKEN, token)
    }
    fun getToken(context: Context): String? {
        return getString(context, API_TOKEN)
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
        return prefs.getString(this.API_TOKEN, null)
    }

    fun clearData(context: Context){
        val editor = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()
    }
}