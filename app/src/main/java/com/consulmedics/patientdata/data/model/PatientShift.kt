package com.consulmedics.patientdata.data.model

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.consulmedics.patientdata.data.api.response.LoadShiftApiResponse
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

@Entity(tableName = "patient_shift")
data class PatientShift(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null
): Serializable {
    var startDate: String = ""
    var endDate: String = ""
    var serviceType: Int = 0
    var nameBidding: String = ""
    var additionalRemuneration: Int =0
    var abNumber: Int = 0
    var bonusPayment: Int = 0
    var doctorNote: String = ""
    var doctorID: Int = 0
    var isUploaded: Boolean = false

    fun loadFromApiResponse(shiftDetails: LoadShiftApiResponse.ShiftDetails){

        startDate = convertDateFormat(shiftDetails.startDate)
        endDate = convertDateFormat(shiftDetails.endDate)
        serviceType = shiftDetails.serviceArea.type
        nameBidding = shiftDetails.serviceArea.nameBidding
        additionalRemuneration = shiftDetails.additionalRemuneration
        abNumber = shiftDetails.abNumber
        bonusPayment = shiftDetails.bonusPayment


    }
    fun convertDateFormat(dateString: String): String {
        Log.e(TAG_NAME, "Parse date time")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }

    fun getPatients(): List<Patient> {

        return emptyList()
    }
}