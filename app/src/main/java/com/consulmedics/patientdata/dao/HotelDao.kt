package com.consulmedics.patientdata.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.consulmedics.patientdata.data.model.Hotel

@Dao
interface HotelDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg hotel: Hotel)
    @Delete
    fun delete(hotel: Hotel)
    @Query("select * from hotel")
    fun getAll(): LiveData<List<Hotel>>
    @Update
    fun updateHotel(vararg  hotel: Hotel)
    @Query("select * from hotel")
    fun getList(): List<Hotel>
}