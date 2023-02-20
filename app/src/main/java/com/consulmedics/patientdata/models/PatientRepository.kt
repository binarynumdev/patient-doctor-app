package com.consulmedics.patientdata.models

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.dao.PatientDao
import com.consulmedics.patientdata.utils.AppUtils.Companion.mmToPt
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PatientRepository(private val patientDao: PatientDao)  {
    val allPatients: LiveData<List<Patient>> = patientDao.getAll()

    // on below line we are creating an insert method
    // for adding the note to our database.
    suspend fun insert(patient: Patient) {
        patientDao.insertAll(patient)
    }

    // on below line we are creating a delete method
    // for deleting our note from database.
    suspend fun delete(patient: Patient){
        patientDao.delete(patient)
    }

    // on below line we are creating a update method for
    // updating our note from database.
    suspend fun update(patient: Patient){
        patientDao.updatePatient(patient)
    }

    fun generatePDF(patient: Patient?): File?{

        if(patient != null){
            val file = File(Environment.getExternalStorageDirectory(), "testfile${UUID.randomUUID().toString()}.pdf")
            val fileOutput = FileOutputStream(file)


            val paperWidth: Float = 92F
            val paperHeight: Float = 62F
            val rectCornerX: Float = 5F
            val rectCornerY: Float = 5F
            val rectCornerRight: Float = 87F
            val rectCornerBottom: Float = 57F


            val lineLeft: Float = 7F
            val lineRight: Float = 85F


            val firstLineY: Float = 7F
            val secondLineY: Float = 17F
            val thirdLineY: Float = 35F
            val fourthLineY: Float = 45F
            val fifthLineY: Float = 55F

            val spaceLineText: Float = 2F


            var document: PdfDocument = PdfDocument()
            var pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(mmToPt(paperWidth).toInt(), mmToPt(paperHeight).toInt(), 1).create();
            var page: PdfDocument.Page = document.startPage(pageInfo)
            var paint: Paint = Paint()
            var title: Paint = Paint()
            var canvas: Canvas = page.canvas

            paint.style = Paint.Style.STROKE



            canvas.drawRect(Rect(mmToPt(rectCornerX).toInt(), mmToPt(rectCornerY).toInt(), mmToPt(rectCornerRight).toInt(), mmToPt(rectCornerBottom).toInt()), paint)

            var redLinePaint: Paint = Paint()
            redLinePaint.style = Paint.Style.STROKE
            redLinePaint.color = Color.RED
            redLinePaint.strokeWidth = 0.3F
            canvas.drawLine(mmToPt(lineLeft) ,mmToPt(firstLineY),mmToPt(lineRight),mmToPt(firstLineY), redLinePaint)


            title.color = Color.RED
            title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
            title.textSize = 6F
            canvas.drawText("Patient ID", mmToPt(lineLeft), mmToPt(firstLineY + spaceLineText), title)



            canvas.drawLine(mmToPt(lineLeft) ,mmToPt(secondLineY),mmToPt(lineRight),mmToPt(secondLineY), redLinePaint)
            canvas.drawText("Last Name, First Name of Patient", mmToPt(lineLeft), mmToPt(secondLineY + spaceLineText), title)
            canvas.drawText("geb.", mmToPt(70F), mmToPt(28F), title)

            canvas.drawLine(mmToPt(lineLeft) ,mmToPt(thirdLineY),mmToPt(lineRight),mmToPt(thirdLineY), redLinePaint)
            canvas.drawText("Kassen-Nr", mmToPt(lineLeft), mmToPt(thirdLineY + spaceLineText), title)
            canvas.drawText("Versicherten-Nr", mmToPt(30F), mmToPt(thirdLineY + spaceLineText), title)
            canvas.drawText("Status", mmToPt(65F), mmToPt(thirdLineY + spaceLineText), title)

            canvas.drawLine(mmToPt(lineLeft) ,mmToPt(fourthLineY),mmToPt(lineRight),mmToPt(fourthLineY), redLinePaint)
            canvas.drawText("Betriebsstatten-Nr", mmToPt(lineLeft), mmToPt(fourthLineY + spaceLineText), title)
            canvas.drawText("Arzt-Nr", mmToPt(37F), mmToPt(fourthLineY + spaceLineText), title)
            canvas.drawText("Datum-Nr", mmToPt(65F), mmToPt(fourthLineY + spaceLineText), title)



            canvas.drawLine(mmToPt(lineLeft) ,mmToPt(fifthLineY),mmToPt(lineRight),mmToPt(fifthLineY), redLinePaint)


//        val birthDateFormat = SimpleDateFormat("dd.MM.yyyy")
//        canvas.drawText("1 Patient ID: ${patient?.patientID}", 60F, 60F, title)
//        canvas.drawText("2 Patient Name: ${patient?.firstName} ${patient?.lastName}", 60F, 90F, title)
//        canvas.drawText("3 Date Of Birth: ${birthDateFormat.format(patient?.birthDate)}", 60F, 120F, title)
//        canvas.drawText("4 Gender: ${patient?.gender}", 60F, 150F, title)
//        canvas.drawText("5 Street: ${patient?.street}", 60F, 180F, title)
//        canvas.drawText("6 City: ${patient?.city}", 60F, 210F, title)
//        canvas.drawText("7 Post Code: ${patient?.postCode}", 60F, 240F, title)
//        canvas.drawText("8 Insurance Number: ${patient?.insuranceNumber}", 60F, 270F, title)
//        canvas.drawText("9 Insurance Name: ${patient?.insuranceName}", 60F, 300F, title)
//        canvas.drawText("10 Insurance Status: ${patient?.insuranceStatus}", 60F, 330F, title)
//        canvas.drawText("11 -----------------", 60F, 360F, title)
//
//
//        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
////        title.setColor(ContextCompat.getColor(this, R.color.black))
//        title.textSize = 15F
//
//        // below line is used for setting
//        // our text to center of PDF.
//        title.textAlign = Paint.Align.CENTER
//        canvas.drawText("This is sample document for our app.", 396F, 560F, title)


            document.finishPage(page)
            document.writeTo(fileOutput)
            document.close()
            return file
        }
        else{
            return  null
        }

    }
}