package com.consulmedics.patientdata.fragments.addeditpatient

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.consulmedics.patientdata.databinding.FragmentPatientInsurranceDetailsBinding
import com.consulmedics.patientdata.models.Patient
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import java.text.SimpleDateFormat

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PatientInsurranceDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PatientInsurranceDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var patient: Patient? = null
    private var _binding: FragmentPatientInsurranceDetailsBinding? = null
    val binding get() = _binding!!
    private val sharedViewModel: AddEditPatientViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            patient = it.getSerializable(ARG_PARAM1) as Patient
        }
        sharedViewModel.patientData.observe(this, Observer {
            Log.e(AppConstants.TAG_NAME, "Shared Vide Model Data Changed in Insurance fragment")
            binding.editInsurranceName.setText(sharedViewModel.patientData.value?.insuranceName)
            binding.editInsurranceNumber.setText(sharedViewModel.patientData.value?.insuranceNumber)
            binding.editInsurranceStatus.setText(sharedViewModel.patientData.value?.insuranceStatus)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientInsurranceDetailsBinding.inflate(inflater, container, false)
        binding.editInsurranceStatus.setText(patient?.insuranceStatus)
        binding.editInsurranceName.setText(patient?.insuranceName)
        binding.editInsurranceNumber.setText(patient?.insuranceNumber)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PatientInsurranceDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Patient) =
            PatientInsurranceDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }
}