package com.consulmedics.patientdata.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.activities.BaseActivity
import com.consulmedics.patientdata.activities.EditPatientShiftActivity
import com.consulmedics.patientdata.adapters.ShiftAdapter
import com.consulmedics.patientdata.adapters.ShiftItemClickInterface
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.data.model.PatientShift
import com.consulmedics.patientdata.databinding.FragmentSubShiftListBinding
import com.consulmedics.patientdata.utils.AppConstants.PAST_TABS
import com.consulmedics.patientdata.utils.AppConstants.PATIENT_SHIFT_DATA
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.utils.AppConstants.UPCOMING_TABS
import com.consulmedics.patientdata.utils.AppConstants.UPLOADED_TABS
import com.consulmedics.patientdata.viewmodels.ShiftViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val SHIFT_OPTION = "shift_option"

/**
 * A simple [Fragment] subclass.
 * Use the [SubShiftListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SubShiftListFragment() : Fragment(), ShiftItemClickInterface {
    private var shiftOption: String? = null
    lateinit var mainActivity: BaseActivity
    private var _binding: FragmentSubShiftListBinding? = null
    val binding get() = _binding!!
    lateinit var shiftAdapter: ShiftAdapter
    private  val viewModel: ShiftViewModel by viewModels()

    companion object {
        fun newInstance(shiftOption: String): SubShiftListFragment {
            val fragment = SubShiftListFragment()
            val args = Bundle()
            args.putString(SHIFT_OPTION, shiftOption)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shiftOption = arguments?.getString(SHIFT_OPTION)

        shiftAdapter = ShiftAdapter(requireContext(), this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubShiftListBinding.inflate(inflater, container, false)

        binding.listShifts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = shiftAdapter
        }
        mainActivity = requireActivity() as BaseActivity
        return binding.root
    }

//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//
//        if (view != null) {
//            CoroutineScope(Dispatchers.Main).launch {
//                if(shiftOption?.equals(PAST_TABS) == true){
//                    Log.e(TAG_NAME, "PAST TABS")
//
//                    viewModel.pastShiftList.observe(viewLifecycleOwner, Observer{
//                        shiftAdapter.updateList(it)
//                    })
//                }
//                else if (shiftOption?.equals(UPCOMING_TABS) == true){
//                    Log.e(TAG_NAME, "UPCOMING TABS")
//                    viewModel.upcomingShiftList.observe(viewLifecycleOwner, Observer{
//                        shiftAdapter.updateList(it)
//                    })
//                }
//            }
//
//            viewModel.uploadShiftResult.observe(viewLifecycleOwner) {
//                when (it) {
//                    is BaseResponse.Loading -> {
//                        mainActivity.showLoadingSpinner("Loading", "Please wait while uploading shift details")
//                    }
//
//                    is BaseResponse.Success -> {
//                        mainActivity.hideLoadingSpinner()
//                    }
//                    is BaseResponse.SessionError ->{
//                        Log.e(TAG_NAME, "REDIRECT TO LOGOUT")
//                        Toast.makeText(context, R.string.wrong_session, Toast.LENGTH_LONG).show()
//                        mainActivity.hideLoadingSpinner()
//                        mainActivity.logout()
//                    }
//                    is BaseResponse.Error -> {
//                        Log.e(TAG_NAME, "API ERROR :${it.msg}")
//                        mainActivity.hideLoadingSpinner()
//                    }
//                    else -> {
//                        mainActivity.hideLoadingSpinner()
//                    }
//                }
//            }
//        }
//    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(view != null) {
            CoroutineScope(Dispatchers.Main).launch {
                if(shiftOption?.equals(PAST_TABS) == true){
                    Log.e(TAG_NAME, "PAST TABS")

                    viewModel.pastShiftList.observe(viewLifecycleOwner, Observer{
                        shiftAdapter.updateList(it, false)
                    })
                }
                else if (shiftOption?.equals(UPCOMING_TABS) == true){
                    Log.e(TAG_NAME, "UPCOMING TABS")

                    viewModel.upcomingShiftList.observe(viewLifecycleOwner, Observer{
                        shiftAdapter.updateList(it, false)
                    })
                }
                else if(shiftOption?.equals(UPLOADED_TABS) == true){

                    viewModel.upLoadedShiftList.observe(viewLifecycleOwner, Observer{
                        shiftAdapter.updateList(it, true)
                    })
                }


            }

            if (view != null) {
                viewModel.uploadShiftResult.observe(viewLifecycleOwner) {
                    when (it) {
                        is BaseResponse.Loading -> {
                            mainActivity.showLoadingSpinner("Loading", "Please wait while uploading shift details")
                        }

                        is BaseResponse.Success -> {
                            mainActivity.hideLoadingSpinner()
                            Toast.makeText(context, "Upload Success", Toast.LENGTH_LONG).show()
                        }
                        is BaseResponse.SessionError ->{
                            Log.e(TAG_NAME, "REDIRECT TO LOGOUT")
                            Toast.makeText(context, R.string.wrong_session, Toast.LENGTH_LONG).show()
                            mainActivity.hideLoadingSpinner()
                            mainActivity.logout()
                        }
                        is BaseResponse.Error -> {
                            Log.e(TAG_NAME, "API ERROR :${it.msg}")
                            mainActivity.hideLoadingSpinner()
                            Toast.makeText(context, "Upload Failed", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            mainActivity.hideLoadingSpinner()
                        }
                    }
                }
            }
        }
    }

    override fun onShiftEditClick(
        patient: PatientShift
    ) {
        startActivity(Intent(requireContext(), EditPatientShiftActivity::class.java).apply {
            putExtra(PATIENT_SHIFT_DATA, patient)
        })
    }

    override fun onShiftUploadClick(currentShift: PatientShift) {
        Log.e("Upload Button Clicked!", "Clicked")
        viewModel.uploadShift(currentShift)
    }
}