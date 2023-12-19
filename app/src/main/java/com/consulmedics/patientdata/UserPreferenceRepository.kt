package com.consulmedics.patientdata

import android.content.Context

class UserPreferenceRepository (context: Context){
    private val sharedPreferences = context.applicationContext.getSharedPreferences("AppSettingsPrefs", Context.MODE_PRIVATE)

    val appTheme: Theme
        get() {
            val theme = sharedPreferences.getString("pref_name_theme_mode", Theme.LIGHT_MODE.name)
            return Theme.valueOf(theme ?: Theme.LIGHT_MODE.name)
        }

    fun updateTheme(theme: Theme) {
        sharedPreferences.edit()
            .putString("pref_name_theme_mode", theme.name)
            .apply()

        ThemeChanger().invoke(theme)
    }

    companion object{
        @Volatile
        private var INSTANCE: UserPreferenceRepository? = null

        fun getInstance(context: Context): UserPreferenceRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }
                val instance = UserPreferenceRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}