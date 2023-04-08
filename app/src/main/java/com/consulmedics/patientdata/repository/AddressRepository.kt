package com.consulmedics.patientdata.repository

import androidx.lifecycle.LiveData
import com.consulmedics.patientdata.dao.AddressDao
import com.consulmedics.patientdata.dao.HotelDao
import com.consulmedics.patientdata.data.model.Address
import com.consulmedics.patientdata.data.model.Hotel

class AddressRepository (private val addreessDao: AddressDao){
    val addressList: LiveData<List<Address>> = addreessDao.getAll()

    // on below line we are creating an insert method
    // for adding the note to our database.
    suspend fun insert(address: Address) {
        addreessDao.insertAll(address)
    }

    // on below line we are creating a delete method
    // for deleting our note from database.
    suspend fun delete(address: Address){
        addreessDao.delete(address)
    }

    // on below line we are creating a update method for
    // updating our note from database.
    suspend fun update(address: Address){
        addreessDao.updateAddress(address)
    }

}