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
        binding.editPatientID.setText(patient?.patientID)
        binding.editFirstName.setText(patient?.firstName)
        binding.editLastName.setText(patient?.lastName)
        binding.editGender.setText( when(patient?.gender == "W") { true -> "Femaile" false -> "Male"}  )
        val birthDateFormat = SimpleDateFormat("yyyy-MM-dd")


        binding.editDateOfBirth.setText(birthDateFormat.format(patient?.birthDate))
        binding.editStreet.setText(patient?.street)
        binding.editCity.setText(patient?.city)
        binding.editPostalCode.setText(patient?.postCode)
        binding.editHouseNumber.setText(patient?.houseNumber)


    }
}