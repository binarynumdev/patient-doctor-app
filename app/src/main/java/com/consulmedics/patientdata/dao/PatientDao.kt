package com.consulmedics.patientdata.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.consulmedics.patientdata.data.model.Patient
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param


@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg patient: Patient)
    @Delete
    fun delete(patient: Patient)
//    @Query("select * from patient order by uid desc")
    @Query("SELECT * FROM patient WHERE isUpdated = 0 ORDER BY uid DESC")
    fun getAll(): LiveData<List<Patient>>
    @Update
    fun updatePatient(vararg  patient: Patient)
    @Query("select * from patient")
    fun getList(): List<Patient>
    @Query("select * from patient where uid <:parentId order by uid desc")
    fun getPreviousPatients(parentId: Int?): LiveData<List<Patient>>
    @Query("SELECT *\n" +
            "FROM patient\n" +
            "WHERE  strftime('%Y-%m-%d',\n" +
            "        SUBSTR(startVisitDate, 1, 4) || '-' || -- Year\n" +
            "        SUBSTR(startVisitDate, 5, 2) || '-' || -- Month\n" +
            "        SUBSTR(startVisitDate, 7, 2)           -- Day\n" +
            "    ) || ' ' || startVisitTime || ':00' \n" +
            "      BETWEEN :startDate AND :endDate " + "AND isUpdated = 0")
    fun getPatientsByShift(startDate: String, endDate: String): List<Patient>

    @Query(
        "UPDATE patient\n" +
                "SET isUpdated = 1\n" +
                "WHERE strftime('%Y-%m-%d',\n" +
                "              SUBSTR(startVisitDate, 1, 4) || '-' || -- Year\n" +
                "              SUBSTR(startVisitDate, 5, 2) || '-' || -- Month\n" +
                "              SUBSTR(startVisitDate, 7, 2)           -- Day\n" +
                "        ) || ' ' || startVisitTime || ':00' \n" +
                "      BETWEEN :startDate AND :endDate "
    )
    fun updateSelectedData(
        startDate: String?,
        endDate: String?
    )
}