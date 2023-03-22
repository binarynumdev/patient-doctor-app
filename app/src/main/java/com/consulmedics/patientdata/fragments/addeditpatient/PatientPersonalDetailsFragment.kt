package com.consulmedics.patientdata.fragments.addeditpatient

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientPersonalDetailsBinding
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.utils.AppConstants.DISPLAY_DATE_FORMAT
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
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
    private var birthYear: Int? = null
    private var birthMonth: Int = 0
    private var birthDay: Int? = null
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
//            binding.editPatientID.setText(sharedViewModel.patientData.value?.patientID)
            binding.editFirstName.setText(sharedViewModel.patientData.value?.firstName)
            binding.editLastName.setText(sharedViewModel.patientData.value?.lastName)
            if(!sharedViewModel.patientData.value?.gender.isNullOrEmpty()){
//                binding.editGender.setText( when(sharedViewModel.patientData.value?.gender == "W") { true -> "Femaile" false -> "Male"}  )
                if(sharedViewModel.patientData.value?.gender == "W"){
                    binding.radioFemale.isChecked = true
                }
                else{
                    binding.radioMale.isChecked = true
                }
            }
            if(sharedViewModel.patientData.value?.birthDate != null){
                val birthDateFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT)
                val cal = Calendar.getInstance()
                cal.time = sharedViewModel.patientData.value?.birthDate
                val year = cal[Calendar.YEAR]
                val month = cal[Calendar.MONTH]
                val day = cal[Calendar.DAY_OF_MONTH]
                binding.editBirthDay.setText("${day}")
                binding.editBirthYear.setText("${year}")
                binding.editBirthMonth.setSelection(month)
            }
            binding.editStreet.setText(sharedViewModel.patientData.value?.street)
            binding.editCity.setText(sharedViewModel.patientData.value?.city)
            binding.editPostalCode.setText(sharedViewModel.patientData.value?.postCode)
            binding.editHouseNumber.setText(sharedViewModel.patientData.value?.houseNumber)
            binding.editPatientPhoneNumber.setText(sharedViewModel.patientData.value?.phoneNumber)
            binding.editPatientNamePractice.setText(sharedViewModel.patientData.value?.practiceName)
        })

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientPersonalDetailsBinding.inflate(inflater, container, false)
        patient?.let { sharedViewModel.setPatientData(it) }
        ArrayAdapter.createFromResource(requireContext(), R.array.month_array, android.R.layout.simple_spinner_dropdown_item).also {
            adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.editBirthMonth.adapter = adapter
        }
        _binding?.apply {

            editFirstName.doAfterTextChanged {
                sharedViewModel.setFirstname(it.toString())
            }
            editLastName.doAfterTextChanged {
                sharedViewModel.setLastname(it.toString())
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
            editBirthMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    birthMonth = position
                    updatePatientBirthDate()
                }

            }
            editBirthDay.doAfterTextChanged {
                if(it.toString().isNotEmpty()){
                    birthDay = it.toString().toInt()
                    updatePatientBirthDate()
                }

            }
            editBirthYear.doAfterTextChanged {
                if(it.toString().isNotEmpty()){
                    birthYear = it.toString().toInt()
                    updatePatientBirthDate()
                }

            }
            radioMale.setOnClickListener{
                sharedViewModel.setGender("M")
            }
            radioFemale.setOnClickListener{
                sharedViewModel.setGender("W")
            }

            editPatientPhoneNumber.doAfterTextChanged {
                sharedViewModel.setPhoneNumber(it.toString())
            }
            editPatientNamePractice.doAfterTextChanged {
                sharedViewModel.setPracticeName(it.toString())
            }
            btnContinue.setOnClickListener {
                findNavController().navigate(R.id.action_patientPersonalDetailsFragment_to_patientInsurranceDetailsFragment)
            }
            btnSave.setOnClickListener {
                sharedViewModel.patientData.value?.let { it1 ->
                    sharedViewModel.savePatient(it1)
                    activity?.finish()
                }
            }
            btnCancel.setOnClickListener {
                requireActivity().finish()
            }
            btnReadCard.setOnClickListener {
                val cardReadResult = sharedViewModel.loadPatientFromCard(requireContext())
                if(cardReadResult){

                }
                else{
                    Toast.makeText(requireContext(), R.string.no_card_reader, Toast.LENGTH_SHORT).show()
                }
            }
        }
        val root = binding.root
        return root
    }

    private fun updatePatientBirthDate() {
        if(birthYear != null  && birthDay != null){
            val cal = Calendar.getInstance()
//            cal.time = sharedViewModel.patientData.value?.birthDate
            cal[Calendar.YEAR] = birthYear!!
            cal[Calendar.MONTH] = birthMonth
            cal[Calendar.DAY_OF_MONTH] = birthDay!!
            Log.e(TAG_NAME, cal.time.toString())
            sharedViewModel.setBirthDate(cal.time);
//            sharedViewModel.patientData.value?.birthDate = cal.time
        }
    }


}