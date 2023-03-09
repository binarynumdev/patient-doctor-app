package com.consulmedics.patientdata.fragments.addeditpatient

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.consulmedics.patientdata.Converters
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientInsurranceDetailsBinding
import com.consulmedics.patientdata.databinding.FragmentPatientLogisticsDetailsBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.AppConstants.HOTEL_TEXT
import com.consulmedics.patientdata.utils.AppConstants.NO_TEXT
import com.consulmedics.patientdata.utils.AppConstants.PREV_PATIENT_TEXT
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.utils.AppConstants.YES_TEXT
import com.consulmedics.patientdata.utils.AppUtils
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


class PatientLogisticsDetailsFragment : Fragment() {
    private var _binding: FragmentPatientLogisticsDetailsBinding? = null
    val binding get() = _binding!!
    private val sharedViewModel: AddEditPatientViewModel by activityViewModels(){
        AddEditPatientViewModelFactory(MyApplication.repository!!)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientLogisticsDetailsBinding.inflate(inflater, container, false)
        binding.apply {
            editDateOfVisit.setOnClickListener {
                var c = Calendar.getInstance()
                val converter: Converters = Converters()
                if(!sharedViewModel.patientData.value?.startVisitDate.isNullOrEmpty()){
                    c.time = converter.stringToDate(sharedViewModel.patientData.value?.startVisitDate!!)
                }
                var year = c.get(Calendar.YEAR)
                var month = c.get(Calendar.MONTH)
                var day = c.get(Calendar.DAY_OF_MONTH)


                var datePicker = DatePickerDialog(requireActivity(),{ view, year, monthOfYear, dayOfMonth ->

                    Log.e(AppConstants.TAG_NAME, "$year $monthOfYear $dayOfMonth")
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    sharedViewModel.setStartVisitDate(converter.dateToString(c.time)!!)
                    val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
                    binding.editDateOfVisit.setText(birthDateFormat.format(c.time))
                },year , month, day)
                var layoutParams = datePicker.window?.attributes
                if (layoutParams != null) {
                    Log.e(TAG_NAME, "Apply new size to datepicker")
                    layoutParams.width = layoutParams.width * 2
                    layoutParams.height =layoutParams.height * 2
                    datePicker.window?.setLayout(layoutParams.width, layoutParams.height)
//                    datePicker.window?.attributes = layoutParams
                }

                datePicker.show()
            }
            editTimeOfVisit.setOnClickListener {
                var c = Calendar.getInstance()
                val converter: Converters = Converters()
                if(!sharedViewModel.patientData.value?.startVisitTime.isNullOrEmpty()){
                    c.time = converter.stringToTime(sharedViewModel.patientData.value?.startVisitTime!!)
                }
                val hourOfDay: Int = c.get(Calendar.HOUR_OF_DAY)
                val minute: Int = c.get(Calendar.MINUTE)
                TimePickerDialog(context,{ timePicker, hour, minute ->
                    c.set(Calendar.HOUR_OF_DAY, hour)
                    c.set(Calendar.MINUTE, minute)
                    editTimeOfVisit.setText(converter.timeToString(c.time))
                    sharedViewModel.setStartVisitTime(converter.timeToString(c.time)!!)
                },
                    hourOfDay,minute,true).show()
            }

            radioStartPointIsPrevPatient.setOnClickListener {
                sharedViewModel.setStartPoint(PREV_PATIENT_TEXT);
            }
            radioStartPointIsHotel.setOnClickListener {
                sharedViewModel.setStartPoint(HOTEL_TEXT)
            }
            radioCurrentAddressSameNo.setOnClickListener {
                sharedViewModel.setCurrentAddressSame(NO_TEXT)
            }
            radioCurrentAddressSameYes.setOnClickListener {
                sharedViewModel.setCurrentAddressSame(YES_TEXT)
            }
            radioCurrentPatientVisitThisShiftYes.setOnClickListener{
                sharedViewModel.setCurrentPatientAlreadyVisited(YES_TEXT)
            }
            radioCurrentPatientVisitThisShiftNo.setOnClickListener {
                sharedViewModel.setCurrentPatientAlreadyVisited(NO_TEXT)
            }
            btnNext.setOnClickListener {
                findNavController().navigate(R.id.action_patientLogisticsDetailsFragment_to_patientDoctorDocumentFragment)
            }
            btnPrev.setOnClickListener {
                activity?.onBackPressed()
            }
            btnSave.setOnClickListener {
                sharedViewModel.patientData.value?.let { it1 ->
                    sharedViewModel.savePatient(it1)
                    activity?.finish()
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
        val converters: Converters = Converters()
        sharedViewModel.patientData.observe(viewLifecycleOwner, Observer {
            Log.e(AppConstants.TAG_NAME, "Shared Vide Model Data Changed in Insurance fragment")
            if(sharedViewModel.patientData.value?.birthDate != null){
                val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
                val cal = Calendar.getInstance()
                cal.time = sharedViewModel.patientData.value?.birthDate
                val year = cal[Calendar.YEAR]
                val month = cal[Calendar.MONTH]
                val day = cal[Calendar.DAY_OF_MONTH]
                binding.textPatientInfo.setText("${sharedViewModel.patientData.value?.lastName} ${sharedViewModel.patientData.value?.firstName} $day, ${month + 1}, $year")
            }
            else{
                binding.textPatientInfo.setText("${sharedViewModel.patientData.value?.lastName} ${sharedViewModel.patientData.value?.firstName} ")
            }

            binding.editTimeOfVisit.setText(sharedViewModel.patientData.value?.startVisitTime)
            if(!sharedViewModel.patientData.value?.startVisitDate.isNullOrEmpty()) {
                binding.editDateOfVisit.setText(
                    birthDateFormat.format(
                        converters.stringToDate(
                            sharedViewModel.patientData.value?.startVisitDate
                        )
                    )
                )
            }
            if(sharedViewModel.patientData.value?.startPoint == HOTEL_TEXT){
                binding.radioStartPointIsHotel.isChecked = true
            }
            else{
                binding.radioStartPointIsPrevPatient.isChecked = true
            }

            if(sharedViewModel.patientData.value?.sameAddAsPrev == YES_TEXT){
                binding.radioCurrentAddressSameYes.isChecked = true
            }
            else{
                binding.radioCurrentAddressSameNo.isChecked = true
            }

            if(sharedViewModel.patientData.value?.alreadyVisitedDuringThisShift == YES_TEXT){
                binding.radioCurrentPatientVisitThisShiftYes.isChecked = true
            }
            else{
                binding.radioCurrentPatientVisitThisShiftNo.isChecked = true
            }
        })
    }

}