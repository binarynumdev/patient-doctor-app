package com.consulmedics.patientdata.viewmodels
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import kotlinx.coroutines.launch
import android.app.Application
import android.content.Context
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.consulmedics.patientdata.MyAppDatabase
import com.consulmedics.patientdata.data.api.request.UploadShiftRequest
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.data.api.response.LoadShiftApiResponse
import com.consulmedics.patientdata.data.api.response.UploadShiftApiResponse
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.data.model.PatientShift
import com.consulmedics.patientdata.repository.AddressRepository
import com.consulmedics.patientdata.repository.PatientRepository
import com.consulmedics.patientdata.repository.PatientShiftRepository
import com.consulmedics.patientdata.repository.UserRepository
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.SessionManager
import androidx.fragment.app.viewModels
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class ShiftViewModel(application: Application) : AndroidViewModel(application) {
    val pastShiftList: LiveData<List<PatientShift>>
    val upLoadedShiftList: LiveData<List<PatientShift>>
    val upcomingShiftList: LiveData<List<PatientShift>>
    val allShiftList: LiveData<List<PatientShift>>
    val userRepo = UserRepository()
    val repository: PatientShiftRepository
    val addressRepository: AddressRepository
    val patientRepositrory: PatientRepository
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    val loadShiftResult: MutableLiveData<BaseResponse<LoadShiftApiResponse>> = MutableLiveData()
    val saveShiftResult: MutableLiveData<BaseResponse<String>> = MutableLiveData()
    val uploadShiftResult: MutableLiveData<BaseResponse<UploadShiftApiResponse>> = MutableLiveData()
    val mContext: Context
    init {
        val dao = MyAppDatabase.getDatabase(application).patientShiftDao()
        mContext = application.applicationContext
        repository = PatientShiftRepository(dao)
        patientRepositrory = PatientRepository(MyAppDatabase.getDatabase(application).patientDao())
        addressRepository = AddressRepository(MyAppDatabase.getDatabase(application).addressDao())
        allShiftList = repository.allPatients
        upcomingShiftList = repository.upcomingShiftList(mContext)
        pastShiftList = repository.pastShiftList(mContext)
        upLoadedShiftList = repository.updatedShiftList(mContext)
    }
    fun loadShiftDetails() {
        loadShiftResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                Log.e("LoadShiftDetails", "Here")

                var api_token: String? = SessionManager.getString(getApplication(), "api_token")
                Log.e("Load_API_TOKEN", api_token.toString())
                //                ApiClient.setBearerToken(api_token.toString())
                val response = userRepo.loadShiftDetails()
                if (response?.code() == 200) {
                    loadShiftResult.value = BaseResponse.Success(response.body())
                    response.body()?.let { repository.saveShiftDetails(it.shiftList, mContext) }
                } else {
                    loadShiftResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                loadShiftResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



     fun uploadShift(currentShift: PatientShift) {
        Log.e("Patient Shift Data" , "${currentShift}")
         var patientList : List<Patient>? = null

         runBlocking {
             delay(250)
             patientList =
                 patientRepositrory.getPatientsByShift(
                     currentShift
                 ) // currentShift.getPatients()
         }
         Log.e("Before Upload details", "Patient List: ${patientList?.size}")
         if(patientList == null || patientList!!.size == 0) {
             Toast.makeText(mContext,"There isn't fully validated", Toast.LENGTH_SHORT).show()
         }
         else {
             uploadShiftResult.value = BaseResponse.Loading()
             viewModelScope.launch {
                 val patientList =
                     patientRepositrory.getPatientsByShift(
                         currentShift
                     ) // currentShift.getPatients()
                 var isPatientFullyValidated = true
                 var uploadShiftRequest: UploadShiftRequest =
                     UploadShiftRequest(
                         shiftId = "${currentShift.uid}",
                         doctorNotes = currentShift.doctorNote,
                         patients = ArrayList()
                     )
                 Log.e(TAG_NAME, "Patient List: ${patientList.size}")
                 var rsaPrivateKey = SessionManager.getPrivateKey(mContext)
                 if (rsaPrivateKey.isNullOrEmpty()) {
                     Log.e(TAG_NAME, "No RSA Private Key")
                     uploadShiftResult.value = BaseResponse.SessionError()
                 } else {
                     patientList.forEach {
                         if (it.isFullyValidated()) {
                             Log.e(TAG_NAME, "Add Patient To Array")
                             it.decryptFields()

                             if (it.startAddress != null) {
                                 it.startAddressDetails = addressRepository.find(it.startAddress)
                             }
                             if (it.visitAddress != null) {
                                 it.visitAddressDetails =
                                     addressRepository.find(it.visitAddress).encrypt(rsaPrivateKey)
                             }
                             if (it.receiptAddress != null) {
                                 it.receiptAddressDetails = addressRepository.find(it.receiptAddress)
                             }
                             it.encryptToSubmit(rsaPrivateKey)

                             uploadShiftRequest.patients.add(it)
                         } else {
                             isPatientFullyValidated = false
                             Toast.makeText(mContext,"Patient data is not fully validated", Toast.LENGTH_SHORT)
                         }
                     }
                     if (!isPatientFullyValidated) {
                         uploadShiftResult.value =
                             BaseResponse.Error(
                                 mContext.getString(R.string.patient_is_not_validated)
                             )
                     } else {

                         try {
                             Log.e(TAG_NAME, "Start Uploading")
                             val response = repository.uploadShiftDetail(uploadShiftRequest)
                             Log.e("status", "${response}")
                             if (response?.code() == 200) {

                                 patientRepositrory.updatePatientsByShift(
                                     currentShift
                                 )
                                 uploadShiftResult.value = BaseResponse.Success()
                                 currentShift.isUploaded = true
                                 repository.updatePatientShift(currentShift)
                             } else {
                                 uploadShiftResult.value = BaseResponse.Error(response?.message())
                             }
                             //                        uploadShiftResult.value =
                             // repository.uploadShiftDetail(uploadShiftRequest)
                         } catch (e: Exception) {
                             uploadShiftResult.value = BaseResponse.Error(e.toString())
                             Log.e(AppConstants.TAG_NAME, e.toString())
                         }
                     }
                 }
             }
         }
    }
    fun savePatientShift(patientShift: PatientShift) {
        saveShiftResult.value = BaseResponse.Loading()

        try {
            viewModelScope.launch {
                repository.insertOrUpdate(patientShift)
            }
                saveShiftResult.value = BaseResponse.Success("Success")
        } catch (ex: Exception) {
            saveShiftResult.value = BaseResponse.Error(ex.message)
        }
    }
    fun saveAndUpload(patientShift: PatientShift) {
        try {
            viewModelScope.launch {
                repository.insertOrUpdate(patientShift)
                //saveShiftResult.value = BaseResponse.Success("Success")
            }
            //Log.e("Save and Upload Button" , "Clicked")
        } catch (ex: Exception) {
            saveShiftResult.value = BaseResponse.Error(ex.message)
        }
        this.uploadShift(patientShift)
    }
}
