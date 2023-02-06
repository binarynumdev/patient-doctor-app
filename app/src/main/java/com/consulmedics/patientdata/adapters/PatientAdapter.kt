package com.consulmedics.patientdata.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.consulmedics.patientdata.databinding.RowPatientItemBinding
import com.consulmedics.patientdata.models.Patient
class PatientAdapter(
    private val mContext: Context,
) :
    RecyclerView.Adapter<PatientAdapter.ViewHolder>(){
    private val allPatients = ArrayList<Patient>()
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are creating an initializing all our
        // variables which we have added in layout file.
        val txtFulLName = itemView.findViewById<TextView>(R.id.textFullName)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflating our layout file for each item of recycler view.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_patient,
            parent, false
        )
        return ViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // on below line we are setting data to item of recycler view.
        holder.txtFulLName.setText("${allPatients.get(position).firstName} ${allPatients.get(position).lastName}")

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
        allPatients.addAll(newList)
        // on below line we are calling notify data
        // change method to notify our adapter.
        notifyDataSetChanged()
    }
}