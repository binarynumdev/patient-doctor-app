package com.consulmedics.patientdata.dao

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.consulmedics.patientdata.data.model.PatientShift
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

@Dao
interface PatientShiftDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg patientShift: PatientShift)
    @Delete
    fun delete(patientShift: PatientShift)
    @Query("select * from patient_shift order by uid desc")
    fun getAll(): LiveData<List<PatientShift>>
    @Update
    fun updatePatientShift(vararg  patientShift: PatientShift)
    @Query("select * from patient_shift")
    fun getList(): List<PatientShift>

    @Query("SELECT * from patient_shift WHERE uid= :id")
    fun getItemById(id: Int): List<PatientShift?>?

    fun insertOrUpdate(patientShift: PatientShift){
        var itemsFromDB  = patientShift.uid?.let { getItemById(it) }
        if (itemsFromDB != null) {
            if(itemsFromDB.isEmpty()){
                Log.e(TAG_NAME, "Insert New Patient Shift")
                insertAll(patientShift)
            }
            else{
                Log.e(TAG_NAME, "Update Patient Shift")
                updatePatientShift(patientShift)
            }
        }

    }
    @Query("select * from patient_shift where endDate <= :fDate ")
    fun getShiftBeforeDate(fDate: String): LiveData<List<PatientShift>>

    @Query("select * from patient_shift where startDate >= :sDate ")
    fun getShiftAfterDate(sDate: String): LiveData<List<PatientShift>>

    fun getUpcoming(): LiveData<List<PatientShift>>{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        return getShiftBeforeDate(formatter.format(current))
    }
    fun getPastShifts(): LiveData<List<PatientShift>>{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        return getShiftAfterDate(formatter.format(current))
    }
}