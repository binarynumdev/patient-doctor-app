package com.consulmedics.patientdata.fragments.addeditpatient

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientReceiptBinding
import com.consulmedics.patientdata.databinding.FragmentPatientSummaryBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

class PatientReceiptFragment : Fragment() {
    private var _binding: FragmentPatientReceiptBinding? = null
    private val sharedViewModel: AddEditPatientViewModel by activityViewModels(){
        AddEditPatientViewModelFactory(MyApplication.repository!!)
    }
    val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPatientReceiptBinding.inflate(inflater, container, false)
        binding.apply {
            editMedikament1.doAfterTextChanged {
                sharedViewModel.setMedicals(1, it.toString())
            }
            editMedikament2.doAfterTextChanged {
                sharedViewModel.setMedicals(2, it.toString())
            }
            editMedikament3.doAfterTextChanged {
                sharedViewModel.setMedicals(3, it.toString())
            }
            btnPrintReceipt.setOnClickListener {
                var pdfFile = sharedViewModel.printReciept()
                if(pdfFile != null){
                    val intent = Intent()
                    intent.action = ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    val uriPdfPath =
                        FileProvider.getUriForFile(requireContext(), requireActivity().applicationContext.packageName + ".provider", pdfFile)
                    Log.d("pdfPath", "" + uriPdfPath);
                    val pdfOpenIntent = Intent(Intent.ACTION_VIEW)
                    pdfOpenIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    pdfOpenIntent.clipData = ClipData.newRawUri("", uriPdfPath)
                    pdfOpenIntent.setDataAndType(uriPdfPath, "application/pdf")
                    pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                    try {
                        startActivity(pdfOpenIntent)
                    } catch (activityNotFoundException: ActivityNotFoundException) {
                        Toast.makeText(requireContext(), "There is no app to load corresponding PDF", Toast.LENGTH_LONG)
                            .show()
                    }
                }

            }
            btnNext.setOnClickListener {
                if(sharedViewModel.isValidMedicalReceipt() == true){
                    findNavController().navigate(R.id.action_patientReceiptFragment_to_patientSummaryFragment)
                }
                else{
                    Toast.makeText(context, R.string.error_in_validate_logistic_details, Toast.LENGTH_LONG).show()
                }
            }
            btnPrev.setOnClickListener {
                activity?.onBackPressed()
            }
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.patientData.observe(viewLifecycleOwner, Observer {
            if(sharedViewModel.patientData.value?.birthDate != null){
                val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
                val cal = Calendar.getInstance()
                cal.time = sharedViewModel.patientData.value?.birthDate
                val year = cal[Calendar.YEAR]
                val month = cal[Calendar.MONTH]
                val day = cal[Calendar.DAY_OF_MONTH]
                binding.textPatientInfo.setText("${sharedViewModel.patientData.value?.firstName} ${sharedViewModel.patientData.value?.firstName} $day, ${month + 1}, $year")
            }
            else{
                binding.textPatientInfo.setText("${sharedViewModel.patientData.value?.firstName} ${sharedViewModel.patientData.value?.firstName} ")
            }

            binding.editMedikament1.setText(sharedViewModel.patientData.value?.medicals1)
            binding.editMedikament2.setText(sharedViewModel.patientData.value?.medicals2)
            binding.editMedikament3.setText(sharedViewModel.patientData.value?.medicals3)
        })
    }
}