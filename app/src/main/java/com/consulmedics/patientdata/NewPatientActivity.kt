package com.consulmedics.patientdata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.consulmedics.patientdata.databinding.ActivityMainBinding
import com.consulmedics.patientdata.databinding.ActivityNewPatientBinding
import com.consulmedics.patientdata.models.Patient
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class NewPatientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewPatientBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val patient:Patient = intent.getSerializableExtra("patient_data") as Patient
        binding = ActivityNewPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}