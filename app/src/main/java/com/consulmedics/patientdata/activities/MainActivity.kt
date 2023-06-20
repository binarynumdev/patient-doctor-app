package com.consulmedics.patientdata.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.consulmedics.patientdata.SCardExt
import com.consulmedics.patientdata.databinding.ActivityMainBinding
import com.consulmedics.patientdata.data.model.Patient

class MainActivity : BaseActivity() {


    private lateinit var binding: ActivityMainBinding

    val scardLib = SCardExt()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.appBarMain.btnNewPatient.setOnClickListener{
            startActivity(Intent(this, AddEditPatientActivity::class.java).apply {
                // you can add values(if any) to pass to the next class or avoid using `.apply`
                putExtra("patient_data", Patient())
            })
        }
        binding.appBarMain.btnSyncPatients.setOnClickListener {

        }
        val sttus = scardLib.USBRequestPermission(applicationContext)
        Log.e("USB_CONNECTION", sttus.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }
}