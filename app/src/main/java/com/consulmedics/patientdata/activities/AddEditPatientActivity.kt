package com.consulmedics.patientdata.activities

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.ActivityAddEditPatientBinding
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import com.shuhart.stepview.StepView
import com.vinay.stepview.models.Step

class AddEditPatientActivity : BaseActivity() {
    private var patient: Patient? = null
    private lateinit var binding: ActivityAddEditPatientBinding
    private lateinit var pageStepper: StepView
    private lateinit var navController: NavController
    private var pageTitleList: List <String> = listOf ()
    private val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
        var tabIndex: Int = 0
        if(destination.id == R.id.patientPersonalDetailsFragment){
            tabIndex = 0
        }
        else if (destination.id == R.id.patientInsurranceDetailsFragment){
            tabIndex = 1
        }
        else if (destination.id == R.id.patientDoctorSignFragment){
            tabIndex = 2
        }
        else if (destination.id == R.id.patientLogisticsDetailsFragment){
            tabIndex = 3
        }
        else if (destination.id == R.id.patientDoctorDocumentFragment){
            tabIndex = 4
        }
        else if (destination.id == R.id.patientAdditionalDetailsFragment){
            tabIndex = 5
        }
        else if (destination.id == R.id.patientReceiptFragment){
            tabIndex = 6
        }
        else if (destination.id == R.id.patientSummaryFragment){
            tabIndex = 7
        }
        pageStepper.go(tabIndex, true)
        for (i in 0 until pageTitleList.count()){

//            if(i < tabIndex){
//                binding.patientStepIndicator.setStepState(Step.State.COMPLETED, i )
//                Log.e(TAG_NAME, "Complted STEP_${i}: ${i ==  tabIndex}: ${i < tabIndex}")
//            }
//            else if (i == tabIndex){
//                binding.patientStepIndicator.setStepState(Step.State.CURRENT, i )
//                Log.e(TAG_NAME, "Current STEP_${i}: ${i ==  tabIndex}: ${i < tabIndex}")
//            }
//            else{
//                binding.patientStepIndicator.setStepState(Step.State.NOT_COMPLETED, i )
//                Log.e(TAG_NAME, "Uncomplted STEP_${i}: ${i ==  tabIndex}: ${i < tabIndex}")
//            }
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_edit_patient)
        binding = ActivityAddEditPatientBinding.inflate(layoutInflater)
        pageStepper = binding.patientStepIndicator as StepView
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_patient_flow) as NavHostFragment
        navController = navHostFragment.navController
        patient = intent.getSerializableExtra("patient_data") as Patient
        navController.setGraph(R.navigation.add_edit_navigation, intent.extras)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener (listener)


        pageTitleList = listOf<String>(
            getString(R.string.patient_data),
            getString(R.string.insurrance_details),
            getString(R.string.patient_sign),
            getString(R.string.logistic_data),
            getString(R.string.doctor_document),
            getString(R.string.additional_details),
            getString(R.string.receipts),
            getString(R.string.sign_doctor))


//        pageStepper.setupWithNavController(findNavController(R.id.add_edit_navigation))
        pageStepper.setSteps(pageTitleList)
        pageStepper.setOnStepClickListener {
            Log.e("STEPINDEX", "INDEX: ${it}")
            if(it == 0){
                navController.navigate(R.id.patientPersonalDetailsFragment)
            }
            else if (it == 1){
                navController.navigate(R.id.patientInsurranceDetailsFragment)
            }
            else if(it == 2){
                navController.navigate(R.id.patientDoctorSignFragment)
            }
            else if (it == 3){
                navController.navigate(R.id.patientLogisticsDetailsFragment)
            }
            else if (it == 4){
                navController.navigate(R.id.patientDoctorDocumentFragment)
            }
            else if (it == 5){
                navController.navigate(R.id.patientAdditionalDetailsFragment)
            }
            else if (it == 6){
                navController.navigate(R.id.patientReceiptFragment)
            }
            else if (it == 7){
                navController.navigate(R.id.patientSummaryFragment)
            }
        }


//        binding.patientStepIndicator.setSteps(pageTitleList)
//        binding.patientStepIndicator.setupWithNavController(findNavController(R.id.add_edit_navigation))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}