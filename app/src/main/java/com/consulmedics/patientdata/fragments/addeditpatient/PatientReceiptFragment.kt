package com.consulmedics.patientdata.fragments.addeditpatient

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientReceiptBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.consulmedics.patientdata.activities.MapsActivity
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.utils.AppUtils
import kotlinx.coroutines.launch

class PatientReceiptFragment : BaseAddEditPatientFragment() {
    private var _binding: FragmentPatientReceiptBinding? = null

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
            addressForm.btnConfirmMap.setText(getString(R.string.address_from_patient_details))
            editMedikament1.doAfterTextChanged {
                sharedViewModel.setMedicals(1, it.toString())
            }
            editMedikament1.doOnTextChanged { text, start, count, after ->
                AppUtils.validateMaxLineMaxLetterForEditText(editMedikament1, text, requireContext());
            }
            editMedikament2.doAfterTextChanged {
                sharedViewModel.setMedicals(2, it.toString())
            }
            editMedikament2.doOnTextChanged { text, start, count, after ->
                AppUtils.validateMaxLineMaxLetterForEditText(editMedikament2, text, requireContext());
            }
            editMedikament3.doAfterTextChanged {
                sharedViewModel.setMedicals(3, it.toString())
            }
            editMedikament3.doOnTextChanged { text, start, count, after ->
                AppUtils.validateMaxLineMaxLetterForEditText(editMedikament3, text, requireContext());
            }
            editFirstName.doAfterTextChanged {
                sharedViewModel.setReceiptFirstName(it.toString())
            }
            editLastName.doAfterTextChanged {
                sharedViewModel.setReceiptLastName(it.toString())
            }
            editAdditionalInfo.doAfterTextChanged {
                sharedViewModel.setReceiptAdditionalInfo(it.toString())
            }
            addressForm.apply {
                editCity.doAfterTextChanged {
                    sharedViewModel.receiptAddress.value?.city = it.toString()
                }
                editStreet.doAfterTextChanged {
                    sharedViewModel.receiptAddress.value?.streetName = it.toString()
                }
                editHouseNumber.doAfterTextChanged {
                    sharedViewModel.receiptAddress.value?.streetNumber = it.toString()
                }
                editPostalCode.doAfterTextChanged {
                    sharedViewModel.receiptAddress.value?.postCode = it.toString()
                }
                btnConfirmMap.setOnClickListener {
                    sharedViewModel.setReceiptAddressFromPatientAddress()
                    editCity.setText(sharedViewModel.patientData.value?.city)
                    editStreet.setText(sharedViewModel.patientData.value?.street)
                    editHouseNumber.setText(sharedViewModel.patientData.value?.houseNumber)
                    editPostalCode.setText(sharedViewModel.patientData.value?.postCode)
                }
            }
            btnContinue.setOnClickListener {
                findNavController().navigate(R.id.action_patientReceiptFragment_to_patientSummaryFragment)
            }
            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }

            sharedViewModel.printResult.observe(viewLifecycleOwner, Observer {
                when(it){
                    is BaseResponse.Success ->{
                        var pdfFile = it.data?.result_file
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
                    else -> {}
                }
            })

        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.patientData.observe(viewLifecycleOwner, Observer {
            binding.editMedikament1.setText(sharedViewModel.patientData.value?.medicals1)
            binding.editMedikament2.setText(sharedViewModel.patientData.value?.medicals2)
            binding.editMedikament3.setText(sharedViewModel.patientData.value?.medicals3)
            binding.editFirstName.setText(it.receiptFirstName)
            binding.editLastName.setText(it.receiptLastName)
            binding.editAdditionalInfo.setText(it.receiptAdditionalInfo)
        })
        sharedViewModel.receiptAddress.observe(viewLifecycleOwner, Observer {
            binding.addressForm.apply {
                editCity.setText(it.city)
                editStreet.setText(it.streetName)
                editHouseNumber.setText(it.streetNumber)
                editPostalCode.setText(it.postCode)
            }
        })
    }
}