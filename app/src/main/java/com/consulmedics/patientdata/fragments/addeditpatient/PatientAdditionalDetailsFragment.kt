package com.consulmedics.patientdata.fragments.addeditpatient

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientAdditionalDetailsBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.AppConstants.NO_TEXT
import com.consulmedics.patientdata.utils.AppConstants.YES_TEXT
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class PatientAdditionalDetailsFragment : Fragment() {
    private var _binding: FragmentPatientAdditionalDetailsBinding? = null
    val binding get() = _binding!!
    private val sharedViewModel: AddEditPatientViewModel by activityViewModels(){
        AddEditPatientViewModelFactory(MyApplication.patientRepository!!, MyApplication.hotelRepository!!, MyApplication.addressRepository!!)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(AppConstants.TAG_NAME, "ONCREATVIEW")
        _binding = FragmentPatientAdditionalDetailsBinding.inflate(inflater, container, false)
        _binding?.apply {

            btnNext.setOnClickListener {
                findNavController().navigate(R.id.action_patientAdditionalDetailsFragment_to_patientReceiptFragment)
            }


            radioDemenzYes.setOnClickListener {
                sharedViewModel.setDementia(YES_TEXT)
            }
            radioDemenzNo.setOnClickListener {
                sharedViewModel.setDementia(NO_TEXT)
            }

            radioGeriatricsYes.setOnClickListener {
                sharedViewModel.setGeriatrics(YES_TEXT)
            }
            radioGeriatricsNo.setOnClickListener {
                sharedViewModel.setGeriatrics(NO_TEXT)
            }

            radioInfantYes.setOnClickListener {
                sharedViewModel.setInfant(YES_TEXT)
            }
            radioInfantNo.setOnClickListener {
                sharedViewModel.setInfant(NO_TEXT)
            }

            radioFracturesYes.setOnClickListener {
                sharedViewModel.setFractures(YES_TEXT)
            }
            radioFracturesNo.setOnClickListener {
                sharedViewModel.setFractures(NO_TEXT)
            }

            radioServerHeadYes.setOnClickListener {
                sharedViewModel.setServeHead(YES_TEXT)
            }
            radioServerHeadNo.setOnClickListener {
                sharedViewModel.setServeHead(NO_TEXT)
            }

            radioThrombosisYes.setOnClickListener {
                sharedViewModel.setThrombosis(YES_TEXT)
            }
            radioThrombosisNo.setOnClickListener {
                sharedViewModel.setThrombosis(NO_TEXT)
            }

            radioHypertensionYes.setOnClickListener {
                sharedViewModel.setHypertension(YES_TEXT)
            }
            radioHypertensionNo.setOnClickListener {
                sharedViewModel.setHypertension(NO_TEXT)
            }

            radioPreHeartAttackYes.setOnClickListener {
                sharedViewModel.setPreHeartAttack(YES_TEXT)
            }
            radioPreHeartAttackNo.setOnClickListener {
                sharedViewModel.setPreHeartAttack(NO_TEXT)
            }

            radioPneumoniaYes.setOnClickListener {
                sharedViewModel.setPneumonia(YES_TEXT)
            }
            radioPneumoniaNo.setOnClickListener {
                sharedViewModel.setPneumonia(NO_TEXT)
            }

            radioDivertikulitisYes.setOnClickListener {
                sharedViewModel.setDivertikulistis(YES_TEXT)
            }
            radioDivertikulitisNo.setOnClickListener {
                sharedViewModel.setDivertikulistis(NO_TEXT)
            }

            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }
            btnSave.setOnClickListener {
                sharedViewModel.patientData.value?.let { it1 ->
                    it.isEnabled = false

                    sharedViewModel.viewModelScope.launch {
                        sharedViewModel.savePatient(it1)
                    }
                    activity?.finish()
                }
            }
            topBar.buttonRight1.visibility = GONE
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.patientData.observe(viewLifecycleOwner, Observer {
            Log.e(AppConstants.TAG_NAME, "Shared Vide Model Data Changed in Additional fragment")
            if(sharedViewModel.patientData.value?.birthDate != null){
                val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
                val cal = Calendar.getInstance()
                cal.time = sharedViewModel.patientData.value?.birthDate
                val year = cal[Calendar.YEAR]
                val month = cal[Calendar.MONTH]
                val day = cal[Calendar.DAY_OF_MONTH]
                binding.topBar.textViewLeft.setText("${it.lastName},${it.firstName}($day.${month + 1}.$year)")
            }
            else{
                binding.topBar.textViewLeft.setText("${it.lastName},${it.firstName} ")
            }




            if(sharedViewModel.patientData.value?.dementia == YES_TEXT){
                binding.radioDemenzYes.isChecked = true
            }
            else if (sharedViewModel.patientData.value?.dementia == NO_TEXT){
                binding.radioDemenzNo.isChecked = true
            }



            if(sharedViewModel.patientData.value?.geriatrics == YES_TEXT){
                binding.radioGeriatricsYes.isChecked = true
            }
            else if (sharedViewModel.patientData.value?.geriatrics == NO_TEXT){
                binding.radioGeriatricsNo.isChecked = true
            }

            if(sharedViewModel.patientData.value?.infant == YES_TEXT){
                binding.radioInfantYes.isChecked = true
            }
            else if (sharedViewModel.patientData.value?.infant == NO_TEXT){
                binding.radioInfantNo.isChecked = true
            }


            if(sharedViewModel.patientData.value?.fractures == YES_TEXT){
                binding.radioFracturesYes.isChecked = true
            }
            else if (sharedViewModel.patientData.value?.fractures == NO_TEXT){
                binding.radioFracturesNo.isChecked = true
            }

            if(sharedViewModel.patientData.value?.serverHandInjury == YES_TEXT){
                binding.radioServerHeadYes.isChecked = true
            }
            else if (sharedViewModel.patientData.value?.serverHandInjury == NO_TEXT){
                binding.radioServerHeadNo.isChecked = true
            }

            if(sharedViewModel.patientData.value?.thrombosis == YES_TEXT){
                binding.radioThrombosisYes.isChecked = true
            }
            else if (sharedViewModel.patientData.value?.thrombosis == NO_TEXT){
                binding.radioThrombosisNo.isChecked = true
            }


            if(sharedViewModel.patientData.value?.hypertension == YES_TEXT){
                binding.radioHypertensionYes.isChecked = true
            }
            else if (sharedViewModel.patientData.value?.hypertension == NO_TEXT){
                binding.radioHypertensionNo.isChecked = true
            }

            if(sharedViewModel.patientData.value?.preHeartAttack == YES_TEXT){
                binding.radioPreHeartAttackYes.isChecked = true
            }
            else if (sharedViewModel.patientData.value?.preHeartAttack == NO_TEXT){
                binding.radioPreHeartAttackNo.isChecked = true
            }

            if(sharedViewModel.patientData.value?.pneumonia == YES_TEXT){
                binding.radioPneumoniaYes.isChecked = true
            }
            else if (sharedViewModel.patientData.value?.pneumonia == NO_TEXT){
                binding.radioPneumoniaNo.isChecked = true
            }


            if(sharedViewModel.patientData.value?.divertikulitis == YES_TEXT){
                binding.radioDivertikulitisYes.isChecked = true
            }
            else if (sharedViewModel.patientData.value?.divertikulitis == NO_TEXT){
                binding.radioDivertikulitisNo.isChecked = true
            }

        })
    }
}