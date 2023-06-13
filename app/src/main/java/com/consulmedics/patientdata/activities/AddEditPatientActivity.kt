package com.consulmedics.patientdata.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.components.LeftStepperAdapter
import com.consulmedics.patientdata.components.MainStepper
import com.consulmedics.patientdata.components.StepperCallback
import com.consulmedics.patientdata.components.models.StepItem
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.databinding.ActivityAddEditPatientBinding
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME


class AddEditPatientActivity : BaseActivity() , StepperCallback{
    private var patient: Patient? = null
    private lateinit var binding: ActivityAddEditPatientBinding
    private lateinit var pageStepper: MainStepper
    private lateinit var navController: NavController
    private var pageTitleList: List <StepItem> = listOf ()
    private var isLeftStepperInitialized = false
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
        pageStepper.go(tabIndex)
        if(isLeftStepperInitialized)
            binding.leftStepper.setCurrentIndex(tabIndex)
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
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_edit_patient)
        binding = ActivityAddEditPatientBinding.inflate(layoutInflater)


        pageTitleList = listOf<StepItem>(
            StepItem(getString(R.string.patient_data), getString(R.string.read_card)),
            StepItem(getString(R.string.insurrance_details)),
            StepItem(getString(R.string.patient_sign)),
            StepItem(getString(R.string.logistic_data)),
            StepItem(getString(R.string.doctor_document)),
            StepItem(getString(R.string.additional_details)),
            StepItem(getString(R.string.receipts)),
            StepItem(getString(R.string.sign_doctor)))


        pageStepper = binding.MainStepper
        pageStepper.setCallback(this)
        pageStepper.setPageList(pageTitleList)
        pageStepper.go(0)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_patient_flow) as NavHostFragment
        navController = navHostFragment.navController
        patient = intent.getSerializableExtra("patient_data") as Patient
        navController.setGraph(R.navigation.add_edit_navigation, intent.extras)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener (listener)

        val leftStepperAdapter = LeftStepperAdapter(applicationContext, this)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@AddEditPatientActivity, drawerLayout, R.string.patient_data, R.string.patient_data)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            leftStepper.initStepper(leftStepperAdapter)
            leftStepperAdapter.updateList(pageTitleList)
            leftStepper.setCurrentIndex(0)
            isLeftStepperInitialized = true

        }

        supportActionBar?.hide()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun stepperRootViewClicked() {
        Log.e(TAG_NAME, "Callback on the activity side")
        val drawer = binding.drawerLayout
        drawer.openDrawer(GravityCompat.START)

    }

    override fun onStepItemClicked(index: Int) {
        pageStepper.go(index)
        binding.leftStepper.setCurrentIndex(index)

    }

    override fun stepActionButtonClicked() {

    }

}