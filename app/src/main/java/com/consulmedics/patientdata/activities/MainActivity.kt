package com.consulmedics.patientdata.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.SCardExt
import com.consulmedics.patientdata.databinding.ActivityMainBinding
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.utils.AppConstants.PATIENT_DATA
import com.consulmedics.patientdata.utils.AppConstants.PATIENT_MODE
import com.consulmedics.patientdata.utils.AppConstants.PHONE_CALL_MODE

class MainActivity : BaseActivity() {


    private lateinit var binding: ActivityMainBinding

    val scardLib = SCardExt()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.appBarMain.btnNewPatient.setOnClickListener{
            showDropDownMenu()
        }
        binding.appBarMain.btnSyncPatients.setOnClickListener {

        }
        val sttus = scardLib.USBRequestPermission(applicationContext)
        Log.e("USB_CONNECTION", sttus.toString())
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

                    putExtra(PATIENT_DATA, Patient())
                })
                return true
            }
        }
        return false
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }
}