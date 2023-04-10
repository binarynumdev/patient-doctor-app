package com.consulmedics.patientdata.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.data.model.Address

class AddressDialogAdapter(context: Context, private val items: List<Address>) : ArrayAdapter<Address>(context, 0, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.row_address_item, parent, false)
        val textAddressView = view.findViewById<TextView>(R.id.txtAddress)
        val currentAddress: Address = items.get(position)
        if(currentAddress.uid == null){
            textAddressView.setText("Choose new address")
        }
        else if (currentAddress.uid == -99){
            textAddressView.setText("Fill address form manually")
        }
        else{
            textAddressView.setText("${currentAddress.streetName}, ${currentAddress.streetNumber} ${currentAddress.city} ${currentAddress.postCode}")
        }

        return view
    }
}