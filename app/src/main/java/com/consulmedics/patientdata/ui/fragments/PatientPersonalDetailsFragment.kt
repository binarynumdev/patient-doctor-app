package com.consulmedics.patientdata.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientDataTabBinding
import com.consulmedics.patientdata.databinding.FragmentPatientPersonalDetailsBinding
import com.consulmedics.patientdata.models.Patient
import java.text.SimpleDateFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PatientPersonalDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PatientPersonalDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var patient: Patient? = null
    private var param2: String? = null
    private var _binding: FragmentPatientPersonalDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            patient = it.getSerializable("patient") as Patient
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientPersonalDetailsBinding.inflate(inflater, container, false)
        binding.editPatientID.setText(patient?.patientID)
        binding.editFirstName.setText(patient?.firstName)
        binding.editLastName.setText(patient?.lastName)
        binding.editGender.setText( when(patient?.gender == "W") { true -> "Femaile" false -> "Male"}  )
        val birthDateFormat = SimpleDateFormat("yyyy-MM-dd")


        binding.editDateOfBirth.setText(birthDateFormat.format(patient?.birthDate))
        binding.editStreet.setText(patient?.street)
        binding.editCity.setText(patient?.city)
        binding.editPostalCode.setText(patient?.postCode)
        binding.editHouseNumber.setText(patient?.houseNumber)
        val root = binding.root
        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PatientPersonalDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Patient) =
            PatientPersonalDetailsFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    putSerializable("patient", param1)
                }
            }
    }
}