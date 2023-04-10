package com.consulmedics.patientdata.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.consulmedics.patientdata.repository.AddressRepository
import com.consulmedics.patientdata.repository.HotelRepository
import com.consulmedics.patientdata.repository.PatientRepository

class AddEditPatientViewModelFactory (private val repository: PatientRepository, private val hotelRepository: HotelRepository, private val addressRepository: AddressRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditPatientViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEditPatientViewModel(repository, hotelRepository, addressRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}