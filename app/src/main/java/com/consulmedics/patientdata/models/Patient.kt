package com.consulmedics.patientdata.models

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.consulmedics.patientdata.utils.AESEncyption
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
@Entity
data class Patient(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null
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
    var signature:      String  = ""

    var phoneNumber:    String  = ""
    var practiceName:   String  = ""


    var signPatient:    String  = ""
    var startVisitDate: String  = ""
    var startVisitTime: String  = ""
    var startPoint:     String  = ""
    var isSameAddAsPrev:    String  = "N"
    var isAlreadyVisitedDuringThisShift:    String  = "N"


    var dementia:       String   = "N"
    var geriatrics:     String  = "N"
    var infant:         String  = "N"
    var fractures:      String  = "N"
    var serverHandInjury:   String  = "N"
    var thrombosis:     String   = "N"
    var hypertension:   String  = "N"
    var preHeartAttack: String = "N"
    var pneumonia:      String = "N"
    var divertikulitis: String = "N"

    var medicals1:      String = ""
    var medicals2:      String = ""
    var medicals3:      String = ""




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
        if(dateofExam?.isEmpty() == true){
            return false
        }
        if(timeOfExam?.isEmpty() == true){
            return false
        }
        if(killometers?.isEmpty() == true){
            return false
        }
        if(diagnosis?.isEmpty() == true){
            return false
        }
        if(healthStatus?.isEmpty() == true){
            return false
        }
        if(signature?.isEmpty() == true){
            return false
        }
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


}