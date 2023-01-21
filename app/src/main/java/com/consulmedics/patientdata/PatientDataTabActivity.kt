package com.consulmedics.patientdata

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Bundle
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.viewpager.widget.ViewPager
import com.consulmedics.patientdata.databinding.ActivityPatientDataTabBinding
import com.consulmedics.patientdata.models.Patient
import com.consulmedics.patientdata.ui.main.SectionsPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class PatientDataTabActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientDataTabBinding
    private var patient: Patient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPatientDataTabBinding.inflate(layoutInflater)
        setContentView(binding.root)
        patient = intent.getSerializableExtra("patient_data") as Patient
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, patient!!)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener { view ->
            run {
                 generatePDF()
            }

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

        paint.style = Paint.Style.STROKE
        canvas.drawRect(Rect(45, 45, 620, 380), paint)

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
        val birthDateFormat = SimpleDateFormat("dd.MM.yyyy")
        canvas.drawText("1 Patient ID: ${patient?.patientID}", 60F, 60F, title)
        canvas.drawText("2 Patient Name: ${patient?.firstName} ${patient?.lastName}", 60F, 90F, title)
        canvas.drawText("3 Date Of Birth: ${birthDateFormat.format(patient?.birthDate)}", 60F, 120F, title)
        canvas.drawText("4 Gender: ${patient?.gender}", 60F, 150F, title)
        canvas.drawText("5 Street: ${patient?.street}", 60F, 180F, title)
        canvas.drawText("6 City: ${patient?.city}", 60F, 210F, title)
        canvas.drawText("7 Post Code: ${patient?.postCode}", 60F, 240F, title)
        canvas.drawText("8 Insurance Number: ${patient?.insuranceNumber}", 60F, 270F, title)
        canvas.drawText("9 Insurance Name: ${patient?.insuranceName}", 60F, 300F, title)
        canvas.drawText("10 Insurance Status: ${patient?.insuranceStatus}", 60F, 330F, title)
        canvas.drawText("11 -----------------", 60F, 360F, title)


        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        title.setColor(ContextCompat.getColor(this, R.color.purple_200))
        title.textSize = 15F

        // below line is used for setting
        // our text to center of PDF.
        title.textAlign = Paint.Align.CENTER
        canvas.drawText("This is sample document for our app.", 396F, 560F, title)


        document.finishPage(page)
        document.writeTo(fileOutput)
        document.close()
        val intent = Intent()
        intent.action = ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
        val uriPdfPath =
            FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", file)
        Log.d("pdfPath", "" + uriPdfPath);
        val pdfOpenIntent = Intent(Intent.ACTION_VIEW)
        pdfOpenIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        pdfOpenIntent.clipData = ClipData.newRawUri("", uriPdfPath)
        pdfOpenIntent.setDataAndType(uriPdfPath, "application/pdf")
        pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        try {
            startActivity(pdfOpenIntent)
        } catch (activityNotFoundException: ActivityNotFoundException) {
            Toast.makeText(this, "There is no app to load corresponding PDF", Toast.LENGTH_LONG)
                .show()
        }
    }

}