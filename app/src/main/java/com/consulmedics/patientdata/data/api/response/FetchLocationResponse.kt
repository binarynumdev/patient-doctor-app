package com.consulmedics.patientdata.data.api.response

import com.google.gson.annotations.SerializedName

data class FetchLocationResponse(
    @SerializedName("plus_code")
    var plusCode: PlusCode,
    @SerializedName("results")
    var results: List<FetchResult>
)
data class PlusCode(
    @SerializedName("compound_code")
    var compoundCode: String,
    @SerializedName("global_code")
    var globalCode: String,
)
data class FetchResult(
    @SerializedName("address_components")
    var addressComponents: List<AddressComponent>,
    @SerializedName("formatted_address")
    var formatedAddress: String,
    @SerializedName("geometry")
    var geometry: Geometry
)
data class AddressComponent(
    @SerializedName("long_name")
    var longName: String,
    @SerializedName("short_name")
    var shortName: String,
    @SerializedName("types")
    var types: List<String>,
)
data class Geometry(
    @SerializedName("bounds")
    var bounds: Bounds,
    @SerializedName("location")
    var location: LatLng,
)

data class Bounds(
    @SerializedName("northeast")
    var northeast: LatLng,
    @SerializedName("southwest")
    var southwest: LatLng,
)

data class LatLng(
    @SerializedName("lat")
    var latitude: Double,
    @SerializedName("lng")
    var longitude: Double,
)