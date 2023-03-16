package com.consulmedics.patientdata.data.api.response

import com.google.gson.annotations.SerializedName

data class SyncPatientResponse (@SerializedName("result")
                                var patients: String)