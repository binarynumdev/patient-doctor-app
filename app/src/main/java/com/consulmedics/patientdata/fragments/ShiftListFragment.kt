package com.consulmedics.patientdata.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.activities.BaseActivity
import com.consulmedics.patientdata.databinding.FragmentPatientListBinding
import com.consulmedics.patientdata.databinding.FragmentShiftListBinding

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
        return binding.root
    }

}