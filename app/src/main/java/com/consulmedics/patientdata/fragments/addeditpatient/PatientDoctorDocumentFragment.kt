package com.consulmedics.patientdata.fragments.addeditpatient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientDoctorDocumentBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class PatientDoctorDocumentFragment : BaseAddEditPatientFragment() {
    private var _binding: FragmentPatientDoctorDocumentBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientDoctorDocumentBinding.inflate(inflater, container, false)
        binding.apply {
            editDiagnosis.doAfterTextChanged {
                sharedViewModel.setDiagnosis(it.toString())
            }
            editHealthStatus.doAfterTextChanged {
                sharedViewModel.setHealthStatus(it.toString())
            }
            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }
            btnContinue.setOnClickListener {
                findNavController().navigate(R.id.action_patientDoctorDocumentFragment_to_patientAdditionalDetailsFragment)
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.patientData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.editDiagnosis.setText(sharedViewModel.patientData.value?.diagnosis)
            binding.editHealthStatus.setText(sharedViewModel.patientData.value?.healthStatus)

        })
    }

}