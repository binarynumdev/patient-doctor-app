package com.consulmedics.patientdata.fragments.patients

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.consulmedics.patientdata.adapters.PatientAdapter
import com.consulmedics.patientdata.adapters.PatientItemClickInterface
import com.consulmedics.patientdata.databinding.FragmentPatientListBinding
import com.consulmedics.patientdata.models.Patient
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.viewmodels.PatientViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PatientListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PatientListFragment : Fragment(), PatientItemClickInterface {

    private  val viewModel: PatientViewModel by viewModels()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val patientAdapter = PatientAdapter(requireContext(), this)
        binding.listPatients.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = patientAdapter
        }
        viewModel.allPatients.observe(viewLifecycleOwner, Observer {
            Log.e(TAG_NAME, "ONVIEWCREATED: ${it.count()}")
            patientAdapter.updateList(it)
        })
    }

    override fun onPatientRemoveClick(patient: Patient) {
        Log.e(TAG_NAME, "Remove Event Handler")
    }

    override fun onPatientEditClick(patient: Patient) {
        Log.e(TAG_NAME, "Edit Event Handler")
    }

    override fun onPatientItemClick(patient: Patient) {
        Log.e(TAG_NAME, "View Event Handler")
    }

}