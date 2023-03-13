package com.consulmedics.patientdata.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.data.RegisterRepository
import com.consulmedics.patientdata.data.Result

class RegisterViewModel(private val registerRepository: RegisterRepository) : ViewModel() {

    private val _registerForm = MutableLiveData<RegisterFormState>()

}