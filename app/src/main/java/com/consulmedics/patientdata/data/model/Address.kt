package com.consulmedics.patientdata.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.consulmedics.patientdata.utils.RSAEncryptionHelper
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

    fun clone(): Address? {
        this.uid = null
        return this
    }

    fun encrypt(rsaPrivateKey: String): String? {
        return RSAEncryptionHelper.encryptStringWithPrivateKey(this.toJsonString(), rsaPrivateKey)
    }
    fun toJsonString(): String{
        return "{streetNumber: '${streetNumber}', streetName: '${streetName}', city: '${city}', posCode: '${postCode}', latitute:'${latitute}',longitute: ${longitute} , isHotel:'${isHotel}' }"
    }
}