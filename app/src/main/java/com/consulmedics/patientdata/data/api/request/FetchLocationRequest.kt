package com.consulmedics.patientdata.data.api.request

import com.google.gson.annotations.SerializedName

data class FetchLocationRequest (
    @SerializedName("latlng")
    var latlng: String,
    @SerializedName("key")
    var apKey: String
)