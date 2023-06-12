package com.consulmedics.patientdata.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.databinding.ActivityLoginBinding
import com.consulmedics.patientdata.databinding.ComponentsMainStepIndicatorBinding
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.google.android.material.progressindicator.CircularProgressIndicator


class MainStepper @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, defStyleRes: Int = 0) : ConstraintLayout(context, attrs, defStyle, defStyleRes) {

    private lateinit var binding: ComponentsMainStepIndicatorBinding
    private var callback: StepperCallback? = null
    private var pageTitleList: List <String> = listOf ()
    private lateinit var stepProgressBar: CircularProgressIndicator
    private lateinit var stepCountTextView: TextView
    private lateinit var stepTitleTextView: TextView

    init {
        val inflater = LayoutInflater.from(context).inflate(R.layout.components_main_step_indicator, this, true)
        val rootLayout  = inflater.findViewById<ConstraintLayout>(R.id.mainLayout)
        stepProgressBar = inflater.findViewById<CircularProgressIndicator>(R.id.progress_bar)
        stepCountTextView = inflater.findViewById<TextView>(R.id.stepCountTextView)
        stepTitleTextView = inflater.findViewById<TextView>(R.id.stepTitleTextView)
        rootLayout.setOnClickListener {
            Log.e(TAG_NAME, "Clicked on stepper")
            callback?.stepperRootViewClicked()
        }
    }
    fun setCallback(callback: StepperCallback) {
        this.callback = callback
    }

    fun setPageList(_pageTitleList: List<String>) {
        pageTitleList = _pageTitleList
    }

    fun go(pageIndex: Int) {
        stepCountTextView.setText("${pageIndex + 1}/${pageTitleList.count()}")
        stepTitleTextView.setText(pageTitleList[pageIndex])
        stepProgressBar.progress = (((pageIndex + 1) / pageTitleList.count().toDouble())* 100).toInt()
    }
}
interface StepperCallback{
    fun stepperRootViewClicked()
    fun onStepItemClicked(index: Int)
}