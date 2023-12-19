package com.consulmedics.patientdata.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.data.model.Address

class AddressDialogAdapter(context: Context, private val items: List<Address>) : RecyclerView.Adapter<AddressDialogAdapter.ViewHolder>() {
    private var context = context
    private var itemClickListener: ((Int) -> Unit)? = null

    private fun onItemClicked(itemId: Int) {
        itemClickListener?.invoke(itemId)
    }
    fun setOnItemClickListener(listener: (Int) -> Unit) {
        itemClickListener = listener
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        internal val textAddressView: TextView = itemView.findViewById<TextView>(R.id.txtAddress)
    }

    // Create and inflate the item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_address_item, parent, false)
        return ViewHolder(view)
    }

    // Bind the data to the views
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAddress: Address = items.get(position)
        if(currentAddress.uid == null){
            holder.textAddressView.setText("Choose new address")
        }
        else if (currentAddress.uid == -99){
            holder.textAddressView.setText("Fill address form manually")
        }
        else if( currentAddress.uid == 10) {
            holder.textAddressView.setText("Get current GPS-location")
        }
        else{
            holder.textAddressView.setText("${currentAddress.streetName}, ${currentAddress.streetNumber} ${currentAddress.city} ${currentAddress.postCode}")
        }
        holder.textAddressView.setOnClickListener {
            onItemClicked(position)
        }

    }
    override fun getItemCount(): Int {
        return items.size
    }


}