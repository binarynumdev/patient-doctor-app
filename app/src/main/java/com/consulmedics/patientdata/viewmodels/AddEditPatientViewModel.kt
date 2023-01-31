package com.consulmedics.patientdata.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.consulmedics.patientdata.models.Patient
import java.text.SimpleDateFormat
import java.util.*

class AddEditPatientViewModel: ViewModel() {
/*
*     var patientID:      String? = ""
    var firstName:      String  = ""
    var lastName:       String  = ""
    var birthDate: Date = Date()
    var street:         String  = ""
    var city:           String  = ""
    var postCode:       String  = ""
    var gender:         String  = ""
    var houseNumber:    String  = ""
    var insuranceNumber:String  = ""
    var insuranceName:  String  = ""
    var insuranceStatus:String  = ""
* */
    private val _patientID = MutableLiveData<String>("")
    val patientID: LiveData<String> = _patientID;
    fun setPatientID(editValue: String){
//        _patientID.value = editValue
        _patientData.value?.patientID = editValue
    }

    private val _firstName = MutableLiveData<String>("")
    val firstName: LiveData<String> = _firstName;
    fun setFirstname(editValue: String){
        _patientData.value?.firstName = editValue
    }

    private val _lastName = MutableLiveData<String>("")
    val lastName: LiveData<String> = _lastName;
    fun setLastname(editValue: String){
        _patientData.value?.lastName = editValue
    }

    private val _birthDate = MutableLiveData<String>("")
    val birthDate: LiveData<String> = _birthDate;
    fun setBirthDate(editValue: String){
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        _patientData.value?.birthDate = formatter.parse(editValue)
    }
    private val _street = MutableLiveData<String>("")
    val street: LiveData<String> = _street;
    fun setStreet(editValue: String){
        _patientData.value?.street = editValue
    }
    private val _city = MutableLiveData<String>("")
    val city: LiveData<String> = _city;
    fun setCity(editValue: String){
        _patientData.value?.city = editValue
    }
    private val _postCode = MutableLiveData<String>("")
    val postCode: LiveData<String> = _postCode;
    fun setPostCode(editValue: String){
        _patientData.value?.postCode = editValue
    }
    private val _gender = MutableLiveData<String>("")
    val gender: LiveData<String> = _gender;
    fun setGender(editValue: String){
        _patientData.value?.gender = editValue
    }
    private val _houseNumber = MutableLiveData<String>("")
    val houseNumber: LiveData<String> = _houseNumber;
    fun setHouseNumber(editValue: String){
        _patientData.value?.houseNumber = editValue
    }
    private val _insuranceNumber = MutableLiveData<String>("")
    val insuranceNumber: LiveData<String> = _insuranceNumber;
    fun setInsuranceNumber(editValue: String){
        _insuranceNumber.value = editValue
    }
    private val _insuranceName = MutableLiveData<String>("")
    val insuranceName: LiveData<String> = _insuranceName;
    fun setInsuranceName(editValue: String){
        _insuranceName.value = editValue
    }
    private val _insuranceStatus = MutableLiveData<String>("")
    val insuranceStatus: LiveData<String> = _insuranceStatus;
    fun setInsuranceStatus(editValue: String){
        _insuranceStatus.value = editValue
    }

    private val _patientData = MutableLiveData<Patient>()
    val patientData: LiveData<Patient> = _patientData;

    fun setPatientData(patient: Patient) {
        _patientData.value = patient
//        patient?.patientID?.let { setPatientID(it) }
//        patient?.firstName?.let { setFirstname(it) }
//        patient?.lastName?.let { setLastname(it) }
//        patient?.gender?.let { setGender(it) }
//        patient?.birthDate?.let {
//            val birthDateFormat = SimpleDateFormat("dd.MM.yyyy")
//            setBirthDate(birthDateFormat.format(it))
//        }
//        patient?.street?.let { setStreet(it) }
//        patient?.postCode?.let { setPostCode(it) }
//        patient?.houseNumber?.let { setHouseNumber(it) }
//
//        patient?.insuranceName?.let { setInsuranceName(it) }
//        patient?.insuranceNumber?.let { setInsuranceNumber(it) }
//        patient?.insuranceStatus?.let { setInsuranceStatus(it) }
    }
}