package com.consulmedics.patientdata.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.consulmedics.patientdata.models.PatientRepository

class AddEditPatientViewModelFactory (private val repository: PatientRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditPatientViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEditPatientViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}