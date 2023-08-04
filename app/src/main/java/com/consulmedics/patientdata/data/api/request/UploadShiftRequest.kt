package com.consulmedics.patientdata.data.api.request

import com.consulmedics.patientdata.data.model.Patient
import com.google.gson.annotations.SerializedName

data class UploadShiftRequest(
    @SerializedName("shift_id")
    var shiftId: String,
    @SerializedName("doctor_notes")
    var doctorNotes: String,
    @SerializedName("patients")
    var patients: ArrayList<Patient>,
)
