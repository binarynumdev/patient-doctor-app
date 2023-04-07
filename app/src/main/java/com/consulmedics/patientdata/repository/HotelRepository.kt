package com.consulmedics.patientdata.repository

import androidx.lifecycle.LiveData
import com.consulmedics.patientdata.dao.HotelDao
import com.consulmedics.patientdata.data.model.Hotel

class HotelRepository (private val hotelDao: HotelDao){
    val hotelList: LiveData<List<Hotel>> = hotelDao.getAll()

    // on below line we are creating an insert method
    // for adding the note to our database.
    suspend fun insert(hotel: Hotel) {
        hotelDao.insertAll(hotel)
    }

    // on below line we are creating a delete method
    // for deleting our note from database.
    suspend fun delete(hotel: Hotel){
        hotelDao.delete(hotel)
    }

    // on below line we are creating a update method for
    // updating our note from database.
    suspend fun update(hotel: Hotel){
        hotelDao.updateHotel(hotel)
    }

}