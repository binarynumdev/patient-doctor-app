package com.consulmedics.patientdata.data.api.request

import com.consulmedics.patientdata.data.model.Patient
import com.google.gson.annotations.SerializedName

data class SyncPatientRequest (
    @SerializedName("patients")
    var patients: List<Patient>
)