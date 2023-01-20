package com.consulmedics.patientdata.models

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date

class Patient : Serializable {
    var personalData:   String  = ""
    var insuredData:    String  = ""
    var firstName:      String  = ""
    var lastName:       String  = ""
    var birthDate:      Date    = Date()
    var street:         String  = ""
    var city:           String  = ""
    var postCode:       String  = ""
    var patientID:      String  = ""
    var gender:         String  = ""
    var houseNumber:    String  = ""
    var insuranceNumber:String  = ""
    var insuranceName:  String  = ""
    var insuranceStatus:String  = ""
    fun loadFrom(readPDResponse: String?, readVDResponse: String?) {
        var factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(personalData.reader())
        var eventType = parser.eventType
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


}