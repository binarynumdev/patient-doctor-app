package com.consulmedics.patientdata.fragments.patients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.FragmentPatientAdditionalDetailsBinding
import com.consulmedics.patientdata.databinding.FragmentPatientListBinding
import com.consulmedics.patientdata.ui.main.PlaceholderFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PatientListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PatientListFragment : Fragment() {


    private var _binding: FragmentPatientListBinding? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientListBinding.inflate(inflater, container, false)
        return binding.root
    }

}