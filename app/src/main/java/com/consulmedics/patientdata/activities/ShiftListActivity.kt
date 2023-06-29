package com.consulmedics.patientdata.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.ActivityShiftListBinding

class ShiftListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShiftListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShiftListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}