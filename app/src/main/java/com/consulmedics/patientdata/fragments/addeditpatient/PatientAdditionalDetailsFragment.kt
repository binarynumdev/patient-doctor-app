package com.consulmedics.patientdata.fragments.addeditpatient

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.drawToBitmap
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.consulmedics.patientdata.Converters
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientAdditionalDetailsBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.utils.AppUtils
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import com.github.gcacace.signaturepad.views.SignaturePad
import java.text.SimpleDateFormat
import java.util.*


class PatientAdditionalDetailsFragment : Fragment() {
    private var _binding: FragmentPatientAdditionalDetailsBinding? = null
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
        Log.e(AppConstants.TAG_NAME, "ONCREATVIEW")
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
            signView.setOnClickListener {
                val builder = AlertDialog.Builder(requireActivity())
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.dialog_signature, null)
                val signPad = dialogLayout.findViewById<SignaturePad>(R.id.signPad)

                builder.setView(dialogLayout)
                builder.setNegativeButton(R.string.cancel, null)
                builder.setPositiveButton(
                    R.string.ok,null)
                builder.setNeutralButton(
                    R.string.clear_sign, null
                )

                val alertDialog = builder.create()
                alertDialog.setOnShowListener {dialog->
                    val button: Button =
                        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                    button.setOnClickListener(View.OnClickListener { // TODO Do something
                        binding.signView.setImageBitmap(signPad.transparentSignatureBitmap)
                        val svgStr = signPad.signatureSvg
                        val newBM: Bitmap = AppUtils.svgStringToBitmap(svgStr)
                        binding.signView.setImageBitmap(newBM)
                        sharedViewModel.setSignature(svgStr)
                        dialog.dismiss()
                        Log.e(TAG_NAME, svgStr)
                    })
                    val clearButton: Button = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                    clearButton.setOnClickListener{
                        signPad.clear()
                    }
                }
                alertDialog.show()
                val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
                val height = (resources.displayMetrics.heightPixels * 0.45).toInt()
                alertDialog.getWindow()?.setLayout(width, height)
            }
            btnNext.setOnClickListener {
                if(sharedViewModel.patientData.value?.isValidAdditionalDetails() == true){
                    findNavController().navigate(R.id.action_patientAdditionalDetailsFragment_to_patientSummaryFragment)
                }
                else{
                    Toast.makeText(context, R.string.error_in_validate_personal_details_form, Toast.LENGTH_LONG).show()
                }

            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.patientData.observe(viewLifecycleOwner, Observer {
            Log.e(AppConstants.TAG_NAME, "Shared Vide Model Data Changed in Additional fragment")
            val converters: Converters = Converters()
            val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
            if(!sharedViewModel.patientData.value?.dateofExam.isNullOrEmpty()){
                binding.editDateOfExam.setText(birthDateFormat.format(converters.stringToDate(sharedViewModel.patientData.value?.dateofExam)))
            }

            binding.editTimeOfExam.setText(sharedViewModel.patientData.value?.timeOfExam)
            binding.editKillometer.setText(sharedViewModel.patientData.value?.killometers)
            binding.editDiagnosis.setText(sharedViewModel.patientData.value?.diagnosis)
            binding.editHealthStatus.setText(sharedViewModel.patientData.value?.healthStatus)
            sharedViewModel.patientData.value?.signature?.let { it1 ->

                if(it1.isNotEmpty()){
                    val newBM:Bitmap = AppUtils.svgStringToBitmap(it1)
                    binding.signView.setImageBitmap(newBM)
                }

            }
        })
    }
}