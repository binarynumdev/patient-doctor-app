package com.consulmedics.patientdata.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract  class BaseActivity: AppCompatActivity() {
    lateinit var progressDialog: ProgressDialog
    fun showLoadingSpinner(title: String, message: String){
        progressDialog = ProgressDialog(this@BaseActivity)
        progressDialog.setTitle(title)
        progressDialog.setMessage(message)
        progressDialog.show()
    }
    fun hideLoadingSpinner(){
        progressDialog.dismiss()
    }
}