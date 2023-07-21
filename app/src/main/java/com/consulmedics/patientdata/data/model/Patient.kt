package com.consulmedics.patientdata.data.model

import android.util.Log
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.consulmedics.patientdata.utils.AESEncyption
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.utils.RSAEncryptionHelper
import com.google.gson.annotations.SerializedName
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
@Entity
data class Patient(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null, var target: String? = "visit"
): Serializable {
    var patientID:      String? = ""
    var firstName:      String  = ""
    var lastName:       String  = ""
    var birthDate:      Date?    = null
    var street:         String  = ""
    var city:           String  = ""
    var postCode:       String  = ""
    var gender:         String  = ""
    var houseNumber:    String  = ""
    var insuranceNumber:String  = ""
    var insuranceName:  String  = ""
    var insuranceStatus:String  = ""

    var dateofExam:     String  = ""
    var timeOfExam:     String  = ""
    var killometers:    String  = ""
    var diagnosis:      String  = ""
    var healthStatus:   String  = ""
    @SerializedName("doctor_sign")
    var signature:      String  = ""

    var phoneNumber:    String  = ""
    var practiceName:   String  = ""

    @SerializedName("patient_sign")
    var signPatient:    String  = ""
    var startVisitDate: Date?  = null
    var startVisitTime: String  = ""
    var startPoint:     String  = ""

    var sameAddAsPrev:    Boolean  = false
    var alreadyVisitedDuringThisShift:    Boolean  = false


    var dementia:       Boolean  = false
    var geriatrics:     Boolean  = false
    var infant:         Boolean  = false
    var fractures:      Boolean  = false
    var serverHandInjury:   Boolean  = false
    var thrombosis:     Boolean  = false
    var hypertension:   Boolean  = false
    var preHeartAttack: Boolean  = false
    var pneumonia:      Boolean  = false
    var divertikulitis: Boolean  = false

    var medicals1:      String = ""
    var medicals2:      String = ""
    var medicals3:      String = ""
    var receiptFirstName: String = ""
    var receiptLastName: String = ""
    var receiptAdditionalInfo: String = ""
    var startAddress:   Int? = null
    var visitAddress:   Int? = null
    var receiptAddress: Int? = null
    var distance:       Double = 0.00
    var sincVisitAddress:Boolean = false

    @Ignore
    @SerializedName("start_address_details")
    var startAddressDetails: Address? = null
    @Ignore
    @SerializedName("visit_address_details")
    var visitAddressDetails: Address? = null
    @Ignore
    @SerializedName("receipt_address_details")
    var receiptAddressDetails: Address? = null

    fun isValidInsuranceDetails():Boolean{
        if(insuranceName?.isEmpty() == true){
            return false
        }
        if(insuranceNumber?.isEmpty() == true){
            return false
        }
        if(insuranceStatus?.isEmpty() == true){
            return false
        }
        if(signPatient?.isEmpty() == true){
            return false
        }

        return true
    }


    fun isValidatePersonalDetails(): Boolean{
        if(patientID?.isEmpty() == true){
            return false
        }
        if(firstName?.isEmpty() == true){
            return false
        }
        if(lastName?.isEmpty() == true){
            return false
        }
        if(street?.isEmpty() == true){
            return false
        }
        if(houseNumber?.isEmpty() == true){
            return false
        }
        if(city?.isEmpty() == true){
            return false
        }
        if(postCode?.isEmpty() == true){
            return false
        }
        if(birthDate == null){
            return false
        }
        if(gender?.isEmpty() == true){
            return false
        }
        if(phoneNumber?.isEmpty() == true){
            return false
        }
        if(practiceName?.isEmpty() == true){
            return  false
        }
        return true
    }

    fun loadFrom(readPDResponse: String?, readVDResponse: String?) {
        if (readPDResponse != null) {
            loadFromXmlStr(readPDResponse)
        }
        if (readVDResponse != null) {
            loadFromXmlStr(readVDResponse)
        }
    }
    fun loadFromXmlStr(xmlStr:String){
        Log.e("XMLSTR", xmlStr)
        var factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
        parser.setInput(xmlStr.reader())
        var tag: String?
        var text = ""
        var eventType = parser.eventType
        var isInsuranceData: Boolean = false
        while(eventType != XmlPullParser.END_DOCUMENT){
            tag = parser.name
//            Log.e("TAG_NAME", parser.name + "<:>" + parser.text + "---")
            when(eventType){
                XmlPullParser.START_TAG -> if (tag.equals("AbrechnenderKostentraeger", ignoreCase = true)){
                    isInsuranceData = true
                }
                XmlPullParser.TEXT -> text = parser.text
                XmlPullParser.END_TAG -> when (tag) {
                    "Versicherten_ID" -> { //Insured_ID
                        Log.e("Insured_ID", text)
                        patientID = text
                    }
                    "Geburtsdatum" -> {//Date of birth
                        Log.e("Date of birth", text)
                        val formatter = SimpleDateFormat("yyyyMMdd")
                        birthDate = formatter.parse(text)
                    }
                    "Vorname" -> {//First name
                        Log.e("First name", text)
                        firstName = text
                    }
                    "Nachname" -> {//Surname
                        Log.e("Surname", text)
                        lastName = text
                    }
                    "Geschlecht" -> {//gender
                        Log.e("gender", text)
                        gender = text
                    }
                    "Postleitzahl" -> {//postal code
                        Log.e("postal code", text)
                        postCode = text
                    }
                    "Ort" -> {//location
                        Log.e("location", text)
                        city = text
                    }
                    "Wohnsitzlaendercode" -> {//Country of Residence Code
                        Log.e("Country of Residence Code", text)
                    }
                    "Strasse" -> {//Street
                        Log.e("Street", text)
                        street = text
                    }
                    "Hausnummer" -> {//House number
                        Log.e("House number", text)
                        houseNumber = text
                    }

                    "Kostentraegerkennung" ->{
                        if(isInsuranceData){
                            insuranceNumber = text
                        }
                    }
                    "Name" ->{
                        if(isInsuranceData){
                            insuranceName = text
                        }
                    }
                    "Versichertenart" ->{
                        insuranceStatus = text
                    }
                }
            }
            eventType = parser.next()
        }
    }

    fun isValidAdditionalDetails(): Boolean {

        return true
    }

    fun encryptFields() {
        /*
        var street:         String  = ""
        var city:           String  = ""
        var postCode:       String  = ""
        var gender:         String  = ""
        var houseNumber:    String  = ""
        var insuranceNumber:String  = ""
        var insuranceName:  String  = ""
        var insuranceStatus:String  = ""

        var dateofExam:     String  = ""
        var timeOfExam:     String  = ""
        var killometers:    String  = ""
        var diagnosis:      String  = ""
        var healthStatus:   String  = ""
        var signature:      String  = ""
        *
        */
        patientID = AESEncyption.encrypt(patientID!!).toString();
        street = AESEncyption.encrypt(street).toString();
        city = AESEncyption.encrypt(city).toString();
        postCode = AESEncyption.encrypt(postCode).toString();
        gender = AESEncyption.encrypt(gender).toString();
        houseNumber = AESEncyption.encrypt(houseNumber).toString();
        insuranceNumber = AESEncyption.encrypt(insuranceNumber).toString();
        insuranceName = AESEncyption.encrypt(insuranceName).toString();
        insuranceStatus = AESEncyption.encrypt(insuranceStatus).toString();
    }

    fun decryptFields() {
        try {
            patientID = AESEncyption.decrypt(patientID!!).toString().trim();
            street = AESEncyption.decrypt(street).toString().trim();
            city = AESEncyption.decrypt(city).toString().trim();
            postCode = AESEncyption.decrypt(postCode).toString().trim();
            gender = AESEncyption.decrypt(gender).toString().trim();
            houseNumber = AESEncyption.decrypt(houseNumber).toString().trim();
            insuranceNumber = AESEncyption.decrypt(insuranceNumber).toString().trim();
            insuranceName = AESEncyption.decrypt(insuranceName).toString().trim();
            insuranceStatus = AESEncyption.decrypt(insuranceStatus).toString().trim();
        }
        catch (e: Exception){
            Log.e(TAG_NAME, e.toString())
        }

    }

    fun isValidLogisticDetails() : Boolean{
        if(startVisitDate == null){
            return false
        }
        if(startVisitTime?.isEmpty() == true){
            return false
        }
        if(startPoint?.isEmpty() == true){
            return false
        }

        return true
    }

    fun isValidDoctorDocument(): Boolean? {
        if(diagnosis?.isEmpty() == true){
            return false
        }
        if(healthStatus?.isEmpty() == true){
            return false
        }
        return true
    }

    fun isValidMedicalReceipt(): Boolean? {
        if(medicals1?.isEmpty() == true){
            return false
        }
        if(medicals2?.isEmpty() == true){
            return false
        }
        if(medicals3?.isEmpty() == true){
            return false
        }
        return true
    }

    fun isFullyValidated(): Boolean {
        if(target.equals("call")){
            if(firstName.isNotEmpty() && lastName.isNotEmpty() && phoneNumber.isNotEmpty()){
                return true
            }
        }
        else{
            if(isValidMedicalReceipt() == true && isValidatePersonalDetails() && isValidAdditionalDetails() && isValidLogisticDetails() && isValidDoctorDocument() == true && isValidMedicalReceipt() == true)
                return true
        }

        return false
    }

    fun encryptToSubmit(rsaPrivateKey: String) {
        firstName = RSAEncryptionHelper.encryptStringWithPrivateKey(firstName!!, rsaPrivateKey)
        lastName = RSAEncryptionHelper.encryptStringWithPrivateKey(lastName, rsaPrivateKey)
//        birthDate = RSAEncryptionHelper.encryptStringWithPrivateKey(city, rsaPrivateKey)
        street = RSAEncryptionHelper.encryptStringWithPrivateKey(street, rsaPrivateKey)
        city = RSAEncryptionHelper.encryptStringWithPrivateKey(city, rsaPrivateKey)
        houseNumber = RSAEncryptionHelper.encryptStringWithPrivateKey(houseNumber, rsaPrivateKey)
        postCode = RSAEncryptionHelper.encryptStringWithPrivateKey(postCode, rsaPrivateKey)
//        insuranceStatus = RSAEncryptionHelper.encryptStringWithPrivateKey(insuranceStatus, rsaPrivateKey)
    }


}