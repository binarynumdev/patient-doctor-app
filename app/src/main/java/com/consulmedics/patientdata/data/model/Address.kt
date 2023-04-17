package com.consulmedics.patientdata.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
@Entity
data class Address(@PrimaryKey(autoGenerate = true) var uid: Int? = null)
    : Serializable {
    var streetNumber: String = ""
    var streetName: String = ""
    var city: String = ""
    var postCode: String = ""
    var latitute: Double = 0.0
    var longitute: Double = 0.0
    var isHotel: Boolean = true

    override fun toString(): String {
        return "${streetName} ${streetNumber}, ${postCode}, ${city}"
    }
}