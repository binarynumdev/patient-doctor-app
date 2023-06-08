package com.consulmedics.patientdata.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.consulmedics.patientdata.R


class MainStepper @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, defStyleRes: Int = 0) : ConstraintLayout(context, attrs, defStyle, defStyleRes) {

    init {
        LayoutInflater.from(context).inflate(R.layout.components_main_step_indicator, this, true)

    }

}