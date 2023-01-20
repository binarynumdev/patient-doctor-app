package com.consulmedics.patientdata

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.consulmedics.patientdata.databinding.ActivityPatientDataTabBinding
import com.consulmedics.patientdata.models.Patient
import com.consulmedics.patientdata.ui.main.SectionsPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


class PatientDataTabActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientDataTabBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPatientDataTabBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val patient: Patient = intent.getSerializableExtra("patient_data") as Patient
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, patient)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener { view ->
            generatePDF()
        }
    }
    fun generatePDF(){

        val file = File(getExternalFilesDir(null), "testfile${UUID.randomUUID().toString()}.pdf")
        val fileOutput = FileOutputStream(file)

        var document:PdfDocument = PdfDocument()
        var pageInfo:PageInfo = PageInfo.Builder(842, 595, 1).create();
        var page:Page = document.startPage(pageInfo)
        var paint: Paint = Paint()
        var title: Paint = Paint()
        var canvas:Canvas = page.canvas


        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.textSize = 15F

        // below line is sued for setting color
        // of our text inside our PDF file.
        title.setColor(ContextCompat.getColor(this, R.color.purple_200))

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText("A portal for IT professionals.", 209F, 100F, title)
        canvas.drawText("Geeks for Geeks", 209F, 80F, title)
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        title.setColor(ContextCompat.getColor(this, R.color.purple_200))
        title.textSize = 15F

        // below line is used for setting
        // our text to center of PDF.
        title.textAlign = Paint.Align.CENTER
        canvas.drawText("This is sample document which we have created.", 396F, 560F, title)


        document.finishPage(page)
        document.writeTo(fileOutput)
        document.close()
        Log.e("PDF", file.path)
    }

}