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
import com.consulmedics.patientdata.MyApplication.Companion.patientDetailsActivityRequestCode
import com.consulmedics.patientdata.PatientDataTabActivity
import com.consulmedics.patientdata.databinding.RowPatientItemBinding
import com.consulmedics.patientdata.models.Patient
class PatientAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,

    patients: List<Patient>
) :
    ArrayAdapter<Patient>(mContext, mLayoutResourceId, patients) {
    private val city: MutableList<Patient> = ArrayList(patients)
    private var allCities: List<Patient> = ArrayList(patients)
    private lateinit var _binding: RowPatientItemBinding
    override fun getCount(): Int {
        return city.size
    }
    override fun getItem(position: Int): Patient {
        return city[position]
    }
    override fun getItemId(position: Int): Long {
        return city[position].uid!!.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        _binding = RowPatientItemBinding.inflate(LayoutInflater.from(context))
        var convertView = convertView
        if (convertView == null) {
            val inflater = (mContext as Activity).layoutInflater
            convertView = _binding.root
        }
        try {
            val patient: Patient = getItem(position)
            patient.patientID?.let { Log.e("PATIENTID", it) }
            val patientIDView:TextView = _binding.textPatientID
            patientIDView.text = patient.patientID
            val patientNameView:TextView = _binding.textPatientName
            patientNameView.text = patient.firstName + " "+ patient.lastName
            val btnEditPatient: ImageButton = _binding.btnEdit
            btnEditPatient.setOnClickListener{view->
                run{
                    Log.e("ONCLICK", "${patient.patientID}")
                    var i1 = Intent(convertView!!.context, PatientDataTabActivity::class.java)
                    i1.putExtra("patient_data", patient)
                    (mContext as Activity).startActivityForResult(i1, patientDetailsActivityRequestCode)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView!!
    }
}