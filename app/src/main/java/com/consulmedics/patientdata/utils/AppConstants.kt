package com.consulmedics.patientdata.utils

object AppConstants {
    const val TAG_NAME: String = "Consulmedics Patient Data"
    const val PERMISSION_REQUEST_CODE: Int = 101
    const val ADMIN_PASSWORD: String = "6PXNxaZhup+JRQrJlM4KnQ=="
    const val CERT_FILE_PATH: String = "Consulmedics/userinfo.cert";
    const val CERT_FILE_FOLDER: String = "/Consulmedics"
    const val CERT_FILE_NAME: String = "userinfo.cert"
    const val DISPLAY_DATE_FORMAT: String = "dd.MM.yyyy"
    const val PREV_PATIENT_TEXT : String = "PrevPatient"
    const val HOTEL_TEXT: String = "Hotel"
    const val YES_TEXT:String =  "Y"
    const val NO_TEXT: String = "N"
    const val MAX_LINE_MEDICALS: Int = 2
    const val MAX_LEN_MEDICALS: Int = 46
    const val MAX_LEN_PER_LINE: Int = 23


    const val BACKEND_BASE_URL: String = "https://admin-test.bd365.de/"
    const val LOGIN_API_ENDPOINT: String = "api/login-to-mobile-app"
    const val SYNC_PATIENT_ENDPOINT: String = "api/v1/sync-patient-data"
    const val LOAD_MISSIONS_ENDPOINT: String = "api/v1/my-missions"

    const val GOOGLE_MAP_API_ENDPOINT: String = "https://maps.googleapis.com/"
    const val FETCH_ADDRESS_FROM_LOCATION_ENDPOINT: String = "maps/api/geocode/json"
    const val FETCH_DIRECTION_API_ENDPOINT: String = "maps/api/directions/json"


    const val PHONE_CALL_MODE: String = "phone_call"
    const val PATIENT_MODE: String = "patient_mode"
    const val PATIENT_DATA: String = "patient_data"

    const val UPCOMING_TABS: String = "upcoming"
    const val PAST_TABS: String = "past"

    const val PATIENT_SHIFT_DATA: String = "patient_shift_data"

}