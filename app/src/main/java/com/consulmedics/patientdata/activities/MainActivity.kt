package com.consulmedics.patientdata.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.se.omapi.Session
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.consulmedics.patientdata.*
import com.consulmedics.patientdata.databinding.ActivityMainBinding
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.utils.AppConstants.PATIENT_DATA
import com.consulmedics.patientdata.utils.AppConstants.PATIENT_MODE
import com.consulmedics.patientdata.utils.AppConstants.PHONE_CALL_MODE
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.utils.SessionManager
import com.google.android.material.navigation.NavigationView
import com.consulmedics.patientdata.data.api.ApiClient
import com.consulmedics.patientdata.fragments.ShiftListFragment

class MainActivity : BaseActivity() , NavigationView.OnNavigationItemSelectedListener{

    private lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    val scardLib = SCardExt()
    private var isDay: Boolean = true
    private var themePosition : Int? = null
    private var arrayTheme : Array<String> ?= null

    private var userPrefs : UserPreferenceRepository = MyApplication.instance.userPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(this, "MainActivity", Toast.LENGTH_SHORT).show()

        var api_token : String? = SessionManager.getString(this, "api_token")

        ApiClient.setBearerToken(api_token!!)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initTheme()

        if(themePosition == 0) {
            binding.appBarMain.btnSyncPatients.setImageResource(R.drawable.ic_day)
        } else {
            binding.appBarMain.btnSyncPatients.setImageResource(R.drawable.ic_night)
        }

        binding.appBarMain.btnNewPatient.setOnClickListener{
            showDropDownMenu()
        }
        binding.appBarMain.btnSyncPatients.setOnClickListener {

            themePosition = when(themePosition) {
                0 -> 1
                else -> 0
            }
            if(themePosition == 0) {
                Toast.makeText(this, "Day", Toast.LENGTH_SHORT).show()
                binding.appBarMain.btnSyncPatients.setImageResource(R.drawable.ic_day)
            } else {
                Toast.makeText(this, "Night", Toast.LENGTH_SHORT).show()
                binding.appBarMain.btnSyncPatients.setImageResource(R.drawable.ic_night)
            }
            setTheme()
        }
        var userFirstName = SessionManager.getFirstName(this)
        var userLastName = SessionManager.getLastName(this)
        binding.appBarMain.textUserName.text = "$userFirstName $userLastName"
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

    private fun initTheme() {
        arrayTheme = resources.getStringArray(R.array.themes)

        themePosition = when(userPrefs.appTheme) {
            Theme.LIGHT_MODE -> 0
            Theme.DARK_MODE -> 1
        }
    }

    private fun setTheme() {
//        Toast.makeText(this, themePosition.toString(), Toast.LENGTH_SHORT).show()
        userPrefs.updateTheme(
            when(themePosition) {
                0 -> Theme.LIGHT_MODE
                else -> Theme.DARK_MODE
            }
        )
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
                SessionManager.saveSelectedMenuItemLabel(this, "shift_list")
                Log.e("DDD", "EEE")
                navController.navigate(R.id.shiftListFragment)
            }
            R.id.nav_patients ->{
                SessionManager.saveSelectedMenuItemLabel(this, "patient_list")
                navController.navigate(R.id.patientListFragment)
            }
        }
        return true
    }
}