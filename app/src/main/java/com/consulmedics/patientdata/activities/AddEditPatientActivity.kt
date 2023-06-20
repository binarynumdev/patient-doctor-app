package com.consulmedics.patientdata.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.consulmedics.patientdata.MyApplication
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.components.ConfirmationDialog
import com.consulmedics.patientdata.components.LeftStepperAdapter
import com.consulmedics.patientdata.components.MainStepper
import com.consulmedics.patientdata.components.StepperCallback
import com.consulmedics.patientdata.components.models.StepItem
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.databinding.ActivityAddEditPatientBinding
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import kotlinx.coroutines.launch


class AddEditPatientActivity : BaseActivity() , StepperCallback{
    private var patient: Patient? = null
    private lateinit var binding: ActivityAddEditPatientBinding
    private lateinit var pageStepper: MainStepper
    private lateinit var navController: NavController
    private var pageTitleList: List <StepItem> = listOf ()
    private var isLeftStepperInitialized = false
    var tabIndex: Int = 0
    private val sharedViewModel: AddEditPatientViewModel by viewModels<AddEditPatientViewModel>(){
        AddEditPatientViewModelFactory(MyApplication.patientRepository!!, MyApplication.hotelRepository!!, MyApplication.addressRepository!!)
    }
    private val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->

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
        reloadPatientData()
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
            StepItem(getString(R.string.insurrance_details), getString(R.string.print_insurance)),
            StepItem(getString(R.string.patient_sign)),
            StepItem(getString(R.string.logistic_data)),
            StepItem(getString(R.string.doctor_document)),
            StepItem(getString(R.string.additional_details)),
            StepItem(getString(R.string.receipts), getString(R.string.print_receipt)),
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
            btnBack.setOnClickListener {
                if(tabIndex > 0)
                    onStepItemClicked(tabIndex - 1)
            }
            btnCancel.setOnClickListener {

                val confirmationDialog = ConfirmationDialog("Are you sure?", "You will lose all data what you did if you confirm yes.")
                confirmationDialog.setNegativeClickListener {
                    confirmationDialog.dismiss()
                }
                confirmationDialog.setPostiveClickListener {
                    confirmationDialog.dismiss()
                    finish()
                }
                confirmationDialog.show(supportFragmentManager, "ConfirmationDialog")
            }
            btnNext.setOnClickListener {
                if(tabIndex < 7)
                    onStepItemClicked(tabIndex + 1)
            }
            btnFinish.setOnClickListener {
                val confirmationDialog = ConfirmationDialog("Confirm go to home?", "You will move to home screen after save data.")
                confirmationDialog.setNegativeClickListener {
                    confirmationDialog.dismiss()
                }
                confirmationDialog.setPostiveClickListener {
                    sharedViewModel.patientData.value?.let { it1 ->
                        sharedViewModel.viewModelScope.launch {
                            sharedViewModel.savePatient(it1)
                        }
                        confirmationDialog.dismiss()
                        finish()
                    }


                }
                confirmationDialog.show(supportFragmentManager, "ConfirmationDialog")
            }

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
        index.also {
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

        reloadPatientData()
    }

    fun reloadPatientData(){

        sharedViewModel.patientData.observe(this) {
            Log.e(TAG_NAME, "UPDATED PATIENT DETAILS")
            pageStepper.setPatientData(it)
        }
    }
    override fun stepActionButtonClicked(buttonString: String) {
        Log.e(TAG_NAME, "STEP ACTION BUTTON HAS BEEN CLICKED")
        Log.e(TAG_NAME, "STEP ACTION BUTTON HAS BEEN CLICKED : TRACK FROM ACTIVITY SIDE")
        val navHostGragment: Fragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_patient_flow)
        if(buttonString == getString(R.string.read_card)){
            sharedViewModel.loadPatientFromCard(applicationContext)
        }
        else if (buttonString == getString(R.string.print_insurance)){
            sharedViewModel.printInsurance()
        }
        else if (buttonString == getString(R.string.print_receipt)){
            sharedViewModel.printReceipt()
        }
    }

}