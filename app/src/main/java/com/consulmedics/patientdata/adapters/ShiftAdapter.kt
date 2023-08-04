package com.consulmedics.patientdata.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.consulmedics.patientdata.Converters
import com.consulmedics.patientdata.MyAppDatabase
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.data.api.response.LoadShiftApiResponse
import com.consulmedics.patientdata.data.model.Patient
import com.consulmedics.patientdata.data.model.PatientShift
import com.consulmedics.patientdata.databinding.RowShiftItemBinding
import com.consulmedics.patientdata.repository.AddressRepository
import com.consulmedics.patientdata.repository.PatientRepository
import com.consulmedics.patientdata.utils.AppConstants
import com.consulmedics.patientdata.utils.AppUtils

class ShiftAdapter(
    private val mContext: Context,
    val patientItemOnClickInterface: ShiftItemClickInterface? = null
) :
    RecyclerView.Adapter<ShiftAdapter.ViewHolder>(){
    val patientRepository: PatientRepository = PatientRepository(MyAppDatabase.getDatabase(mContext).patientDao())
    private val allShifts = ArrayList<PatientShift>()
    inner class ViewHolder(val itemBinding: RowShiftItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflating our layout file for each item of recycler view.
        Log.e(AppConstants.TAG_NAME, "ONCREATEVIEWHOLDER")
        val binding  = RowShiftItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentShift = allShifts.get(position)
        holder.itemView.post {
            holder.itemBinding.apply {
                val converters: Converters = Converters()
                textShiftTime.text = "${converters.dateToFormatedString(currentShift.startDate, "yyyy-MM-dd HH:mm:ss", "dd.MM.yyyy HH")} - ${converters.dateToFormatedString(currentShift.endDate, "yyyy-MM-dd HH:mm:ss", "H")} Uhr"
                if(currentShift.serviceType == 1 || currentShift.serviceType == 0){
                    imageServiceType.setImageDrawable(mContext.getDrawable(R.drawable.ic_car))
                    textServiceType.setText("Visit Patient")
                }
                else if (currentShift.serviceType == 2){
                    imageServiceType.setImageDrawable(mContext.getDrawable(R.drawable.ic_home))
                    textServiceType.setText("Stay Hospital")
                }
                textShiftName.text = currentShift.nameBidding
                if(patientItemOnClickInterface == null){
                    btnFillShiftDetails.visibility = GONE
                }
                else{
                    btnFillShiftDetails.setOnClickListener {
                        patientItemOnClickInterface.onShiftEditClick(currentShift)
                    }
                }
                patientRepository.getPatientsByShift(currentShift)?.apply {
                    if(this.size > 0){
                        textIsFinished.text = "${this.size} Patients"
                        textIsFinished.setBackgroundResource(R.drawable.bg_blue_circle)
                    }
                }


                if(currentShift.doctorNote.isNotEmpty()){
                    btnUpload.visibility = VISIBLE
                    btnUpload.setOnClickListener {
                        patientItemOnClickInterface?.onShiftUploadClick(currentShift)
                    }
//                    btnFillShiftDetails.setImageDrawable(mContext.getDrawable(R.drawable.ic_upload))
//                    textIsFinished.text = mContext.getString(R.string.completed)
//                    textIsFinished.setBackgroundResource(R.drawable.bg_blue_circle)
                }

            }
        }


    }

    override fun getItemCount(): Int {
        // on below line we are
        // returning our list size.
        return allShifts.size
    }

    // below method is use to update our list of notes.
    fun updateList(newList: List<PatientShift>) {
        // on below line we are clearing
        // our notes array list
        allShifts.clear()

        // on below line we are adding a
        // new list to our all notes list.
        Log.e(AppConstants.TAG_NAME, "Update List: ${newList.count()}")
        allShifts.addAll(newList)
        // on below line we are calling notify data
        // change method to notify our adapter.
        notifyDataSetChanged()
    }
}
interface ShiftItemClickInterface{
    fun onShiftEditClick(patient: PatientShift)
    fun onShiftUploadClick(currentShift: PatientShift)
}