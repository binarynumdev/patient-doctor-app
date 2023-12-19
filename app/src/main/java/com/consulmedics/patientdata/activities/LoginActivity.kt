package com.consulmedics.patientdata.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.consulmedics.patientdata.data.api.ApiClient
import com.consulmedics.patientdata.utils.SessionManager
import com.consulmedics.patientdata.databinding.ActivityLoginBinding
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.data.api.response.LoginResponse
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.consulmedics.patientdata.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnNext = binding.btnNext
        btnNext.setOnClickListener{
            Log.e(TAG_NAME, "BTN CLICKED")
            val email = binding.editEmailAddress.text.toString()
            val pwd = binding.editPassword.text.toString()
            viewModel.loginUser(email = email, pwd = pwd)
            Log.e("EMAIL", email.toString())
            Log.e("PASSWORD", pwd.toString())
        }

        viewModel.loginResult.observe(this) {
            when (it) {
                is BaseResponse.Loading -> {
                    showLoading()
                }

                is BaseResponse.Success -> {
                    stopLoading()
                    processLogin(it.data)
                }

                is BaseResponse.Error -> {
                    stopLoading()
                    processError(it.msg)
                }
                else -> {
                    stopLoading()
                }
            }
        }
    }
    fun processLogin(data: LoginResponse?) {
        showToast("Success:")
        Log.e("Dataaaaaaaaa", data?.data?.api_token.toString())

        if(!data?.data?.first_name.isNullOrEmpty()){
            data?.data?.first_name?.let {
                SessionManager.saveFirstName(this, it)
            }
        }
        if(!data?.data?.last_name.isNullOrEmpty()){
            data?.data?.last_name?.let {
                SessionManager.saveLastName(this, it)
            }
        }
        if(!data?.data?.private_key.isNullOrEmpty()){
            data?.data?.private_key?.let {
                SessionManager.savePrivateKey(this, it)
            }
        }
        if(!data?.data?.userID.isNullOrEmpty()){
            data?.data?.userID?.let { SessionManager.saveUserID(this, it) }
        }
        if(!data?.data?.doctorID.isNullOrEmpty()){
            data?.data?.doctorID?.let {
                SessionManager.saveDoctorID(this, it)
                Log.e(TAG_NAME, "Doctor ID: ${it}")
            }
        } else return
        if (!data?.data?.api_token.isNullOrEmpty()) {
            data?.data?.api_token?.let {
                Log.e("api_token_here", it)
//                ApiClient.setBearerToken(it)
                SessionManager.saveAuthToken(this, it)
            }
            navigateToHome()
        }
    }
    fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }
    fun stopLoading(){
        binding.progressBar.visibility = View.GONE
    }
    fun processError(msg: String?) {
        showToast("Error:" + msg)
    }
    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
