package com.consulmedics.patientdata.data.api.response

import com.google.gson.annotations.SerializedName

data class DirectionsApiResponse(
    @SerializedName("routes")
    val routes: List<Route>
)
data class Route(
    @SerializedName("legs")
    val legs: List<Leg>
)
data class Leg(
    @SerializedName("distance")
    val distance: DistanceValue
)
data class DistanceValue(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int
)