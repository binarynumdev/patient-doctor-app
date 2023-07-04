package com.consulmedics.patientdata.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.consulmedics.patientdata.adapters.ShiftAdapter
import com.consulmedics.patientdata.databinding.FragmentSubShiftListBinding
import com.consulmedics.patientdata.utils.AppConstants.PAST_TABS
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.utils.AppConstants.UPCOMING_TABS
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
class SubShiftListFragment(shiftOption: String) : Fragment() {
    private var shiftOption: String? = shiftOption
    private var _binding: FragmentSubShiftListBinding? = null
    val binding get() = _binding!!
    lateinit var shiftAdapter: ShiftAdapter
    private  val viewModel: ShiftViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        shiftAdapter = ShiftAdapter(requireContext())
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            if(shiftOption?.equals(PAST_TABS) == true){
                Log.e(TAG_NAME, "PAST TABS")
                viewModel.pastShiftList.observe(viewLifecycleOwner, Observer{
                    shiftAdapter.updateList(it)
                })
            }
            else if (shiftOption?.equals(UPCOMING_TABS) == true){
                Log.e(TAG_NAME, "UPCOMING TABS")
                viewModel.upcomingShiftList.observe(viewLifecycleOwner, Observer{
                    shiftAdapter.updateList(it)
                })
            }

        }
    }

}