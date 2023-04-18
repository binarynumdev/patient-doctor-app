package com.consulmedics.patientdata.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.consulmedics.patientdata.data.model.Address
import com.consulmedics.patientdata.data.model.Hotel

@Dao
interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll( address: Address): Long
    @Delete
    fun delete(address: Address)
    @Query("select * from address")
    fun getAll(): LiveData<List<Address>>
    @Update
    fun updateAddress(vararg  address: Address)
    @Query("select * from address")
    fun getList(): List<Address>
    @Query("select * from address where isHotel = 1 and streetNumber != '' and streetName != '' and city != '' and postCode != ''")
    fun getHotels(): LiveData<List<Address>>

    @Query("SELECT * FROM address WHERE uid=:id ")
    fun findAddress(id: Int): Address
}