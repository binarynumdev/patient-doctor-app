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
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class PatientAdditionalDetailsFragment : BaseAddEditPatientFragment() {
    private var _binding: FragmentPatientAdditionalDetailsBinding? = null
    val binding get() = _binding!!

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
                sharedViewModel.setDementia(true)
            }
            radioDemenzNo.setOnClickListener {
                sharedViewModel.setDementia(false)
            }

            radioGeriatricsYes.setOnClickListener {
                sharedViewModel.setGeriatrics(true)
            }
            radioGeriatricsNo.setOnClickListener {
                sharedViewModel.setGeriatrics(false)
            }

            radioInfantYes.setOnClickListener {
                sharedViewModel.setInfant(true)
            }
            radioInfantNo.setOnClickListener {
                sharedViewModel.setInfant(false)
            }

            radioFracturesYes.setOnClickListener {
                sharedViewModel.setFractures(true)
            }
            radioFracturesNo.setOnClickListener {
                sharedViewModel.setFractures(false)
            }

            radioServerHeadYes.setOnClickListener {
                sharedViewModel.setServeHead(true)
            }
            radioServerHeadNo.setOnClickListener {
                sharedViewModel.setServeHead(false)
            }

            radioThrombosisYes.setOnClickListener {
                sharedViewModel.setThrombosis(true)
            }
            radioThrombosisNo.setOnClickListener {
                sharedViewModel.setThrombosis(false)
            }

            radioHypertensionYes.setOnClickListener {
                sharedViewModel.setHypertension(true)
            }
            radioHypertensionNo.setOnClickListener {
                sharedViewModel.setHypertension(false)
            }

            radioPreHeartAttackYes.setOnClickListener {
                sharedViewModel.setPreHeartAttack(true)
            }
            radioPreHeartAttackNo.setOnClickListener {
                sharedViewModel.setPreHeartAttack(false)
            }

            radioPneumoniaYes.setOnClickListener {
                sharedViewModel.setPneumonia(true)
            }
            radioPneumoniaNo.setOnClickListener {
                sharedViewModel.setPneumonia(false)
            }

            radioDivertikulitisYes.setOnClickListener {
                sharedViewModel.setDivertikulistis(true)
            }
            radioDivertikulitisNo.setOnClickListener {
                sharedViewModel.setDivertikulistis(false)
            }

            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.patientData.observe(viewLifecycleOwner, Observer {
//            sharedViewModel.patientData.value?.dementia?.let {
//                if(it){
//                    binding.radioDemenzYes.isChecked = true
//                } else{
//                    binding.radioDemenzNo.isChecked = true
//                }
//            } ?: run{
//                binding.radioDemenzYes.isChecked = false
//                binding.radioDemenzNo.isChecked = false
//            }

            when (sharedViewModel.patientData.value?.dementia) {
                true-> binding.radioDemenzYes.isChecked = true
                false -> binding.radioDemenzNo.isChecked = true
                null -> {
                    binding.radioDemenzYes.isChecked = false
                    binding.radioDemenzNo.isChecked = false
                }
            }

            when (sharedViewModel.patientData.value?.geriatrics) {
                true-> binding.radioGeriatricsYes.isChecked = true
                false -> binding.radioGeriatricsNo.isChecked = true
                null -> {
                    binding.radioGeriatricsYes.isChecked = false
                    binding.radioGeriatricsNo.isChecked = false
                }
            }

            when (sharedViewModel.patientData.value?.infant) {
                true-> binding.radioInfantYes.isChecked = true
                false -> binding.radioInfantNo.isChecked = true
                null -> {
                    binding.radioInfantYes.isChecked = false
                    binding.radioInfantNo.isChecked = false
                }
            }

            when (sharedViewModel.patientData.value?.fractures) {
                true-> binding.radioFracturesYes.isChecked = true
                false -> binding.radioFracturesNo.isChecked = true
                null -> {
                    binding.radioFracturesYes.isChecked = false
                    binding.radioFracturesNo.isChecked = false
                }
            }

            when (sharedViewModel.patientData.value?.severeHandInjury) {
                true-> binding.radioServerHeadYes.isChecked = true
                false -> binding.radioServerHeadNo.isChecked = true
                null -> {
                    binding.radioServerHeadYes.isChecked = false
                    binding.radioServerHeadNo.isChecked = false
                }
            }

            when (sharedViewModel.patientData.value?.thrombosis) {
                true-> binding.radioThrombosisYes.isChecked = true
                false -> binding.radioThrombosisNo.isChecked = true
                null -> {
                    binding.radioThrombosisYes.isChecked = false
                    binding.radioThrombosisNo.isChecked = false
                }
            }

            when (sharedViewModel.patientData.value?.hypertension) {
                true-> binding.radioHypertensionYes.isChecked = true
                false -> binding.radioHypertensionNo.isChecked = true
                null -> {
                    binding.radioHypertensionYes.isChecked = false
                    binding.radioHypertensionNo.isChecked = false
                }
            }

            when (sharedViewModel.patientData.value?.preHeartAttack) {
                true-> binding.radioPreHeartAttackYes.isChecked = true
                false -> binding.radioPreHeartAttackNo.isChecked = true
                null -> {
                    binding.radioPreHeartAttackYes.isChecked = false
                    binding.radioPreHeartAttackNo.isChecked = false
                }
            }

            when (sharedViewModel.patientData.value?.pneumonia) {
                true-> binding.radioPneumoniaYes.isChecked = true
                false -> binding.radioPneumoniaNo.isChecked = true
                null -> {
                    binding.radioPneumoniaYes.isChecked = false
                    binding.radioPneumoniaNo.isChecked = false
                }
            }

            when (sharedViewModel.patientData.value?.divertikulitis) {
                true-> binding.radioDivertikulitisYes.isChecked = true
                false -> binding.radioDivertikulitisNo.isChecked = true
                null -> {
                    binding.radioDivertikulitisYes.isChecked = false
                    binding.radioDivertikulitisNo.isChecked = false
                }
            }
        })
    }
}