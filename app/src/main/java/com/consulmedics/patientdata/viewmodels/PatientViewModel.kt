package com.consulmedics.patientdata.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.consulmedics.patientdata.PatientsDatabase
import com.consulmedics.patientdata.models.Patient
import com.consulmedics.patientdata.models.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PatientViewModel(application: Application) : AndroidViewModel(application)  {
    val allPatients : LiveData<List<Patient>>
    val repository : PatientRepository
    val currentPatient: LiveData<Patient> = MutableLiveData<Patient>()
    // on below line we are initializing
    // our dao, repository and all notes
    init {
        val dao = PatientsDatabase.getDatabase(application).patientDao()
        repository = PatientRepository(dao)
        allPatients = repository.allPatients
    }

    // on below line we are creating a new method for deleting a note. In this we are
    // calling a delete method from our repository to delete our note.
    fun deleteNote (patient: Patient) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(patient)
    }

    // on below line we are creating a new method for updating a note. In this we are
    // calling a update method from our repository to update our note.
    fun updateNote(patient: Patient) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(patient)
    }


    // on below line we are creating a new method for adding a new note to our database
    // we are calling a method from our repository to add a new note.
    fun addNote(patient: Patient) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(patient)
    }
}