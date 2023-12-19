package com.consulmedics.patientdata.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.viewModelScope
import com.consulmedics.patientdata.Converters
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.data.model.PatientShift
import com.consulmedics.patientdata.databinding.ActivityEditPatientShiftBinding
import com.consulmedics.patientdata.fragments.SubShiftListFragment
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import com.consulmedics.patientdata.viewmodels.ShiftViewModel
import kotlinx.coroutines.launch

class EditPatientShiftActivity : BaseActivity() {
    private var patientShift: PatientShift? = null
    private lateinit var binding: ActivityEditPatientShiftBinding
    private  val viewModel: ShiftViewModel by viewModels<ShiftViewModel>()
    val patientItemOnClickInterface: SubShiftListFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPatientShiftBinding.inflate(layoutInflater)
        setContentView(binding.root)
        patientShift = intent.getSerializableExtra(AppConstants.PATIENT_SHIFT_DATA) as PatientShift
        val converters: Converters = Converters()
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            textShiftBiddingName.text = patientShift?.nameBidding
            shiftDateTimes.text = "${converters.dateToFormatedString(patientShift?.startDate!!, "yyyy-MM-dd HH:mm:ss", "dd.MM.yyyy H")} - ${converters.dateToFormatedString(patientShift?.endDate!!, "yyyy-MM-dd HH:mm:ss", "H")} Uhr"
            if(patientShift?.serviceType == 1 || patientShift?.serviceType == 0){
                textServiceType.text = "Visit Patient"
                icServiceType.setImageDrawable(getDrawable(R.drawable.ic_car))
            }
            else{
                textServiceType.text = "Stay Hospital"
                icServiceType.setImageDrawable(getDrawable(R.drawable.ic_home))
            }

            editShiftDoctorNotes.setText(patientShift?.doctorNote)
            btnSave.setOnClickListener {
                if(confirmShiftCompleted.isChecked){
                    patientShift?.doctorNote = editShiftDoctorNotes.text.toString()
                    viewModel.viewModelScope.launch {
                        viewModel.savePatientShift(patientShift!!)
                    }
                }
                else{
                    confirmShiftCompleted.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            }

            btnSaveUp.setOnClickListener {
                if(confirmShiftCompleted.isChecked){
                    Log.e("PatientShift data","${patientShift}")
                    patientShift?.doctorNote = editShiftDoctorNotes.text.toString()
                    viewModel.viewModelScope.launch {
                        viewModel.saveAndUpload(patientShift!!)
                    }
                }
                else{
                    confirmShiftCompleted.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            }

        }
        viewModel.uploadShiftResult.observe(this){
            when(it){
                is BaseResponse.Loading -> {
                    showLoadingSpinner("Loading", "Please wait while upload shift & patient details")
                }

                is BaseResponse.Success -> {
                    hideLoadingSpinner()
                    Toast.makeText(this, "Upload Success", Toast.LENGTH_LONG).show()
                    finish()
                }

                is BaseResponse.Error -> {
                    Log.e(AppConstants.TAG_NAME, "API ERROR :${it.msg}")
                    Toast.makeText(this, "Upload Failed", Toast.LENGTH_LONG).show()
                    hideLoadingSpinner()
                }
                else -> {
                    hideLoadingSpinner()
                }
            }
        }
        viewModel.saveShiftResult.observe(this){
            when (it) {
                is BaseResponse.Loading -> {
                    showLoadingSpinner("Loading", "Please wait while load shift details")
                }

                is BaseResponse.Success -> {
                    hideLoadingSpinner()
                    finish()
                }

                is BaseResponse.Error -> {
                    Log.e(AppConstants.TAG_NAME, "API ERROR :${it.msg}")
                    hideLoadingSpinner()
                }
                else -> {
                    hideLoadingSpinner()
                }
            }
        }


    }


}