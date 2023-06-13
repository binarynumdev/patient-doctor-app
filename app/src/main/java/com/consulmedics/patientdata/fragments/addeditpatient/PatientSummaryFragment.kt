package com.consulmedics.patientdata.fragments.addeditpatient

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientSummaryBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.AppUtils
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import com.github.gcacace.signaturepad.views.SignaturePad
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PatientSummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PatientSummaryFragment : BaseAddEditPatientFragment() {

    private var _binding: FragmentPatientSummaryBinding? = null
    private val sharedViewModel: AddEditPatientViewModel by activityViewModels(){
        AddEditPatientViewModelFactory(MyApplication.patientRepository!!, MyApplication.hotelRepository!!, MyApplication.addressRepository!!)
    }
    val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel.patientData.observe(this, Observer {
            Log.e(AppConstants.TAG_NAME, "Shared Vide Model Data Changed in Summary Fragment")
            if(it.birthDate != null){
                val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
                val cal = Calendar.getInstance()
                cal.time = it.birthDate
                val year = cal[Calendar.YEAR]
                val month = cal[Calendar.MONTH]
                val day = cal[Calendar.DAY_OF_MONTH]
                binding.topBar.textViewLeft.setText("${it.lastName},${it.firstName}($day.${month + 1}.$year)")
            }
            else{
                binding.topBar.textViewLeft.setText("${it.lastName},${it.firstName} ")
            }

                if(it.signature.isNotEmpty()){
                    val newBM:Bitmap = AppUtils.svgStringToBitmap(it.signature)
                    binding.imageSignView.setImageBitmap(newBM)
                }


        })
        sharedViewModel.isLoading.observe(this,{

        })
        sharedViewModel.isFinished.observe(this, {
            if(it)
                activity?.finish()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(AppConstants.TAG_NAME, "ONCREATVIEW")
        // Inflate the layout for this fragment
        _binding = FragmentPatientSummaryBinding.inflate(inflater, container, false)
        binding.imageSignView.setOnClickListener {
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
                    binding.imageSignView.setImageBitmap(signPad.transparentSignatureBitmap)
                    val svgStr = signPad.signatureSvg
                    val newBM: Bitmap = AppUtils.svgStringToBitmap(svgStr)
                    binding.imageSignView.setImageBitmap(newBM)
                    sharedViewModel.setSignature(svgStr)
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
        binding.btnSave.setOnClickListener {
            sharedViewModel.patientData.value?.let { it1 ->
                it.isEnabled = false

                sharedViewModel.viewModelScope.launch {
                    sharedViewModel.savePatient(it1)
                }
//                activity?.finish()
            }
        }
        binding.btnPrev.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.topBar.buttonRight1.visibility = GONE
        return binding.root
    }
}