package com.consulmedics.patientdata.data.api.response

import com.google.gson.annotations.SerializedName

data class UploadShiftApiResponse (
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("message")
    var message: String
)