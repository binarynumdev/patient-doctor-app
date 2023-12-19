package com.consulmedics.patientdata.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.caverock.androidsvg.SVG
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.utils.AppConstants.CERT_FILE_PATH
import com.consulmedics.patientdata.utils.AppConstants.TAG_NAME
import java.io.*
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.Locale

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
        fun hideKeyboard(view: View) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        fun mmToPt(mm: Float): Float{
            return  mm / 25.4F * 72
        }
        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
            return false
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
        fun calculateTimeDiffInHours(startDate: String, endDate: String): Long {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val startDateTime = format.parse(startDate)
            val endDateTime = format.parse(endDate)

            val differenceInMillis = abs(endDateTime.time - startDateTime.time)
            val differenceInHours = differenceInMillis / (1000 * 60 * 60)

            return differenceInHours
        }

    }

}