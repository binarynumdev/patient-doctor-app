package com.consulmedics.patientdata.fragments.patients

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.activities.AddEditPatientActivity
import com.consulmedics.patientdata.activities.BaseActivity
import com.consulmedics.patientdata.activities.PatientDetailsActivity
import com.consulmedics.patientdata.adapters.PatientAdapter
import com.consulmedics.patientdata.adapters.PatientItemClickInterface
import com.consulmedics.patientdata.databinding.FragmentPatientListBinding
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.viewmodels.PatientViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    lateinit var mainActivity: BaseActivity
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientListBinding.inflate(inflater, container, false)
        mainActivity = requireActivity() as BaseActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val patientAdapter = PatientAdapter(requireContext(), this)
        binding.listPatients.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = patientAdapter

        }
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.allPatients.observe(viewLifecycleOwner, Observer {
                mainActivity.showLoadingSpinner("Loading", "Please wait while loading patients.")
                Thread(Runnable {
                    // Do background task
                    it.forEach {
                        it.decryptFields()
                    }


                    // Update UI on the main thread
                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        patientAdapter.updateList(it)
                        mainActivity.hideLoadingSpinner()
                    }
                }).start()


            })
        }

    }

    override fun onPatientRemoveClick(patient: Patient) {
        Log.e(TAG_NAME, "Remove Event Handler")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.warning))
        builder.setMessage(R.string.confirm_remove_patient)
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
//            Toast.makeText(requireContext(),
//                android.R.string.yes, Toast.LENGTH_SHORT).show()
            viewModel.deletePatient(patient)
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
//            Toast.makeText(requireContext(),
//                android.R.string.no, Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    override fun onPatientEditClick(patient: Patient) {
        Log.e(TAG_NAME, "Edit Event Handler")

        startActivity(Intent(requireContext(), AddEditPatientActivity::class.java).apply {
            putExtra("patient_data", patient)
        })
    }

    override fun onPatientItemClick(patient: Patient) {
        Log.e(TAG_NAME, "View Event Handler")
        startActivity(Intent(requireContext(), PatientDetailsActivity::class.java).apply {
            putExtra("patient_data", patient)
        })
    }

}