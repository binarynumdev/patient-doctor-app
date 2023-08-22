package com.consulmedics.patientdata

import androidx.appcompat.app.AppCompatDelegate

class ThemeChanger {
    operator fun invoke(theme: Theme) = change(theme)

    private fun change(theme: Theme) {
        when(theme) {
            Theme.LIGHT_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Theme.DARK_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}