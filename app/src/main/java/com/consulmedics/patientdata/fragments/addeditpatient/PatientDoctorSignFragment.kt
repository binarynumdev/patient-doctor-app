package com.consulmedics.patientdata.fragments.addeditpatient

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientDoctorSignBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.AppUtils
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import com.github.gcacace.signaturepad.views.SignaturePad
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PatientDoctorSignFragment : BaseAddEditPatientFragment() {
    private var _binding: FragmentPatientDoctorSignBinding? = null
    val binding get() = _binding!!
    private val sharedViewModel: AddEditPatientViewModel by activityViewModels(){
        AddEditPatientViewModelFactory(MyApplication.patientRepository!!, MyApplication.hotelRepository!!, MyApplication.addressRepository!!)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPatientDoctorSignBinding.inflate(inflater, container, false)
        binding.apply {
            signPatientImg.setOnClickListener {
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
                        binding.signPatientImg.setImageBitmap(signPad.transparentSignatureBitmap)
                        val svgStr = signPad.signatureSvg
                        val newBM: Bitmap = AppUtils.svgStringToBitmap(svgStr)
                        binding.signPatientImg.setImageBitmap(newBM)
                        sharedViewModel.setSignPatient(svgStr)
                        dialog.dismiss()
                        Log.e(AppConstants.TAG_NAME, svgStr)
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
            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }
            btnSave.setOnClickListener {
                sharedViewModel.patientData.value?.let { it1 ->
                    it.isEnabled = false

                    sharedViewModel.viewModelScope.launch {
                        sharedViewModel.savePatient(it1)
                    }
                    activity?.finish()
                }
            }
            btnContinue.setOnClickListener {
                if(sharedViewModel.patientData.value?.signPatient.isNullOrEmpty()){
                    Toast.makeText(requireContext(), R.string.error_no_patient_sign, Toast.LENGTH_SHORT).show()
                }
                else{
                    findNavController().navigate(R.id.action_patientDoctorSignFragment_to_patientLogisticsDetailsFragment)
                }
            }
            topBar.apply {
                buttonRight1.visibility = GONE
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.patientData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(sharedViewModel.patientData.value?.birthDate != null){
                val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
                val cal = Calendar.getInstance()
                cal.time = sharedViewModel.patientData.value?.birthDate
                val year = cal[Calendar.YEAR]
                val month = cal[Calendar.MONTH]
                val day = cal[Calendar.DAY_OF_MONTH]
                binding.topBar.textViewLeft.setText("${it.lastName},${it.firstName}($day.${month + 1}.$year)")
            }
            else{
                binding.topBar.textViewLeft.setText("${it.lastName},${it.firstName} ")
            }

            sharedViewModel.patientData.value?.signPatient?.let { it1 ->

                if(it1.isNotEmpty()){
                    val newBM:Bitmap = AppUtils.svgStringToBitmap(it1)
                    binding.signPatientImg.setImageBitmap(newBM)
                }

            }
        })
    }

}