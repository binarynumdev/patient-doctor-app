package com.consulmedics.patientdata

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.consulmedics.patientdata.databinding.ActivityNoUserIdBinding
import com.consulmedics.patientdata.activities.LoginActivity
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME

class NoUserIdActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoUserIdBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoUserIdBinding.inflate(layoutInflater)
        binding.apply {
            btnAdminLogin.setOnClickListener {
                Log.e(TAG_NAME, "GO TO ADMIN");
                val i = Intent(applicationContext, LoginActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
            }
        }
        setContentView(binding.root)
    }
}