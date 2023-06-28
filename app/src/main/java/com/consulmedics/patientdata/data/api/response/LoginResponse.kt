package com.consulmedics.patientdata.data.api.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("first_name")
    var first_name: String,
    @SerializedName("last_name")
    var last_name: String,
    @SerializedName("api_token")
    var api_token: String,
    @SerializedName("private_key")
    var private_key: String,
    @SerializedName("user_id")
    var userID: String,
    @SerializedName("doctor_id")
    var doctorID: String



)
