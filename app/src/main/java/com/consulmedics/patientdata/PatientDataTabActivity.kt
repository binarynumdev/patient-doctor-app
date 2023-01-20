package com.consulmedics.patientdata

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.consulmedics.patientdata.ui.main.SectionsPagerAdapter
import com.consulmedics.patientdata.databinding.ActivityPatientDataTabBinding
import com.consulmedics.patientdata.models.Patient

class PatientDataTabActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientDataTabBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPatientDataTabBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val patient: Patient = intent.getSerializableExtra("patient_data") as Patient
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, patient)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}