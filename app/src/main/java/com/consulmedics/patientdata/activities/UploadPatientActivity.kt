package com.consulmedics.patientdata.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.adapters.ShiftAdapter
import com.consulmedics.patientdata.adapters.ShiftItemClickInterface
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.data.model.PatientShift
import com.consulmedics.patientdata.databinding.ActivityUploadPatientBinding
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.AppUtils
import com.consulmedics.patientdata.viewmodels.ShiftViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UploadPatientActivity : BaseActivity() {
    private lateinit var binding: ActivityUploadPatientBinding
    private  val viewModel: ShiftViewModel  by viewModels<ShiftViewModel>()
    lateinit var shiftAdapter: ShiftAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
        }
        if(AppUtils.isOnline(this)){
            viewModel.loadShiftDetails()
        }
        else{
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show()
            finish()
        }

        viewModel.loadShiftResult.observe(this) {
            when (it) {
                is BaseResponse.Loading -> {
                    showLoadingSpinner("Loading", "Please wait while load shift details")
                }

                is BaseResponse.Success -> {
                    Log.e(AppConstants.TAG_NAME, "API SUCCESS: ${it.data?.shiftList?.count()}")
                    loadShiftList()
                    hideLoadingSpinner()
                }

                is BaseResponse.Error -> {
                    Log.e(AppConstants.TAG_NAME, "API ERROR :${it.msg}")
                    hideLoadingSpinner()
                }
                else -> {
                    hideLoadingSpinner()
                }
            }
        }
    }
    private fun loadShiftList(){
        shiftAdapter = ShiftAdapter(this)
        binding.listShifts.apply {
            layoutManager = LinearLayoutManager(this@UploadPatientActivity)
            adapter = shiftAdapter
            CoroutineScope(Dispatchers.Main).launch {

                viewModel.pastShiftList.observe(this@UploadPatientActivity, Observer{
                    shiftAdapter.updateList(it, false)
                })

            }
        }
    }

}