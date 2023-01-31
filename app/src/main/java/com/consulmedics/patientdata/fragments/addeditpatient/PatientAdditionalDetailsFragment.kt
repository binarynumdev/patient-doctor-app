package com.consulmedics.patientdata.fragments.addeditpatient

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.consulmedics.patientdata.Converters
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientAdditionalDetailsBinding
import com.consulmedics.patientdata.databinding.FragmentPatientInsurranceDetailsBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import java.text.SimpleDateFormat
import java.util.*

class PatientAdditionalDetailsFragment : Fragment() {
    private var _binding: FragmentPatientAdditionalDetailsBinding? = null
    val binding get() = _binding!!
    private val sharedViewModel: AddEditPatientViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel.patientData.observe(this, Observer {
            Log.e(AppConstants.TAG_NAME, "Shared Vide Model Data Changed in Additional fragment")
            binding.editDateOfExam.setText(sharedViewModel.patientData.value?.dateofExam)
            binding.editTimeOfExam.setText(sharedViewModel.patientData.value?.timeOfExam)
            binding.editKillometer.setText(sharedViewModel.patientData.value?.killometers)
            binding.editDiagnosis.setText(sharedViewModel.patientData.value?.diagnosis)
            binding.editHealthStatus.setText(sharedViewModel.patientData.value?.healthStatus)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPatientAdditionalDetailsBinding.inflate(inflater, container, false)
        _binding?.apply {
            editDateOfExam.setOnClickListener {
                var c = Calendar.getInstance()
                val converter: Converters = Converters()
                if(!sharedViewModel.patientData.value?.dateofExam.isNullOrEmpty()){
                    c.time = converter.stringToDate(sharedViewModel.patientData.value?.dateofExam!!)
                }
                var year = c.get(Calendar.YEAR)
                var month = c.get(Calendar.MONTH)
                var day = c.get(Calendar.DAY_OF_MONTH)


                DatePickerDialog(requireActivity(),{ view, year, monthOfYear, dayOfMonth ->

                    Log.e(AppConstants.TAG_NAME, "$year $monthOfYear $dayOfMonth")
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    sharedViewModel.patientData.value?.dateofExam = converter.dateToString(c.time)!!
                    val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
                    binding.editDateOfExam.setText(birthDateFormat.format(c.time))
                },year , month, day).show()
            }
            editTimeOfExam.setOnClickListener {
                var c = Calendar.getInstance()
                val converter: Converters = Converters()
                if(!sharedViewModel.patientData.value?.timeOfExam.isNullOrEmpty()){
                    c.time = converter.stringToTime(sharedViewModel.patientData.value?.timeOfExam!!)
                }
                val hourOfDay: Int = c.get(Calendar.HOUR_OF_DAY)
                val minute: Int = c.get(Calendar.MINUTE)
                TimePickerDialog(context,{ timePicker, hour, minute ->
                        c.set(Calendar.HOUR_OF_DAY, hour)
                        c.set(Calendar.MINUTE, minute)
                        editTimeOfExam.setText(converter.timeToString(c.time))
                        sharedViewModel.setTimeOfExam(converter.timeToString(c.time)!!)
                    },
                hourOfDay,minute,true).show()
            }
            editKillometer.doAfterTextChanged {
                sharedViewModel.setKillometer(it.toString())
            }
            editDiagnosis.doAfterTextChanged {
                sharedViewModel.setDiagnosis(it.toString())
            }
            editHealthStatus.doAfterTextChanged {
                sharedViewModel.setHealthStatus(it.toString())
            }

        }
        return binding.root
    }

}