package com.consulmedics.patientdata.activities

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

class AddEditPatientActivity : AppCompatActivity() {
    private var patient: Patient? = null
    private lateinit var binding: ActivityAddEditPatientBinding

    private lateinit var navController: NavController
    private val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
        var tabIndex: Int = 0
        if(destination.id == R.id.patientPersonalDetailsFragment){
            tabIndex = 0
        }
        else if (destination.id == R.id.patientInsurranceDetailsFragment){
            tabIndex = 1
        }
        binding.patientStepIndicator.go(tabIndex, true)
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
//        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener (listener)

        var pageTitleList: List <String> = listOf<String>(
            getString(R.string.patient_data),
            getString(R.string.insurrance_details),
            getString(R.string.logistic_data),
            getString(R.string.doctor_document),
            getString(R.string.additional_details),
            getString(R.string.receipts),
            getString(R.string.sign_doctor))
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