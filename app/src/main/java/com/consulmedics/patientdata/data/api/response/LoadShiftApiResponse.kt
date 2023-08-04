package com.consulmedics.patientdata.data.api.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoadShiftApiResponse(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("message")
    var message: String,
    @SerializedName("data")
    var shiftList: List<ShiftDetails>,
) {
    class ShiftDetails(
        @SerializedName("id")
        var id: Int,
        @SerializedName("start_date")
        var startDate: String,
        @SerializedName("end_date")
        var endDate: String,
        @SerializedName("additional_remuneration")
        var additionalRemuneration: Int,
        @SerializedName("ab")
        var abNumber: Int,
        @SerializedName("bonus_payment")
        var bonusPayment: Int,
        @SerializedName("service_area_id")
        var serviceAreaId: Int,
        @SerializedName("service_area")
        var serviceArea: ServiceArea,
    ) {
        class ServiceArea (
            @SerializedName("type")
            var type: Int,
            @SerializedName("name_bidding")
            var nameBidding: String
        )
    }
}
