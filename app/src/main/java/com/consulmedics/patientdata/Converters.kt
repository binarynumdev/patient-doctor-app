package com.consulmedics.patientdata

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class Converters {
    @TypeConverter
    fun dateToString(date: Date?): String?{
        val birthDateFormat = SimpleDateFormat("yyyyMMdd")
        return birthDateFormat.format(date)
    }
    @TypeConverter
    fun stringToDate(string: String?): Date?{
        val formatter = SimpleDateFormat("yyyyMMdd")
        return formatter.parse(string)
    }

    fun stringToTime(timeStr: String): Date {
        val timeFormat = SimpleDateFormat("HH:mm")
        return timeFormat.parse(timeStr)
    }

    fun timeToString(date:Date): String{
        val timeFormat = SimpleDateFormat("HH:mm")
        return timeFormat.format(date)
    }
}