package com.consulmedics.patientdata.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity


abstract  class BaseActivity: AppCompatActivity() {
    lateinit var progressDialog: ProgressDialog
    fun showLoadingSpinner(title: String, message: String){
        progressDialog = ProgressDialog(this@BaseActivity)
        progressDialog.setTitle(title)
        progressDialog.setMessage(message)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        Log.e("Fragment", "Show Loading Dailog")
    }
    fun hideLoadingSpinner(){
        if(this::progressDialog.isInitialized){
            progressDialog.dismiss()
            Log.e("Fragment", "Hide Loading Dailog")
        }
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
    fun logout(){
        val intent = Intent(this, LoginActivity::class.java)
        // Clear the activity stack and set the login activity as the new root
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // Start the login activity
        startActivity(intent)
    }
}