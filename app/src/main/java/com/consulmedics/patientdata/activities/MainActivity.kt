package com.consulmedics.patientdata.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.SCardExt
import com.consulmedics.patientdata.databinding.ActivityMainBinding
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.utils.AppConstants.PATIENT_DATA
import com.consulmedics.patientdata.utils.AppConstants.PATIENT_MODE
import com.consulmedics.patientdata.utils.AppConstants.PHONE_CALL_MODE
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity() , NavigationView.OnNavigationItemSelectedListener{


    private lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    val scardLib = SCardExt()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.appBarMain.btnNewPatient.setOnClickListener{
            showDropDownMenu()
        }
        binding.appBarMain.btnSyncPatients.setOnClickListener {
            startActivity(Intent(this, UploadPatientActivity::class.java).apply {
            })
        }
        toggle = ActionBarDrawerToggle(this@MainActivity, binding.appBarMain.drawerLayout, R.string.patient_data, R.string.patient_data)
        binding.appBarMain.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.appBarMain.drawerButton.setOnClickListener {
            binding.appBarMain.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.appBarMain.navView.setNavigationItemSelectedListener(this)
        val sttus = scardLib.USBRequestPermission(applicationContext)
        Log.e("USB_CONNECTION", sttus.toString())

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_patient_flow) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_main_graph, intent.extras)
    }

    fun showDropDownMenu(){
        val popupMenu = PopupMenu(this, binding.appBarMain.btnNewPatient)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.main, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            handleMenuItemClick(it)
        }
        popupMenu.show()

    }
    private fun handleMenuItemClick(item: MenuItem): Boolean{
        when(item.itemId){
            R.id.action_new_patient ->{
                startActivity(Intent(this, AddEditPatientActivity::class.java).apply {
                    // you can add values(if any) to pass to the next class or avoid using `.apply`
                    putExtra(PATIENT_DATA, Patient())
                })
                return true
            }
            R.id.action_new_patient_called ->{
                startActivity(Intent(this, AddEditPatientActivity::class.java).apply {
                    // you can add values(if any) to pass to the next class or avoid using `.apply`
                    putExtra(PATIENT_MODE, PHONE_CALL_MODE)

                    putExtra(PATIENT_DATA, Patient(target = "call"))
                })
                return true
            }
        }
        return false
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.e("DDD","DDD")
        binding.appBarMain.drawerLayout.closeDrawer(GravityCompat.START)
        when(item.itemId){
            R.id.nav_shift ->{
                Log.e("DDD", "EEE")
                navController.navigate(R.id.shiftListFragment)
            }
            R.id.nav_patients ->{
                navController.navigate(R.id.patientListFragment)
            }
        }
        return true
    }
}