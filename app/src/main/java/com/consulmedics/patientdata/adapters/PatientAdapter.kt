package com.consulmedics.patientdata.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.consulmedics.patientdata.PatientDataTabActivity
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.ItemPatientBinding
import com.consulmedics.patientdata.databinding.RowPatientItemBinding
import com.consulmedics.patientdata.models.Patient
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME

class PatientAdapter(
    private val mContext: Context,
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
        holder.itemBinding.textFullName.setText("${currentPatient.firstName} ${currentPatient.lastName}")
        holder.itemBinding.textFullAddress.setText("${currentPatient.street} ${currentPatient.houseNumber} ${currentPatient.city} ${currentPatient.postCode}")

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