package com.consulmedics.patientdata.fragments.addeditpatient

import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.consulmedics.patientdata.Converters
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientPersonalDetailsBinding
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.databinding.FragmentPatientDetailsForPhoneCallBinding
import com.consulmedics.patientdata.utils.AppConstants
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
class PatientDetailsForPhoneCallFragment : BaseAddEditPatientFragment() {
    // TODO: Rename and change types of parameters
    private var patient: Patient? = null
    private var param2: String? = null
    private var _binding: FragmentPatientDetailsForPhoneCallBinding? = null
    private var birthYear: Int? = null
    private var birthMonth: Int = 0
    private var birthDay: Int? = null
    private var tmpFirstName: String = ""
    private var tmpLastName: String = ""
    private lateinit var apiKey: String


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
        _binding = FragmentPatientDetailsForPhoneCallBinding.inflate(inflater, container, false)
        patient?.let { sharedViewModel.setPatientData(it) }

        _binding?.apply {

            editFirstName.doAfterTextChanged {
                sharedViewModel.setFirstname(it.toString())
                tmpFirstName = it.toString()
            }
            editLastName.doAfterTextChanged {
                sharedViewModel.setLastname(it.toString())
                tmpLastName = it.toString()
            }


            editPatientBirthDate.setOnClickListener {
                var c = Calendar.getInstance()
                val converter: Converters = Converters()
                if(sharedViewModel.patientData.value?.birthDate != null){
                    c.time = sharedViewModel.patientData.value?.birthDate
                }
                var year = c.get(Calendar.YEAR)
                var month = c.get(Calendar.MONTH)
                var day = c.get(Calendar.DAY_OF_MONTH)


                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.custom_date_picker)
                val btnTimeOk = dialog.findViewById<Button>(R.id.btnOk)
                val btnTimeCancel = dialog.findViewById<Button>(R.id.btnCancel)
                var datePicker = dialog.findViewById<DatePicker>(R.id.date_picker)
                datePicker.init(year, month, day, DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->  })
                btnTimeCancel.setOnClickListener {
                    dialog.dismiss()
                }
                btnTimeOk.setOnClickListener {
                    c.set(Calendar.YEAR, datePicker.year)
                    c.set(Calendar.MONTH, datePicker.month)
                    c.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
                    sharedViewModel.setBirthDate(c.time)
                    dialog.dismiss()
                    val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
                    binding.editPatientBirthDate.setText(birthDateFormat.format(c.time))
                }
                dialog.show()
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
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.patientData.observe(viewLifecycleOwner, Observer {
            Log.e(TAG_NAME, "Shared Vide Model Data Changed")
//            binding.editPatientID.setText(sharedViewModel.patientData.value?.patientID)
            tmpFirstName = it.firstName
            tmpLastName = it.lastName
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

            binding.editPatientPhoneNumber.setText(sharedViewModel.patientData.value?.phoneNumber)
        })
    }


}