package com.consulmedics.patientdata.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.consulmedics.patientdata.Converters
import com.consulmedics.patientdata.databinding.ActivityPatientDetailsBinding
import com.consulmedics.patientdata.data.model.Patient

class PatientDetailsActivity : AppCompatActivity() {
    private var patient: Patient? = null
    private lateinit var binding: ActivityPatientDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        patient = intent.getSerializableExtra("patient_data") as Patient

        binding.apply {
            textPatientFullAddress.setText("${patient?.street} ${patient?.houseNumber}, ${patient?.city} ${patient?.postCode}")
            textPatientFullName.setText("${patient?.lastName} ${patient?.firstName}")
            textInsuranceName.setText(patient?.insuranceName)
            textInsuranceStatus.setText(patient?.insuranceStatus)
            textInsurnaceNumber.setText(patient?.insuranceNumber)
            val converters = Converters()
            textDateOfVisitStart.setText(converters.dateToFormatedString(converters.stringToDate(patient?.startVisitDate)))
            textTimeOfVisitStart.setText(patient?.startVisitTime)
            textDiagnosis.setText(patient?.diagnosis)
            textHealthStatus.setText(patient?.healthStatus)

        }
    }
}