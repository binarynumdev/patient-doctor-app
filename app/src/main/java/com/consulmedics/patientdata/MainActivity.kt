package com.consulmedics.patientdata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.consulmedics.patientdata.databinding.ActivityMainBinding
import com.consulmedics.patientdata.models.Patient
import com.google.android.material.navigation.NavigationView
import com.identive.libs.SCard
import com.identive.libs.WinDefs
import java.io.IOException


class MainActivity : AppCompatActivity() {



    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    val scardLib = SCardExt()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            run {
                var patient: Patient = Patient()
                patient.loadExample()
                startActivity(Intent(this, NewPatientActivity::class.java).apply {
                    // you can add values(if any) to pass to the next class or avoid using `.apply`
                    putExtra("patient_data", patient)
                })
                /*
                var status: Long = 0
                if(!scardLib.initialized){
                    status = scardLib.SCardEstablishContext(baseContext)
                    if(0L != status){
                        Log.e("ERROR", "Establish context error")
                    }
                    else{
                        status = scardLib.SCardListReaders(baseContext)
                    }
                }
                if(scardLib.initialized){
                    status = scardLib.SCardConnect()
                    if(0L == status){
                        var patientData = scardLib.getPatientData()
                    }
                }

                 */

            }
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        //val status = scard.USBRequestPermission(applicationContext)
        scardLib.USBRequestPermission(applicationContext)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}