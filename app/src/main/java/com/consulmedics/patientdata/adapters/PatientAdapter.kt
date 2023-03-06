package com.consulmedics.patientdata.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.consulmedics.patientdata.MyApplication.Companion.patientDetailsActivityRequestCode
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.ItemPatientBinding
import com.consulmedics.patientdata.databinding.RowPatientItemBinding
import com.consulmedics.patientdata.models.Patient
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import java.text.SimpleDateFormat

class PatientAdapter(
    private val mContext: Context,
    val patientItemOnClickInterface: PatientItemClickInterface
) :
    RecyclerView.Adapter<PatientAdapter.ViewHolder>(){
    private val allPatients = ArrayList<Patient>()
    inner class ViewHolder(val itemBinding: ItemPatientBinding) : RecyclerView.ViewHolder(itemBinding.root) {
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflating our layout file for each item of recycler view.
        Log.e(TAG_NAME, "ONCREATEVIEWHOLDER")
        val binding  = ItemPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // on below line we are setting data to item of recycler view.
        var currentPatient = allPatients.get(position)
        currentPatient.decryptFields()
        holder.itemBinding.apply {
            if("${currentPatient.firstName} ${currentPatient.lastName}" != " "){
                textFullName.setText("${currentPatient.firstName} ${currentPatient.lastName}")
            }
            else{
                textFullName.setText(mContext.getString(R.string.no_full_name))
            }

            if("${currentPatient.street} ${currentPatient.houseNumber} ${currentPatient.city} ${currentPatient.postCode}" != "   "){
                textFullAddress.setText("${currentPatient.street} ${currentPatient.houseNumber} ${currentPatient.city} ${currentPatient.postCode}")
            }
            else{
                textFullAddress.setText(mContext.getString(R.string.no_address))
            }

            if(currentPatient.patientID?.isNotEmpty() == true){
                textPatientID.setText(currentPatient.patientID)
            }
            else{
                textPatientID.setText(mContext.getString(R.string.no_patient_id))
            }


            if(currentPatient.birthDate != null){
                val birthDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT)
                textBirthDate.setText(birthDateFormat.format(currentPatient.birthDate))
            }
            else{
                textBirthDate.setText(mContext.getString(R.string.no_birthdate))
            }
            if(!currentPatient.gender.isNullOrEmpty()){
                if(currentPatient.gender == "W"){
                    textGender.setText( mContext.getString(R.string.female) )
                }
                else if (currentPatient.gender == "M"){
                    textGender.setText( mContext.getString(R.string.male)  )
                }


            }
            else{
                textGender.setText( mContext.getString(R.string.no_gender) )
            }

            if(currentPatient.isFullyValidated()){
                textIsFinished.setText(R.string.completed)
                textIsFinished.setTextColor(Color.GREEN)
                statusColoredBorder.setBackgroundColor(Color.GREEN)
            }
            else{
                textIsFinished.setTextColor(Color.RED)
                textIsFinished.setText(R.string.incompleted)
                statusColoredBorder.setBackgroundColor(Color.RED)
            }
            btnEditPatient.setOnClickListener {
                patientItemOnClickInterface.onPatientEditClick(currentPatient)
            }

            btnRemovePatient.setOnClickListener {
                patientItemOnClickInterface.onPatientRemoveClick(currentPatient)
            }

            patientItem.setOnClickListener {
                patientItemOnClickInterface.onPatientItemClick(currentPatient)
            }
        }



    }

    override fun getItemCount(): Int {
        // on below line we are
        // returning our list size.
        return allPatients.size
    }

    // below method is use to update our list of notes.
    fun updateList(newList: List<Patient>) {
        // on below line we are clearing
        // our notes array list
        allPatients.clear()
        // on below line we are adding a
        // new list to our all notes list.
        Log.e(TAG_NAME, "Update List: ${newList.count()}")
        allPatients.addAll(newList)
        // on below line we are calling notify data
        // change method to notify our adapter.
        notifyDataSetChanged()
    }
}

interface PatientItemClickInterface{
    fun onPatientRemoveClick(patient: Patient)
    fun onPatientEditClick(patient: Patient)
    fun onPatientItemClick(patient: Patient)
}