package com.consulmedics.patientdata.data.api.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoadShiftApiResponse(
    @SerializedName("data")
    var shiftList: List<ShiftDetails>,
) {
    class ShiftDetails(
        @SerializedName("id")
        var id: String,
        @SerializedName("start_date")
        var startDate: String,
        @SerializedName("end_date")
        var endDate: String,
        @SerializedName("service_area")
        var serviceArea: ServiceArea,
    ) {
        class ServiceArea (
            @SerializedName("type")
            var type: String,
        )
    }
}
