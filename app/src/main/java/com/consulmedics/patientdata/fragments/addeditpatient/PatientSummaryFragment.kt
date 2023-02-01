package com.consulmedics.patientdata.fragments.addeditpatient

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.caverock.androidsvg.SVG
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientSummaryBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.AppUtils
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PatientSummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PatientSummaryFragment : Fragment() {

    private var _binding: FragmentPatientSummaryBinding? = null
    private val sharedViewModel: AddEditPatientViewModel by activityViewModels()
    val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel.patientData.observe(this, Observer {
            Log.e(AppConstants.TAG_NAME, "Shared Vide Model Data Changed in Summary Fragment")
            binding.textFullName.setText("${sharedViewModel.patientData.value?.firstName} ${sharedViewModel.patientData.value?.lastName}")
            sharedViewModel.patientData.value?.signature?.let { it1 ->

                if(it1.isNotEmpty()){
                    val newBM:Bitmap = AppUtils.svgStringToBitmap(it1)
                    binding.imageSignView.setImageBitmap(newBM)
                }

            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(AppConstants.TAG_NAME, "ONCREATVIEW")
        // Inflate the layout for this fragment
        _binding = FragmentPatientSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }
}