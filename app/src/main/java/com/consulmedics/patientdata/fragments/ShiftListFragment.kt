package com.consulmedics.patientdata.fragments

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
import com.consulmedics.patientdata.adapters.ShiftAdapter
import com.consulmedics.patientdata.adapters.ShiftPageAdapter
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.databinding.FragmentShiftListBinding
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.utils.AppUtils
import com.consulmedics.patientdata.viewmodels.ShiftViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    private var _binding: FragmentShiftListBinding? = null
    lateinit var mainActivity: BaseActivity

    private  val viewModel: ShiftViewModel by viewModels()
    val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShiftListBinding.inflate(inflater, container, false)
        mainActivity = requireActivity() as BaseActivity
//        binding.listShifts.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = shiftAdapter
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val adapter = ShiftPageAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            tab.text = when(position){
                0 -> getString(R.string.past_shifts)
                1 -> getString(R.string.upcoming_shifts)
                else -> ""
            }
        }.attach()
        viewModel.loadShiftResult.observe(viewLifecycleOwner) {
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

        if(AppUtils.isOnline(requireContext())){
            viewModel.loadShiftDetails()
        }
        else{

        }
//

    }

}