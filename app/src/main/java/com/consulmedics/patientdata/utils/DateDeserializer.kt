package com.consulmedics.patientdata.utils
import com.google.gson.*
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateDeserializer : JsonDeserializer<Date> {
    private val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US) // Define your desired date format here

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        val dateString = json?.asString
        return try {
            dateFormat.parse(dateString)
        } catch (e: ParseException) {
            throw JsonParseException("Error parsing date: $dateString", e)
        }
    }
}
