package com.consulmedics.patientdata.viewmodels

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.consulmedics.patientdata.MyAppDatabase
import com.consulmedics.patientdata.SCardExt
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.data.api.response.PDFGenerateResponse
import com.consulmedics.patientdata.data.model.Address
import com.consulmedics.patientdata.data.model.Hotel
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.repository.AddressRepository
import com.consulmedics.patientdata.repository.HotelRepository
import com.consulmedics.patientdata.repository.PatientRepository
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class AddEditPatientViewModel(private val patientRepository: PatientRepository, private val hotelRepository: HotelRepository, private val addressRepository: AddressRepository): ViewModel() {
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
    val scardLib = SCardExt()
    var googleMapApiKey = ""
    private val _patientData = MutableLiveData<Patient>()
    val patientData: LiveData<Patient> = _patientData
    val hotelList : LiveData<List<Address>>
    var previousPatients:LiveData<List<Patient>>
    private val _startAddress = MutableLiveData<Address>()
    var startAddress: LiveData<Address> = _startAddress
    private val _visitAddress = MutableLiveData<Address>()
    var visitAddress: LiveData<Address> = _visitAddress
    private val _receiptAddress = MutableLiveData<Address>()
    var receiptAddress: LiveData<Address> = _receiptAddress
//    private val _printResult = MutableLiveData<BaseResponse<PDFGenerateResponse>>()
    var printResult: MutableLiveData<BaseResponse<PDFGenerateResponse>> = MutableLiveData()

    init {
        hotelList = addressRepository.hotelList
        previousPatients = patientRepository.previousPatients(null)
        if(_patientData.value?.startAddress != null){
            _startAddress.value = addressRepository.find(_patientData.value?.startAddress)
        }
        else{
            _startAddress.value = Address()
        }

        if(_patientData.value?.visitAddress != null){
            _visitAddress.value = addressRepository.find(_patientData.value?.visitAddress)
        }
        else{
            _visitAddress.value = Address()
        }
        if(_patientData.value?.receiptAddress != null){
            Log.e(TAG_NAME, "Load Receipt Address")
            _receiptAddress.value = addressRepository.find(_patientData.value?.receiptAddress)
        }
        else{
            _receiptAddress.value = Address()
        }
    }
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isFinished = MutableLiveData<Boolean>(false)
    val isFinished: LiveData<Boolean> = _isFinished
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
    fun setBirthDate(editValue: Date){
//        val formatter = SimpleDateFormat("dd.MM.yyyy")
        _patientData.value?.birthDate = editValue
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
        _patientData.value?.insuranceNumber = editValue
    }
    private val _insuranceName = MutableLiveData<String>("")
    val insuranceName: LiveData<String> = _insuranceName;
    fun setInsuranceName(editValue: String){
        _patientData.value?.insuranceName = editValue
    }
    private val _insuranceStatus = MutableLiveData<String>("")
    val insuranceStatus: LiveData<String> = _insuranceStatus;
    fun setInsuranceStatus(editValue: String){
        _patientData.value?.insuranceStatus = editValue
    }



    fun setPatientData(patient: Patient) {
        _patientData.value = patient
        previousPatients = patientRepository.previousPatients(patient.uid)
        if(patient.startAddress != null){
            _startAddress.value = addressRepository.find(patient.startAddress)
            Log.e(TAG_NAME, "Start Address : ${patient.startAddress}")
        }
        else{
            _startAddress.value = Address()
        }

        if(patient.visitAddress != null){
            Log.e(TAG_NAME, "Visit Address : ${patient.visitAddress}")
            _visitAddress.value = addressRepository.find(patient.visitAddress)
            Log.e(TAG_NAME, "Visit Address : ${visitAddress.value?.uid}")
        }
        else{
            _visitAddress.value = Address()
        }
        if(patient.receiptAddress != null){
            Log.e(TAG_NAME, "Visit Address : ${patient.receiptAddress}")
            _receiptAddress.value = addressRepository.find(patient.receiptAddress)
            Log.e(TAG_NAME, "Visit Address : ${receiptAddress.value?.uid}")
        }
        else{
            _receiptAddress.value = Address()
        }
    }

    fun setKillometer(editValue: String) {
        _patientData.value?.killometers = editValue
    }

    fun setDiagnosis(editValue: String) {
        _patientData.value?.diagnosis = editValue
    }

    fun setHealthStatus(editValue: String) {
        _patientData.value?.healthStatus = editValue
    }

    fun setDateOfExam(editValue: String){
        _patientData.value?.dateofExam = editValue
    }

    fun setTimeOfExam(editValue: String){
        _patientData.value?.timeOfExam = editValue
    }



    fun setSignature(signatureSvg: String?) {
        if (signatureSvg != null) {
            _patientData.value?.signature = signatureSvg
        }
    }

    fun setSignPatient(signatureSvg: String?){
        if (signatureSvg != null) {
            _patientData.value?.signPatient = signatureSvg
        }
    }
    fun updatePatient(patient: Patient) = viewModelScope.launch(Dispatchers.IO) {
        patientRepository.update(patient)
    }


    // on below line we are creating a new method for adding a new note to our database
    // we are calling a method from our repository to add a new note.
    fun insertPatient(patient: Patient) = viewModelScope.launch(Dispatchers.IO) {
        patientRepository.insert(patient)
    }
    suspend fun calculateDistance(){
        var tmpPatient = patientData.value
        Log.e(TAG_NAME, "Calculate Distance: ${visitAddress.value!!.longitute}")
        if(visitAddress.value != null && startAddress.value != null){
            if(visitAddress.value!!.longitute == 0.00 || visitAddress.value!!.latitute == 0.00 || startAddress.value!!.longitute == 0.00 || startAddress.value!!.latitute == 0.00 ){
                tmpPatient?.distance = 0.00
            }
            else{

                tmpPatient?.distance = addressRepository.calculateDistance(startAddress.value!!, visitAddress.value!!, googleMapApiKey)
            }
            _patientData.value = tmpPatient
        }
    }
    suspend fun savePatient(patient: Patient) {
        _isLoading.value = true
        patient.encryptFields()
        if(patient.target.equals("call")){

        }
        else{
            visitAddress.value?.let {
                if(visitAddress.value?.uid == null)
                    patient.visitAddress = addressRepository.insert(it).toInt()
                else{
                    if(visitAddress.value!!.latitute == 0.00 && visitAddress.value!!.longitute ==0.00){
                        addressRepository.update(visitAddress.value!!)
                    }
                }
            }
            startAddress.value?.let {
                if(startAddress.value?.uid == null)
                    patient.startAddress = addressRepository.insert(it).toInt()
                else{
                    if(startAddress.value!!.latitute == 0.00 && startAddress.value!!.longitute ==0.00){
                        addressRepository.update(startAddress.value!!)
                    }
                }
            }
            receiptAddress.value?.let {
                if(receiptAddress.value?.uid == null)
                    patient.receiptAddress = addressRepository.insert(it).toInt()
                else{
                    addressRepository.update(receiptAddress.value!!)
                }
            }
        }



        if(patient.uid == null){
            Log.e(TAG_NAME, "INSERT PATIENT")
            insertPatient(patient)
        }
        else{
            Log.e(TAG_NAME, "UPDATE PATIENT")
            updatePatient(patient)
        }
        Log.e(TAG_NAME, "Created Or Updated Patient: ${patient.uid}")
        _isLoading.value = false
        _isFinished.value = true
    }

    fun setPhoneNumber(editValue: String) {
        _patientData.value?.phoneNumber = editValue

    }

    fun setPracticeName(editValue: String) {
        _patientData.value?.practiceName = editValue

    }

    fun isValidatePersonalDetails(): Boolean? {
        return _patientData.value?.isValidatePersonalDetails()
    }

    fun isValidInsuranceDetails(): Boolean? {
        return _patientData.value?.isValidInsuranceDetails()
    }

    fun setStartVisitDate(startDate: Date) {
        _patientData.value?.startVisitDate = startDate
    }

    fun setStartVisitTime(timeToString: String) {
        _patientData.value?.startVisitTime = timeToString
    }

    fun setStartPoint(s: String) {
        _patientData.value?.startPoint = s
    }

    fun setCurrentAddressSame(s: Boolean) {
        _patientData.value?.sameAddAsPrev = s
    }

    fun setCurrentPatientAlreadyVisited(s: Boolean) {
        _patientData.value?.alreadyVisitedDuringThisShift = s
    }

    suspend fun setCurrentAddress(address: Int?){
        _patientData.value?.visitAddress = address
        calculateDistance()
    }
    suspend fun setCurrentAddress(address: Address?){
        Log.e(TAG_NAME, "SET Visit Point: ${address?.uid}")
        _patientData.value?.visitAddress = address?.uid
        _visitAddress.value = address
        calculateDistance()
    }
    suspend fun setStartAddress(address: Int?){
//        _patientData.value?.startAddress = address

        val findAddress = addressRepository.find(address)
        var newAddress = findAddress.clone()

        _startAddress.value = newAddress
        calculateDistance()

    }
    suspend fun setVisitAddress(address: Int?){
//        _patientData.value?.visitAddress = address
        val findAddress = addressRepository.find(address)
        var newAddress = findAddress.clone()
        _visitAddress.value = newAddress
        calculateDistance()

    }
    suspend fun setStartAddress(address: Address?){
        Log.e(TAG_NAME, "SET Start Point: ${address?.uid}")
        _patientData.value?.startAddress = address?.uid
        _startAddress.value = address
        calculateDistance()
    }
    fun isValidLogisticDetails(): Boolean? {
        return _patientData.value?.isValidLogisticDetails()
    }

    fun isValidDoctorDocument(): Boolean? {
        return _patientData.value?.isValidDoctorDocument()
    }

    fun setDementia(editValue: Boolean) {
        _patientData.value?.dementia = editValue
    }

    fun setGeriatrics(editValue: Boolean) {
        _patientData.value?.geriatrics = editValue
    }

    fun setInfant(editValue: Boolean) {
        _patientData.value?.infant = editValue
    }

    fun setFractures(editValue: Boolean) {
        _patientData.value?.fractures = editValue
    }

    fun setServeHead(editValue: Boolean) {
        _patientData.value?.severeHandInjury = editValue
    }

    fun setThrombosis(editValue: Boolean) {
        _patientData.value?.thrombosis = editValue
    }

    fun setHypertension(editValue: Boolean) {
        _patientData.value?.hypertension = editValue
    }

    fun setPreHeartAttack(editValue: Boolean) {
        _patientData.value?.preHeartAttack = editValue
    }

    fun setPneumonia(editValue: Boolean) {
        _patientData.value?.pneumonia = editValue
    }

    fun setDivertikulistis(editValue: Boolean) {
        _patientData.value?.divertikulitis = editValue
    }

    fun setMedicals(i: Int, editValue: String) {
        if(i == 1){
            _patientData.value?.medicals1 = editValue
        }
        else if(i == 2){
            _patientData.value?.medicals2 = editValue
        }
        else if(i == 3){
            _patientData.value?.medicals3 = editValue
        }
    }

    fun printInsurance() {
        printResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            val result = patientRepository.generateInsurnacePDF(_patientData.value)
            if(result!= null){

                printResult.value = BaseResponse.Success(PDFGenerateResponse(result))

            }
        }
    }

    fun printReceipt(){
        printResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            val result = patientRepository.generateReceiptPDF(_patientData.value)
            if(result!= null){

                printResult.value = BaseResponse.Success(PDFGenerateResponse(result))

            }
        }
    }

    fun isValidMedicalReceipt(): Boolean? {
        return _patientData.value?.isValidMedicalReceipt()
    }

    fun loadPatientFromCard(_context: Context): Boolean {
        var status: Long = 0
        if(!scardLib.initialized){
            status = scardLib.SCardEstablishContext(_context)
            if(0L != status){
                return false
            }
            else{
                scardLib.SCardListReaders(_context)
            }
        }
        if(scardLib.initialized){
            status = scardLib.SCardConnect()
            if(0L == status){
                var patientData = scardLib.getPatientData()
                _patientData.value = patientData
//                setPatientID(patientData.patientID!!)
  //              setBirthDate(patientData.birthDate!!)
    //            setFirstname(patientData.firstName)
      //          setLastname(patientData.lastName)
        //        setGender(patientData.gender)
          //      setPostCode(patientData.postCode)
            //    setCity(patientData.city)
              //  setStreet(patientData.street)
                //setHouseNumber(patientData.houseNumber)
                ///setInsuranceName(patientData.insuranceName)
                //setInsuranceNumber(patientData.insuranceNumber)
                //setInsuranceStatus(patientData.insuranceStatus)
                return true
            }
            else{
                return false
            }
        }

        return false
    }

    fun setApiKey(apiKey: String) {
        googleMapApiKey = apiKey
    }

    fun setVisitAddressFromPatientData(isSync: Boolean) {
        _patientData.value?.sincVisitAddress = isSync
    }

    fun setVisitAddress(address: Address) {
        _visitAddress.value = address
    }

    fun formatVisitLocation() {
        if(_visitAddress.value != null){
            _visitAddress.value?.latitute = 0.0
            _visitAddress.value?.longitute = 0.0
        }
    }

    fun setReceiptFirstName(firstname: String) {
        _patientData.value?.receiptFirstName = firstname
    }

    fun setReceiptLastName(lastname: String) {
        _patientData.value?.receiptLastName = lastname
    }

    fun setReceiptAdditionalInfo(addInfo: String) {
        _patientData.value?.receiptAdditionalInfo = addInfo
    }

    fun setReceiptAddressFromPatientAddress() {
        if(!patientData.value?.city.isNullOrEmpty()){
            _receiptAddress.value?.city = patientData.value?.city!!
        }
        if(!patientData.value?.street.isNullOrEmpty()){
            _receiptAddress.value?.streetName = patientData.value?.street!!
        }
        if(!patientData.value?.houseNumber.isNullOrEmpty()){
            _receiptAddress.value?.streetNumber = patientData.value?.houseNumber!!
        }
        if(!patientData.value?.postCode.isNullOrEmpty()){
            _receiptAddress.value?.postCode = patientData.value?.postCode!!
        }
    }


}