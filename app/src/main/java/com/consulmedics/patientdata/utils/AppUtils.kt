package com.consulmedics.patientdata.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.caverock.androidsvg.SVG
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.utils.AppConstants.CERT_FILE_PATH
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import java.io.*

class AppUtils {
    companion object {
        fun checkUserID(): Boolean {
            var certFile: File = File(Environment.getExternalStorageDirectory(),CERT_FILE_PATH)
            var isUserIDExsist:Boolean = false
            if(certFile.exists()){
                var fileInputStream = FileInputStream(certFile)
                var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder: StringBuilder = StringBuilder()
                var text: String? = null
                while ({ text = bufferedReader.readLine(); text }() != null) {
                    stringBuilder.append(text)
                }

                val decrypted:String? = AESEncyption.decrypt(stringBuilder.toString())

                fileInputStream.close()
                if(decrypted != null){
                    val userInfo: List<String> = decrypted!!.split("@")
                    if(userInfo.count() == 2){
                        val userID: String = userInfo[0]
                        val deviceID: String = userInfo[1]
                        if(userID != "" && deviceID != ""){
                            Log.e(AppConstants.TAG_NAME, "UserID:$userID , DeviceID:$deviceID")
                            isUserIDExsist = true
                        }
                    }
                }
            }
            return isUserIDExsist
        }

        fun svgStringToBitmap(svgString: String): Bitmap {
            val svg: SVG = SVG.getFromString(svgString)
            val svgWidth = if (svg.documentWidth != -1f) svg.documentWidth else 500f
            val svgHeight = if (svg.documentHeight != -1f) svg.documentHeight else 500f
            Log.e(AppConstants.TAG_NAME,
                "${svgWidth}:${svgHeight}:${svg.documentHeight}:${svg.documentWidth}:${svg.documentDescription}"
            )
            val newBM = Bitmap.createBitmap(
                Math.ceil(svgWidth.toDouble()).toInt(),
                Math.ceil(svgHeight.toDouble()).toInt(),
                Bitmap.Config.ARGB_8888
            )
            val bmcanvas = Canvas(newBM)
            bmcanvas.drawRGB(255, 255, 255)
            svg.renderToCanvas(bmcanvas)
            return newBM
        }

        fun mmToPt(mm: Float): Float{
            return  mm / 25.4F * 72
        }

        fun validateMaxLineMaxLetterForEditText(editTextField: EditText, text: CharSequence?, context: Context) {
            if (text != null) {
                val stringList = text.split('\n')
                stringList.forEach {
                    if(it.length > AppConstants.MAX_LEN_PER_LINE){
                        editTextField.getText().delete(editTextField.getSelectionEnd() - 1,editTextField.getSelectionStart());
                        Toast.makeText(context, R.string.edit_max_letter_per_line_error, Toast.LENGTH_LONG).show();
                    }
                }
            }
            if(editTextField.lineCount > AppConstants.MAX_LINE_MEDICALS){
                editTextField.getText().delete(editTextField.getSelectionEnd() - 1,editTextField.getSelectionStart());
                Toast.makeText(context, R.string.edit_max_line_error, Toast.LENGTH_LONG).show();
            }
            if (text != null) {
                if(text.length > AppConstants.MAX_LEN_MEDICALS){
                    editTextField.getText().delete(editTextField.getSelectionEnd() - 1,editTextField.getSelectionStart());
                    Toast.makeText(context, R.string.edit_max_letters_error, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}