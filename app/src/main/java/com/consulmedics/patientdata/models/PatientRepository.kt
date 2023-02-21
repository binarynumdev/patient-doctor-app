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
import com.consulmedics.patientdata.Converters
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
            val file = File(Environment.getExternalStorageDirectory(), "Patient Receipt ${patient.patientID} ${Date().time}.pdf")
            val fileOutput = FileOutputStream(file)


            val paperWidth: Float = 82F
            val paperHeight: Float = 52F
            val rectCornerX: Float = -14.1F
            val rectCornerY: Float = 16.1F
            val rectCornerRight: Float = 65.1F
            val rectCornerBottom: Float = 66.1F


            val lineLeft: Float = -13.1F
            val lineRight: Float = 65F


            val firstLineY: Float = 1F + 16.1F
            val secondLineY: Float = 11F + 16.1F
            val thirdLineY: Float = 27F + 16.1F
            val fourthLineY: Float = 37F + 16.1F
            val fifthLineY: Float = 48F + 16.1F

            val spaceLineText: Float = 2F


            var document: PdfDocument = PdfDocument()
            var pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder( mmToPt(paperHeight).toInt(),mmToPt(paperWidth).toInt(), 1).create();
            var page: PdfDocument.Page = document.startPage(pageInfo)
            var paint: Paint = Paint()
            var title: Paint = Paint()
            var fieldTitle: Paint = Paint()

            var canvas: Canvas = page.canvas
            canvas.save();
            canvas.rotate(90F, (canvas.width / 2).toFloat(),
                (canvas.height / 2).toFloat()
            );
            paint.style = Paint.Style.STROKE



            canvas.drawRect(Rect(mmToPt(rectCornerX).toInt(), mmToPt(rectCornerY).toInt(), mmToPt(rectCornerRight).toInt(), mmToPt(rectCornerBottom).toInt()), paint)
//
            var redLinePaint: Paint = Paint()
            redLinePaint.style = Paint.Style.STROKE
            redLinePaint.color = Color.RED
            redLinePaint.strokeWidth = 0.3F
            canvas.drawLine(mmToPt(lineLeft) ,mmToPt(firstLineY),mmToPt(lineRight),mmToPt(firstLineY), redLinePaint)


            title.color = Color.RED
            title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
            title.textSize = 9F

            fieldTitle.color = Color.RED
            fieldTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
            fieldTitle.textSize = 6F


            canvas.drawText("Insurance Name", mmToPt(lineLeft), mmToPt(firstLineY + spaceLineText ), fieldTitle)

            canvas.drawText(patient.insuranceName, mmToPt(lineLeft), mmToPt(firstLineY + 6.5F), title)

            canvas.drawLine(mmToPt(lineLeft) ,mmToPt(secondLineY),mmToPt(lineRight),mmToPt(secondLineY), redLinePaint)
            canvas.drawText("Last Name, First Name of Patient", mmToPt(lineLeft), mmToPt(secondLineY + spaceLineText ), fieldTitle)
            canvas.drawText("${patient.lastName} ${patient.firstName}", mmToPt(lineLeft), mmToPt(secondLineY + 6F), title)
            canvas.drawText("${patient.street} ${patient.houseNumber}", mmToPt(lineLeft), mmToPt(secondLineY + 10.5F), title)
            canvas.drawText("${patient.postCode} ${patient.city}", mmToPt(lineLeft), mmToPt(secondLineY + 15F), title)



            canvas.drawText("geb.", mmToPt(48F), mmToPt(34F  ), fieldTitle)
            val converters: Converters = Converters()
            converters.dateToFormatedString(patient.birthDate)
                ?.let { canvas.drawText(it, mmToPt(48F), mmToPt(34F + 5F), title) }

            canvas.drawLine(mmToPt(lineLeft) ,mmToPt(thirdLineY),mmToPt(lineRight),mmToPt(thirdLineY), redLinePaint)
            canvas.drawText("Kassen-Nr", mmToPt(lineLeft), mmToPt(thirdLineY + spaceLineText  ), fieldTitle)
            canvas.drawText("${patient.insuranceNumber}", mmToPt(lineLeft), mmToPt(thirdLineY + 6.5F), title)

            canvas.drawText("Versicherten-Nr", mmToPt(16F), mmToPt(thirdLineY + spaceLineText  ), fieldTitle)
            canvas.drawText("${patient.patientID}", mmToPt(16F), mmToPt(thirdLineY + 6.5F), title)

            canvas.drawText("Status", mmToPt(43F), mmToPt(thirdLineY + spaceLineText  ), fieldTitle)
            canvas.drawText("${patient.insuranceStatus}", mmToPt(43F), mmToPt(thirdLineY + 6.5F), title)

            canvas.drawLine(mmToPt(lineLeft) ,mmToPt(fourthLineY),mmToPt(lineRight),mmToPt(fourthLineY), redLinePaint)
            canvas.drawText("Betriebsstatten-Nr", mmToPt(lineLeft), mmToPt(fourthLineY + spaceLineText  ), fieldTitle)
            canvas.drawText("Arzt-Nr", mmToPt(23F), mmToPt(fourthLineY + spaceLineText  ), fieldTitle)
            canvas.drawText("Datum-Nr", mmToPt(43F), mmToPt(fourthLineY + spaceLineText  ), fieldTitle)
            canvas.drawText("${converters.dateToFormatedString(Date())}", mmToPt(43F), mmToPt(fourthLineY + 6.5F), title)



            canvas.drawLine(mmToPt(lineLeft) ,mmToPt(fifthLineY),mmToPt(lineRight),mmToPt(fifthLineY), redLinePaint)

            canvas.restore();

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