package com.consulmedics.patientdata.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View.GONE
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
import com.consulmedics.patientdata.utils.AppConstants.PATIENT_DATA
import com.consulmedics.patientdata.utils.AppConstants.PATIENT_MODE
import com.consulmedics.patientdata.utils.AppConstants.PHONE_CALL_MODE
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModel
import com.consulmedics.patientdata.viewmodels.AddEditPatientViewModelFactory
import kotlinx.coroutines.launch


class AddEditPatientActivity : BaseActivity() , StepperCallback{
    private var patient: Patient? = null
    private var patientMode: String? = null
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

        if(patientMode == PHONE_CALL_MODE){

        }
        else{
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
            else if (destination.id == R.id.printFragment) {
                tabIndex = 8
            }
            pageStepper.go(tabIndex)
            if(isLeftStepperInitialized)
                binding.leftStepper.setCurrentIndex(tabIndex)
//            reloadPatientData()
        }



    }
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_edit_patient)
        Log.e("OnCreated", "OnCreated")
        binding = ActivityAddEditPatientBinding.inflate(layoutInflater)
        patientMode = intent.getStringExtra(PATIENT_MODE)
        pageStepper = binding.MainStepper
        var goTabIndex: Int = intent.getIntExtra("tab_index", -1)

        setContentView(binding.root)

        if(patientMode == PHONE_CALL_MODE){
            pageTitleList = listOf<StepItem>(
                StepItem(getString(R.string.patient_data_from_phone)))
            pageStepper.setPageList(pageTitleList)
        }
        else{
            pageTitleList = listOf<StepItem>(
                StepItem(getString(R.string.patient_data), getString(R.string.read_card)),
                StepItem(getString(R.string.insurrance_details), getString(R.string.print_insurance)),
                StepItem(getString(R.string.patient_sign)),
                StepItem(getString(R.string.logistic_data)),
                StepItem(getString(R.string.doctor_document)),
                StepItem(getString(R.string.additional_details)),
                StepItem(getString(R.string.receipts), getString(R.string.print_receipt)),
                StepItem(getString(R.string.sign_doctor)),
                StepItem(getString(R.string.print))
            )
            pageStepper.setCallback(this)
            pageStepper.setPageList(pageTitleList)
        }
//
        if(goTabIndex == -1){
            goTabIndex = 0
        }
//
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_patient_flow) as NavHostFragment
        navController = navHostFragment.navController
        patient = intent.getSerializableExtra(PATIENT_DATA) as Patient
        if(patient != null) {
            sharedViewModel.setPatientData(patient!!)
            pageStepper.setPatientData(patient!!)
        }
        if(patientMode == PHONE_CALL_MODE){
            navController.setGraph(R.navigation.add_edit_navigation_phone_call, intent.extras)
        }
        else{
            navController.setGraph(R.navigation.add_edit_navigation, intent.extras)
//            reloadPatientData()
        }
//
        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener (listener)

        val leftStepperAdapter: LeftStepperAdapter = LeftStepperAdapter(applicationContext, this)
        binding.apply {
            if(patientMode == PHONE_CALL_MODE){
                btnBack.visibility = GONE
                btnNext.visibility = GONE
            }
            else{
                toggle = ActionBarDrawerToggle(this@AddEditPatientActivity, drawerLayout, R.string.patient_data, R.string.patient_data)
                drawerLayout.addDrawerListener(toggle)
                toggle.syncState()

                binding.leftStepper.initStepper(leftStepperAdapter)
                leftStepperAdapter.updateList(pageTitleList)
                if(goTabIndex == -1){
                    binding.leftStepper.setCurrentIndex(0)
                } else{
                    binding.leftStepper.setCurrentIndex(goTabIndex)
                }
                isLeftStepperInitialized = true
                btnBack.setOnClickListener {
                    if(tabIndex > 0)
                        onStepItemClicked(tabIndex - 1)
                }
                btnNext.setOnClickListener {
                    if(tabIndex < 8)
                        onStepItemClicked(tabIndex + 1)
                }
                onStepItemClicked(goTabIndex)
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

//        onStepItemClicked(goTabIndex)
        supportActionBar?.hide()
//        reloadPatientData()
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
        Log.e("OnStepItemClicked", "OnStepItemClicked")
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
            else if (it == 8){
                navController.navigate(R.id.printFragment)
            }
        }

//        reloadPatientData()
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
            Log.e("PRINT_LABEL111", "PRINT_LABEL222")

            sharedViewModel.printInsurance()
        }
        else if (buttonString == getString(R.string.print_receipt)){
            Log.e("23232", "PRINT_LABEL222")
            sharedViewModel.printReceipt()
        }
    }

}