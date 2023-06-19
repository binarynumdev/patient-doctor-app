package com.consulmedics.patientdata.components

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.adapters.AddressDialogAdapter
import com.consulmedics.patientdata.databinding.FragmentSelectBottomSheetDialogListDialogItemBinding
import com.consulmedics.patientdata.databinding.FragmentSelectBottomSheetDialogListDialogBinding
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    SelectBottomSheetDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class SelectBottomSheetDialogFragment(radioOptions: Array<String>, isCustomAdapter: Boolean, modalTitle: String = "") : BottomSheetDialogFragment() {
    private var radioOptions = radioOptions
    private var modalTitle: String = modalTitle
    private var _binding: FragmentSelectBottomSheetDialogListDialogBinding? = null
    private var itemClickListener: ((Int) -> Unit)? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val isCustomAdapter = isCustomAdapter
    private lateinit var listAdapter: RecyclerView.Adapter<*>
    init {
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG_NAME, "View Created On the BottomSheetDialog")

        _binding =
            FragmentSelectBottomSheetDialogListDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            Log.e(TAG_NAME, "View Created On the BottomSheetDialog")
            if(!isCustomAdapter)
               adapter =StringAdapter(radioOptions)
            else
                adapter = listAdapter
        }
        if(modalTitle != ""){
            binding.dialogTitle.text = modalTitle
        }


    }
    fun setAdapter(arrayAdapter:  RecyclerView.Adapter<*>){
        listAdapter = arrayAdapter
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
    private inner class ViewHolder internal constructor(binding: FragmentSelectBottomSheetDialogListDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal val text: TextView = binding.text
    }

    private fun onItemClicked(itemId: Int) {
        itemClickListener?.invoke(itemId)
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        itemClickListener = listener
    }
    private inner class StringAdapter internal constructor(private val radioOptions: Array<String>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                FragmentSelectBottomSheetDialogListDialogItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.apply {
                text = radioOptions[position]
                setOnClickListener {
                    onItemClicked(position)
                }
            }
        }

        override fun getItemCount(): Int {
            return radioOptions.size
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}