package com.consulmedics.patientdata.models

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.Serializable

class Patient : Serializable {
    var personalData: String = ""
    var insuredData: String = ""
    fun loadFrom(readPDResponse: String?, readVDResponse: String?) {
        var factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(personalData.reader())
        var eventType = parser.eventType
        while(eventType != XmlPullParser.END_DOCUMENT){
            val tagname = parser.name
            when(eventType){
                XmlPullParser.START_TAG -> if (tagname.equals("employee", ignoreCase = true)){

                }
            }
        }
    }
    fun loadExample(){
        var xmlStr: String = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\" standalone=\"yes\"?>\n" +
                "<vsdp:UC_PersoenlicheVersichertendatenXML CDM_VERSION=\"5.2.0\"\n" +
                "\txmlns:vsdp=\"http://ws.gematik.de/fa/vsdm/vsd/v5.2\">\n" +
                "\t<vsdp:Versicherter>\n" +
                "\t\t<vsdp:Versicherten_ID>T115774582</vsdp:Versicherten_ID>\n" +
                "\t\t<vsdp:Person>\n" +
                "\t\t\t<vsdp:Geburtsdatum>19800713</vsdp:Geburtsdatum>\n" +
                "\t\t\t<vsdp:Vorname>Claudia</vsdp:Vorname>\n" +
                "\t\t\t<vsdp:Nachname>Burdack</vsdp:Nachname>\n" +
                "\t\t\t<vsdp:Geschlecht>W</vsdp:Geschlecht>\n" +
                "\t\t\t<vsdp:StrassenAdresse>\n" +
                "\t\t\t\t<vsdp:Postleitzahl>01187</vsdp:Postleitzahl>\n" +
                "\t\t\t\t<vsdp:Ort>Dresden</vsdp:Ort>\n" +
                "\t\t\t\t<vsdp:Land>\n" +
                "\t\t\t\t\t<vsdp:Wohnsitzlaendercode>D</vsdp:Wohnsitzlaendercode>\n" +
                "\t\t\t\t</vsdp:Land>\n" +
                "\t\t\t\t<vsdp:Strasse>Bayreuther Str.</vsdp:Strasse>\n" +
                "\t\t\t\t<vsdp:Hausnummer>30</vsdp:Hausnummer>\n" +
                "\t\t\t</vsdp:StrassenAdresse>\n" +
                "\t\t</vsdp:Person>\n" +
                "\t</vsdp:Versicherter>\n" +
                "</vsdp:UC_PersoenlicheVersichertendatenXML>"
//        xmlStr.replace("vsdp:", "")
        var factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
        parser.setInput(xmlStr.reader())
        var tag: String?
        var text = ""
        var eventType = parser.eventType
        while(eventType != XmlPullParser.END_DOCUMENT){
            tag = parser.name
//            Log.e("TAG_NAME", parser.name + "<:>" + parser.text + "---")
            when(eventType){
                XmlPullParser.START_TAG -> if (tag.equals("employee", ignoreCase = true)){

                }
                XmlPullParser.TEXT -> text = parser.text
                XmlPullParser.END_TAG -> when (tag) {
                    "Versicherten_ID" -> { //Insured_ID
                        Log.e("Insured_ID", text)
                    }
                    "Geburtsdatum" -> {//Date of birth
                        Log.e("Date of birth", text)
                    }
                    "Vorname" -> {//First name
                        Log.e("First name", text)
                    }
                    "Nachname" -> {//Surname
                        Log.e("Surname", text)
                    }
                    "Geschlecht" -> {//gender
                        Log.e("gender", text)
                    }
                    "Postleitzahl" -> {//postal code
                        Log.e("postal code", text)
                    }
                    "Ort" -> {//location
                        Log.e("location", text)
                    }
                    "Wohnsitzlaendercode" -> {//Country of Residence Code
                        Log.e("Country of Residence Code", text)
                    }
                    "Strasse" -> {//Street
                        Log.e("Street", text)
                    }
                    "Hausnummer" -> {//House number
                        Log.e("House number", text)
                    }
                }
            }
            eventType = parser.next()
        }
    }


}