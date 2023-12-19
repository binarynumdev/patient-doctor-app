package com.consulmedics.patientdata.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.ConfirmDialogBinding

class ConfirmationDialog(title: String, message: String) : DialogFragment() {
    private var _binding : ConfirmDialogBinding? = null
    private val binding get() = _binding!!
    private var title = title
    private var message = message
    private var postiveClickListener: ((Int) -> Unit)? = null
    private var negativeClickListener: ((Int) -> Unit)? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ConfirmDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            textTitle.text = title
            textMessage.text = message
            btnCancel.setOnClickListener {
                negativeClickListener?.invoke(0)
            }
            btnOk.setOnClickListener {
                postiveClickListener?.invoke(1)
            }
        }
    }
    fun setPostiveClickListener(listener: (Int) -> Unit){
        postiveClickListener = listener
    }
    fun setNegativeClickListener(listener: (Int) -> Unit){
        negativeClickListener = listener
    }
    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}