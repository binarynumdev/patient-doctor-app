package com.consulmedics.patientdata.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.consulmedics.patientdata.activities.AdminActivity
import com.consulmedics.patientdata.databinding.ActivityLoginBinding

import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.ui.register.RegisterActivity
import com.consulmedics.patientdata.utils.AESEncyption
import com.consulmedics.patientdata.utils.AppConstants.ADMIN_PASSWORD
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.utils.AppUtils

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnNext = binding.btnNext
        val editPassword = binding.editAdminPassword
        btnNext.setOnClickListener{
            Log.e(TAG_NAME, "BTN CLICKED")
            var encrypted = AESEncyption.encrypt(editPassword.text.toString())
            Log.e(TAG_NAME, encrypted!!)
            if(encrypted.trim() == ADMIN_PASSWORD){
                var userIDExist = AppUtils.checkUserID()
                if(userIDExist){
                    val i = Intent(this, AdminActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                }
                else{
                    val i = Intent(this, RegisterActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                }
            }
            else{
                Toast.makeText(applicationContext, R.string.password_no_match, Toast.LENGTH_LONG).show()
            }
        }
    }

}
