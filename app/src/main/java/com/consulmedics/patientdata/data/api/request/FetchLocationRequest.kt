package com.consulmedics.patientdata.data.api.request

import com.google.gson.annotations.SerializedName

data class FetchLocationRequest (
    @SerializedName("latlng")
    var latlng: String,
    @SerializedName("address")
    var address: String,
    @SerializedName("key")
    var apKey: String
)