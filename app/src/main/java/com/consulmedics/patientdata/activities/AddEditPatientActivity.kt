package com.consulmedics.patientdata.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.get
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.ActivityAddEditPatientBinding
import com.consulmedics.patientdata.databinding.ActivityMainBinding
import com.consulmedics.patientdata.models.Patient
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.vinay.stepview.models.Step

class AddEditPatientActivity : AppCompatActivity() {
    private var patient: Patient? = null
    private lateinit var binding: ActivityAddEditPatientBinding

    private lateinit var navController: NavController
    private var pageTitleList: List <Step> = listOf ()
    private val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
        var tabIndex: Int = 0
        if(destination.id == R.id.patientPersonalDetailsFragment){
            tabIndex = 0
        }
        else if (destination.id == R.id.patientInsurranceDetailsFragment){
            tabIndex = 1
        }
        else if (destination.id == R.id.patientLogisticsDetailsFragment){
            tabIndex = 2
        }
        for (i in 0 until pageTitleList.count()){

            if(i < tabIndex){
                binding.patientStepIndicator.setStepState(Step.State.COMPLETED, i )
                Log.e(TAG_NAME, "Complted STEP_${i}: ${i ==  tabIndex}: ${i < tabIndex}")
            }
            else if (i == tabIndex){
                binding.patientStepIndicator.setStepState(Step.State.CURRENT, i )
                Log.e(TAG_NAME, "Current STEP_${i}: ${i ==  tabIndex}: ${i < tabIndex}")
            }
            else{
                binding.patientStepIndicator.setStepState(Step.State.NOT_COMPLETED, i )
                Log.e(TAG_NAME, "Uncomplted STEP_${i}: ${i ==  tabIndex}: ${i < tabIndex}")
            }
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_edit_patient)
        binding = ActivityAddEditPatientBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_patient_flow) as NavHostFragment
        navController = navHostFragment.navController
        patient = intent.getSerializableExtra("patient_data") as Patient
        navController.setGraph(R.navigation.add_edit_navigation, intent.extras)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener (listener)

        binding.patientStepIndicator.apply {
            setTextSize(20)
            setNotCompletedStepTextColor(Color.DKGRAY)
            setNotCompletedLineColor(Color.DKGRAY)

            setCompletedLineColor(getColor(R.color.app_orange))
            setCompletedStepTextColor(getColor(R.color.app_blue))


            setCurrentStepTextColor(getColor(R.color.app_orange))
            setReverse(false)
        }
        pageTitleList = listOf<Step>(
            Step(getString(R.string.patient_data), Step.State.CURRENT),
            Step(getString(R.string.insurrance_details)),
            Step(getString(R.string.logistic_data)),
            Step(getString(R.string.doctor_document)),
            Step(getString(R.string.additional_details)),
            Step(getString(R.string.receipts)),
            Step(getString(R.string.sign_doctor)))
        binding.patientStepIndicator.setSteps(pageTitleList)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}