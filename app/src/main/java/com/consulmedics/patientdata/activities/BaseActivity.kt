package com.consulmedics.patientdata.activities

import android.app.ProgressDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity


abstract  class BaseActivity: AppCompatActivity() {
    lateinit var progressDialog: ProgressDialog
    fun showLoadingSpinner(title: String, message: String){
        progressDialog = ProgressDialog(this@BaseActivity)
        progressDialog.setTitle(title)
        progressDialog.setMessage(message)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }
    fun hideLoadingSpinner(){
        progressDialog.dismiss()
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}