package com.consulmedics.patientdata.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Hotel(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null
): Serializable {
    var hotelName: String = ""
    var address: String = ""
    var city: String = ""
}
