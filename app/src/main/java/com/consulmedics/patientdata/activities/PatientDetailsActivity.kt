package com.consulmedics.patientdata.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.ActivityPatientDetailsBinding

class PatientDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPatientDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}