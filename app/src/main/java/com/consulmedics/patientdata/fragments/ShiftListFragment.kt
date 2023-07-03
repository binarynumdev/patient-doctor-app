package com.consulmedics.patientdata.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.activities.BaseActivity
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.databinding.FragmentPatientListBinding
import com.consulmedics.patientdata.databinding.FragmentShiftListBinding
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.utils.AppUtils
import com.consulmedics.patientdata.viewmodels.PatientViewModel
import com.consulmedics.patientdata.viewmodels.ShiftViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShiftListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShiftListFragment : Fragment() {
    private  val viewModel: ShiftViewModel by viewModels()
    private var _binding: FragmentShiftListBinding? = null
    lateinit var mainActivity: BaseActivity
    val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadShiftResult.observe(this) {
            when (it) {
                is BaseResponse.Loading -> {
                    mainActivity.showLoadingSpinner("Loading", "Please wait while load shift details")
                }

                is BaseResponse.Success -> {
                    Log.e(TAG_NAME, "API SUCCESS: ${it.data?.shiftList?.count()}")
                    mainActivity.hideLoadingSpinner()
                }

                is BaseResponse.Error -> {
                    Log.e(TAG_NAME, "API ERROR :${it.msg}")
                    mainActivity.hideLoadingSpinner()
                }
                else -> {
                    mainActivity.hideLoadingSpinner()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShiftListBinding.inflate(inflater, container, false)
        mainActivity = requireActivity() as BaseActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG_NAME, "SHIFT LIST")
        if(AppUtils.isOnline(requireContext())){
            viewModel.loadShiftDetails()
        }
        else{

        }
    }

}