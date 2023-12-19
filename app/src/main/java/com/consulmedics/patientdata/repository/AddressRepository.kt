package com.consulmedics.patientdata.repository

import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.LiveData
import com.consulmedics.patientdata.dao.AddressDao
import com.consulmedics.patientdata.dao.HotelDao
import com.consulmedics.patientdata.data.api.methods.DirectionApi
import com.consulmedics.patientdata.data.api.methods.FetchLocationApi
import com.consulmedics.patientdata.data.model.Address
import com.consulmedics.patientdata.data.model.Hotel
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class AddressRepository (private val addreessDao: AddressDao){
    val hotelList:   LiveData<List<Address>> = addreessDao.getHotels()
    val addressList: LiveData<List<Address>> = addreessDao.getAll()

    // on below line we are creating an insert method
    // for adding the note to our database.
    fun insert(address: Address): Long {
        return addreessDao.insertAll(address)
    }

    // on below line we are creating a delete method
    // for deleting our note from database.
    suspend fun delete(address: Address){
        addreessDao.delete(address)
    }

    // on below line we are creating a update method for
    // updating our note from database.
    suspend fun update(address: Address): Long{
        addreessDao.updateAddress(address)
        return address.uid!!.toLong()
    }

    fun find(startAddress: Int?): Address {
        Log.e("TAG", "Address ID: ${startAddress}")
        return addreessDao.findAddress(startAddress!!)
    }
    suspend fun calculateDistance(startPoint: Address, targetPoint: Address, apiKey: String): Double= withContext(Dispatchers.IO)  {


        val call = DirectionApi.getApi()?.fetch(origin = "${startPoint.latitute},${startPoint.longitute}", destination = "${targetPoint.latitute},${targetPoint.longitute}", apiKey = apiKey)

        val response = call?.execute()
        var totalDistance = 0.00
        if (response != null) {
            if (response.isSuccessful) {
                val directions = response.body()
                if(directions?.routes?.isNotEmpty() == true){
                    Log.e("DISTANCE", "${directions?.routes?.get(0)?.legs?.get(0)?.distance?.text}")
                    totalDistance = directions?.routes?.get(0)?.legs?.get(0)?.distance?.value?.toDouble()!!
                }

            } else {
                Log.e("DISTANCE", "-----")
            }
        }
        totalDistance
    }
    fun calculateDistance(sPoint: Int, tPoint: Int): Double{
        val startPoint = addreessDao.findAddress(sPoint)
        val targetPoint = addreessDao.findAddress(tPoint)

        if(startPoint.longitute == 0.00 || startPoint.latitute == 0.00 || targetPoint.longitute == 0.00 || targetPoint.latitute == 0.00){
            return 0.00
        }
        else{
            var distanceInKm =  acos(sin(startPoint.latitute)*sin(targetPoint.latitute)+cos(startPoint.latitute)*cos(targetPoint.latitute)*cos(targetPoint.longitute-startPoint.longitute))*6371
            val roundoff = (distanceInKm * 100.0).roundToInt() / 100.0
            return roundoff
        }
        return 0.00
    }

}