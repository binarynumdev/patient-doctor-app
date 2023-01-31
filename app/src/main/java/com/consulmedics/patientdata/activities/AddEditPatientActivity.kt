package com.consulmedics.patientdata.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.models.Patient

class AddEditPatientActivity : AppCompatActivity() {
    private var patient: Patient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_patient)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_patient_flow) as NavHostFragment
        val navController = navHostFragment.navController
        patient = intent.getSerializableExtra("patient_data") as Patient
        navController.setGraph(R.navigation.add_edit_navigation, intent.extras)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}