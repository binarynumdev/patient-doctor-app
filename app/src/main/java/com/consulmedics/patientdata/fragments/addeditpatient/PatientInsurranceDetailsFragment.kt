package com.consulmedics.patientdata.fragments.addeditpatient

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.databinding.FragmentPatientInsurranceDetailsBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.AppUtils
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PatientInsurranceDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PatientInsurranceDetailsFragment : BaseAddEditPatientFragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentPatientInsurranceDetailsBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientInsurranceDetailsBinding.inflate(inflater, container, false)
        _binding?.apply {
            editInsurranceName.doAfterTextChanged {
                sharedViewModel.setInsuranceName(it.toString())
            }
            editInsurranceNumber.doAfterTextChanged {
                sharedViewModel.setInsuranceNumber(it.toString())
            }
            editInsurranceStatus.doAfterTextChanged {
                sharedViewModel.setInsuranceStatus(it.toString())
            }
            editPatientID.doAfterTextChanged {
                sharedViewModel.setPatientID(it.toString())
            }

            btnContinue.setOnClickListener {
                findNavController().navigate(R.id.action_patientInsurranceDetailsFragment_to_patientDoctorDocumentFragment)

            }
            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }



        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.patientData.observe(viewLifecycleOwner, Observer {
            Log.e(AppConstants.TAG_NAME, "Shared Vide Model Data Changed in Insurance fragment")
            binding.editInsurranceName.setText(sharedViewModel.patientData.value?.insuranceName)
            binding.editInsurranceNumber.setText(sharedViewModel.patientData.value?.insuranceNumber)
            binding.editInsurranceStatus.setText(sharedViewModel.patientData.value?.insuranceStatus)
            binding.editPatientID.setText(sharedViewModel.patientData.value?.patientID)
            sharedViewModel.patientData.value?.signPatient?.let { it1 ->

                if(it1.isNotEmpty()){
                    val newBM:Bitmap = AppUtils.svgStringToBitmap(it1)
//                    binding.signPatientImg.setImageBitmap(newBM)
                }

            }
        })
        sharedViewModel.printResult.observe(viewLifecycleOwner, Observer {
            when(it){
                is BaseResponse.Success ->{
                   var pdfFile = it.data?.result_file
                    if(pdfFile != null){
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
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
                else -> {}
            }
        })
    }
}