package com.consulmedics.patientdata.fragments.addeditpatient

import android.content.pm.PackageManager
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientPersonalDetailsBinding
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.utils.AppConstants.DISPLAY_DATE_FORMAT
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import kotlinx.coroutines.launch
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
    private var tmpFirstName: String = ""
    private var tmpLastName: String = ""
    private lateinit var apiKey: String

    private val sharedViewModel: AddEditPatientViewModel by activityViewModels(){
        AddEditPatientViewModelFactory(MyApplication.patientRepository!!, MyApplication.hotelRepository!!, MyApplication.addressRepository!!)
    }
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            patient = it.getSerializable("patient_data") as Patient
        }
        try {
            // Load the API key from the local.properties file
            val applicationInfo = requireActivity().packageManager.getApplicationInfo(
                requireActivity().packageName, PackageManager.GET_META_DATA)
            val bundle = applicationInfo.metaData
            apiKey = bundle.getString("com.google.android.geo.API_KEY").toString()
            sharedViewModel.setApiKey(apiKey)

        } catch (e: Exception) {
            e.printStackTrace()
        }


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
                tmpFirstName = it.toString()
                updateTopBar()
            }
            editLastName.doAfterTextChanged {
                sharedViewModel.setLastname(it.toString())
                tmpLastName = it.toString()
                updateTopBar()
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
                if(it.toString().isNullOrEmpty()){
                    editPatientPhoneNumber.setBackgroundResource(R.drawable.bg_edit_text_red_border)
                }
                else{
                    editPatientPhoneNumber.setBackgroundResource(R.drawable.bg_edit_text)
                }
                sharedViewModel.setPhoneNumber(it.toString())
            }
            editPatientNamePractice.doAfterTextChanged {
                sharedViewModel.setPracticeName(it.toString())
                if(it.toString().isNullOrEmpty()){
                    editPatientNamePractice.setBackgroundResource(R.drawable.bg_edit_text_red_border)
                }
                else{
                    editPatientNamePractice.setBackgroundResource(R.drawable.bg_edit_text)
                }
            }
            btnContinue.setOnClickListener {
                if(canSave()){
                    findNavController().navigate(R.id.action_patientPersonalDetailsFragment_to_patientInsurranceDetailsFragment)
                }
                else{
                    Toast.makeText(requireContext(), R.string.error_empty_phone_number, Toast.LENGTH_SHORT).show()
                }

            }
            btnSave.setOnClickListener {
                if(canSave()){
                    sharedViewModel.patientData.value?.let { it1 ->
                        it.isEnabled = false

                        sharedViewModel.viewModelScope.launch {
                            sharedViewModel.savePatient(it1)
                        }
                        activity?.finish()
                    }
                }
                else{
                    Toast.makeText(requireContext(), R.string.error_empty_phone_number, Toast.LENGTH_SHORT).show()
                }

            }
            btnCancel.setOnClickListener {
                requireActivity().finish()
            }

            topBar.apply {
//                textViewLeft.visibility = GONE
                buttonRight1.text = getText(R.string.read_card)
                buttonRight1.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.stat_sys_download, 0, 0, 0)
                buttonRight1.setOnClickListener {
                    val cardReadResult = sharedViewModel.loadPatientFromCard(requireContext())
                    if(cardReadResult){

                    }
                    else{
                        Toast.makeText(requireContext(), R.string.no_card_reader, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        val root = binding.root
        return root
    }

    private fun canSave(): Boolean {
        if(binding.editPatientPhoneNumber.text.isNullOrEmpty()){
            return false
        }
        if(binding.editPatientNamePractice.text.isNullOrEmpty()){
            return false
        }
        return true
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
        updateTopBar()
    }

    private fun updateTopBar() {
        Log.e(TAG_NAME,"${tmpLastName} ${tmpFirstName} ${birthDay} ${birthYear} ${birthMonth}" )
        if(birthDay == null || birthYear == null || birthMonth == null){
            if(tmpLastName.isNullOrEmpty()){
                binding.topBar.textViewLeft.text = "${tmpFirstName}"
            }
            else if (tmpFirstName.isNullOrEmpty()){
                binding.topBar.textViewLeft.text = "${tmpLastName}"
            }
            else{
                binding.topBar.textViewLeft.text = "${tmpLastName},${tmpFirstName}"
            }
        }
        else{
            val dateStr = "(${birthDay}.${birthMonth+1}.${birthYear})"
            if(tmpLastName.isNullOrEmpty()){
                binding.topBar.textViewLeft.text = "${tmpFirstName}${dateStr}"
            }
            else if (tmpFirstName.isNullOrEmpty()){
                binding.topBar.textViewLeft.text = "${tmpLastName}${dateStr}"
            }
            else{
                binding.topBar.textViewLeft.text = "${tmpLastName},${tmpFirstName}${dateStr}"
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.patientData.observe(viewLifecycleOwner, Observer {
            Log.e(TAG_NAME, "Shared Vide Model Data Changed")
//            binding.editPatientID.setText(sharedViewModel.patientData.value?.patientID)
            tmpFirstName = it.firstName
            tmpLastName = it.lastName
            updateTopBar()
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
                binding.topBar.textViewLeft.setText("${sharedViewModel.patientData.value?.lastName},${sharedViewModel.patientData.value?.firstName}($day.${month + 1}.$year)")
            }
            else{
                binding.topBar.textViewLeft.setText("${sharedViewModel.patientData.value?.lastName},${sharedViewModel.patientData.value?.firstName} ")
            }
            binding.editStreet.setText(sharedViewModel.patientData.value?.street)
            binding.editCity.setText(sharedViewModel.patientData.value?.city)
            binding.editPostalCode.setText(sharedViewModel.patientData.value?.postCode)
            binding.editHouseNumber.setText(sharedViewModel.patientData.value?.houseNumber)
            binding.editPatientPhoneNumber.setText(sharedViewModel.patientData.value?.phoneNumber)
            binding.editPatientNamePractice.setText(sharedViewModel.patientData.value?.practiceName)
        })
    }


}