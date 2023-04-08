package com.consulmedics.patientdata.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.consulmedics.patientdata.data.model.Address
import com.consulmedics.patientdata.data.model.Hotel

@Dao
interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg address: Address)
    @Delete
    fun delete(address: Address)
    @Query("select * from address")
    fun getAll(): LiveData<List<Address>>
    @Update
    fun updateAddress(vararg  address: Address)
    @Query("select * from address")
    fun getList(): List<Address>
}