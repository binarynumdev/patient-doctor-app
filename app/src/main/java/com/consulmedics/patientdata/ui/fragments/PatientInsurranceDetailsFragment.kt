package com.consulmedics.patientdata.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientInsurranceDetailsBinding
import com.consulmedics.patientdata.databinding.FragmentPatientPersonalDetailsBinding
import com.consulmedics.patientdata.models.Patient

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            patient = it.getSerializable(ARG_PARAM1) as Patient
        }
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