package com.consulmedics.patientdata.dao

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.data.model.PatientShift
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Dao
interface PatientShiftDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg patientShift: PatientShift)
    @Delete
    fun delete(patientShift: PatientShift)



    @Query("select * from patient_shift order by uid desc")
    fun getAll(): LiveData<List<PatientShift>>

    @Query("SELECT * FROM patient_shift WHERE isUploaded = 1 ORDER BY uid DESC")
    fun getUploadedAll(): LiveData<List<PatientShift>>

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
                Log.e("This is the patientShift", "${patientShift}")

                insertAll(patientShift)
            }
            else{
                Log.e(TAG_NAME, "Update Patient Shift")
                val willUpdatePatientShift = itemsFromDB.first()
                if(patientShift.doctorNote.isEmpty() && willUpdatePatientShift?.doctorNote?.isNotEmpty() == true){
                    patientShift.doctorNote = willUpdatePatientShift?.doctorNote.toString()
                }
                if(patientShift.isUploaded == false && willUpdatePatientShift?.isUploaded == true){
                    patientShift.isUploaded = true
                }
                updatePatientShift(patientShift)
            }
        }

    }
    @Query("select * from patient_shift where startDate >= :fDate and doctorID = :doctorID")
    fun getShiftBeforeDate(fDate: String, doctorID: String?): LiveData<List<PatientShift>>

    @Query("SELECT * FROM patient_shift WHERE startDate <= :sDate AND doctorID = :doctorID AND isUploaded = 0")
    fun getShiftAfterDate(sDate: String, doctorID: String?): LiveData<List<PatientShift>>
    fun getUpcoming(doctorID: String?): LiveData<List<PatientShift>>{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

//        return getShiftBeforeDate(formatter.format(current), doctorID)
        var result =  getShiftBeforeDate(formatter.format(current), doctorID)
        Log.e("Query Result", "${result.value?.count()}")
        return result
    }
    fun getPastShifts(doctorID: String?): LiveData<List<PatientShift>>{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        var result =  getShiftAfterDate(formatter.format(current), doctorID)
        Log.e("Query Result", "${result.value?.count()}")
        return result
    }
}