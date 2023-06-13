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
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.components.models.StepItem
import com.consulmedics.patientdata.databinding.ActivityLoginBinding
import com.consulmedics.patientdata.databinding.ComponentsMainStepIndicatorBinding
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import com.google.android.material.progressindicator.CircularProgressIndicator


class MainStepper @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, defStyleRes: Int = 0) : ConstraintLayout(context, attrs, defStyle, defStyleRes) {

    private lateinit var binding: ComponentsMainStepIndicatorBinding
    private var callback: StepperCallback? = null
    private var pageTitleList: List <StepItem> = listOf ()
    private lateinit var stepProgressBar: CircularProgressIndicator
    private lateinit var stepCountTextView: TextView
    private lateinit var stepTitleTextView: TextView
    private lateinit var stepActionButton: Button
    private lateinit var stepActionButtonFrame: FrameLayout
    init {
        val inflater = LayoutInflater.from(context).inflate(R.layout.components_main_step_indicator, this, true)
        val rootLayout  = inflater.findViewById<ConstraintLayout>(R.id.mainLayout)
        stepActionButton = inflater.findViewById<Button>(R.id.btnStepAction)
        stepProgressBar = inflater.findViewById<CircularProgressIndicator>(R.id.progress_bar)
        stepCountTextView = inflater.findViewById<TextView>(R.id.stepCountTextView)
        stepTitleTextView = inflater.findViewById<TextView>(R.id.stepTitleTextView)
        stepActionButtonFrame = inflater.findViewById<FrameLayout>(R.id.stepActionButtonFrame)
        rootLayout.setOnClickListener {
            Log.e(TAG_NAME, "Clicked on stepper")
            callback?.stepperRootViewClicked()
        }
        stepActionButton.setOnClickListener {
            callback?.stepActionButtonClicked(stepActionButton.text.toString())
        }

    }
    fun setCallback(callback: StepperCallback) {
        this.callback = callback
    }

    fun setPageList(_pageTitleList: List<StepItem>) {
        pageTitleList = _pageTitleList
    }

    fun go(pageIndex: Int) {
        stepCountTextView.setText("${pageIndex + 1}/${pageTitleList.count()}")
        stepTitleTextView.setText(pageTitleList[pageIndex].stepTitle)
        if(pageTitleList[pageIndex].actionButtonString != ""){
            stepActionButtonFrame.visibility = VISIBLE
            stepActionButton.text = pageTitleList[pageIndex].actionButtonString
        }
        else{
            stepActionButtonFrame.visibility = GONE
        }
        stepProgressBar.progress = (((pageIndex + 1) / pageTitleList.count().toDouble())* 100).toInt()
    }
}
interface StepperCallback{
    fun stepperRootViewClicked()
    fun onStepItemClicked(index: Int)

    fun stepActionButtonClicked(buttonText: String)
}