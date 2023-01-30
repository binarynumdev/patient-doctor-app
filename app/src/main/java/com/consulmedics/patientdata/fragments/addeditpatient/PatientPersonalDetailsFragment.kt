package com.consulmedics.patientdata.fragments.addeditpatient

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.consulmedics.patientdata.databinding.FragmentPatientPersonalDetailsBinding
import com.consulmedics.patientdata.models.Patient
import java.text.SimpleDateFormat

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
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            patient = it.getSerializable("patient") as Patient
        }
    }
    fun getFirstName(): String {
        return _binding!!.editFirstName.text.toString()
    }
    fun getPatientID():String{
        return _binding!!.editPatientID.text.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientPersonalDetailsBinding.inflate(inflater, container, false)
        if(_binding != null)
            Log.e("INITFRAGMENT", "OK")
        if(patient != null){
            binding.editPatientID.setText(patient?.patientID)
            binding.editFirstName.setText(patient?.firstName)
            binding.editLastName.setText(patient?.lastName)
            binding.editGender.setText( when(patient?.gender == "W") { true -> "Femaile" false -> "Male"}  )
            val birthDateFormat = SimpleDateFormat("dd.MM.yyyy")


            binding.editDateOfBirth.setText(birthDateFormat.format(patient?.birthDate))
            binding.editStreet.setText(patient?.street)
            binding.editCity.setText(patient?.city)
            binding.editPostalCode.setText(patient?.postCode)
            binding.editHouseNumber.setText(patient?.houseNumber)
        }

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