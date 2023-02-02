package com.consulmedics.patientdata.fragments.addeditpatient

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientPersonalDetailsBinding
import com.consulmedics.patientdata.models.Patient
import com.consulmedics.patientdata.utils.AppConstants.DISPLAY_DATE_FORMAT
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

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
    private val sharedViewModel: AddEditPatientViewModel by activityViewModels() {
        AddEditPatientViewModelFactory(MyApplication.repository!!)
    }
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            patient = it.getSerializable("patient_data") as Patient
        }
        sharedViewModel.patientData.observe(this, Observer {
            Log.e(TAG_NAME, "Shared Vide Model Data Changed")
            binding.editPatientID.setText(sharedViewModel.patientData.value?.patientID)
            binding.editFirstName.setText(sharedViewModel.patientData.value?.firstName)
            binding.editLastName.setText(sharedViewModel.patientData.value?.lastName)
            if(!sharedViewModel.patientData.value?.gender.isNullOrEmpty()){
                binding.editGender.setText( when(sharedViewModel.patientData.value?.gender == "W") { true -> "Femaile" false -> "Male"}  )
            }
            if(sharedViewModel.patientData.value?.birthDate != null){
                val birthDateFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT)
                binding.editDateOfBirth.setText(birthDateFormat.format(sharedViewModel.patientData.value?.birthDate))
            }
            binding.editStreet.setText(sharedViewModel.patientData.value?.street)
            binding.editCity.setText(sharedViewModel.patientData.value?.city)
            binding.editPostalCode.setText(sharedViewModel.patientData.value?.postCode)
            binding.editHouseNumber.setText(sharedViewModel.patientData.value?.houseNumber)
        })

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientPersonalDetailsBinding.inflate(inflater, container, false)
        patient?.let { sharedViewModel.setPatientData(it) }
        _binding?.apply {
            editPatientID.doAfterTextChanged {
                sharedViewModel.setPatientID(it.toString())
            }
            editFirstName.doAfterTextChanged {
                sharedViewModel.setFirstname(it.toString())
            }
            editLastName.doAfterTextChanged {
                sharedViewModel.setLastname(it.toString())
            }
            editDateOfBirth.doAfterTextChanged {
                sharedViewModel.setBirthDate(it.toString())
            }
            editStreet.doAfterTextChanged {
                sharedViewModel.setStreet(it.toString())
            }
            editHouseNumber.doAfterTextChanged {
                sharedViewModel.setHouseNumber(it.toString())
            }
            editCity.doAfterTextChanged {
                sharedViewModel.setCity(it.toString())
            }
            editPostalCode.doAfterTextChanged {
                sharedViewModel.setPostCode(it.toString())
            }
            editDateOfBirth.setOnClickListener{
                var c = Calendar.getInstance()
                if(sharedViewModel.patientData.value?.birthDate != null){
                    c.time = sharedViewModel.patientData.value?.birthDate!!
                }
                var year = c.get(Calendar.YEAR)
                var month = c.get(Calendar.MONTH)
                var day = c.get(Calendar.DAY_OF_MONTH)


                DatePickerDialog(requireActivity(),{ view, year, monthOfYear, dayOfMonth ->

                    Log.e(TAG_NAME, "$year $monthOfYear $dayOfMonth")
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    sharedViewModel.patientData.value?.birthDate = c.time
                    val birthDateFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT)
                    binding.editDateOfBirth.setText(birthDateFormat.format(c.time))
                },year , month, day).show()
            }
            editGender.setOnClickListener {
                val listItems =
                    arrayOf(getString(R.string.male),getString(R.string.female))

                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.select_gender)
                    .setSingleChoiceItems(listItems, when(editGender.text.toString().equals(getString(R.string.male))){true -> 0 false -> 1}){dialog, which ->
                        sharedViewModel.setGender(when(which == 0) { true -> "M" false -> "W"})
                        binding.editGender.setText( when(which == 1) { true -> "Femaile" false -> "Male"}  )
                    }
                    .setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
            btnContinue.setOnClickListener {
                if(sharedViewModel.patientData.value?.isValidatePersonalDetails() == true){
                    findNavController().navigate(R.id.action_patientPersonalDetailsFragment_to_patientInsurranceDetailsFragment)
                }
                else{
                    Toast.makeText(context, R.string.error_in_validate_personal_details_form, Toast.LENGTH_LONG).show()
                }
            }
        }
        val root = binding.root
        return root
    }


}