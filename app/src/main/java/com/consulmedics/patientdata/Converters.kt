package com.consulmedics.patientdata

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class Converters {
    @TypeConverter
    fun dateToString(date: Date?): String?{
        val birthDateFormat = SimpleDateFormat("yyyyMMdd")
        if(date == null){
            return ""
        }
        return birthDateFormat.format(date)
    }
    @TypeConverter
    fun stringToDate(string: String?): Date?{
        val formatter = SimpleDateFormat("yyyyMMdd")
        if(string.isNullOrEmpty()){
            return null
        }
        return formatter.parse(string)
    }
    fun stringToTime(timeStr: String?): Date? {
        val timeFormat = SimpleDateFormat("HH:mm")
        if(timeStr.isNullOrEmpty()){
            return null
        }
        return timeFormat.parse(timeStr)
    }
    fun timeToString(date:Date?): String?{
        val timeFormat = SimpleDateFormat("HH:mm")
        if(date == null){
            return ""
        }
        return timeFormat.format(date)
    }

    fun dateToFormatedString(date: Date?): String?{
        val timeFormat = SimpleDateFormat("dd.MM.yyyy")
        if(date == null){
            return  ""
        }
        return timeFormat.format(date)
    }
    fun dateToFormatedString(dateStr: String, originPattern: String, outputPattern: String): String?{
        val formatter = SimpleDateFormat(originPattern)
        val dateTime = formatter.parse(dateStr)
        val outputFormatter = SimpleDateFormat(outputPattern)
        return outputFormatter.format(dateTime)
    }
}