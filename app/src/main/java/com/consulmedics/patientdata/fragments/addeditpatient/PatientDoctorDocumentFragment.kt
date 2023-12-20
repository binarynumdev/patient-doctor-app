package com.consulmedics.patientdata.fragments.addeditpatient

import android.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import com.consulmedics.patientdata.databinding.FragmentPatientDoctorDocumentBinding
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class PatientDoctorDocumentFragment : BaseAddEditPatientFragment() {
    private var _binding: FragmentPatientDoctorDocumentBinding? = null
    val binding get() = _binding!!
    private var capturedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPatientDoctorDocumentBinding.inflate(inflater, container, false)
        binding.apply {
            editDiagnosis.doAfterTextChanged {
                sharedViewModel.setDiagnosis(it.toString())
            }
            editHealthStatus.doAfterTextChanged {
                sharedViewModel.setHealthStatus(it.toString())
            }
            takePhotoButton.setOnClickListener {
                Log.e("This is for taking photos", "arrived")
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 1888)

            }
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1888) {
            if (data != null) {
                val image = data.extras!!["data"] as Bitmap?
                val savedImageUri = saveImageToFile(image)
                val imageView = _binding?.photoView as ImageView
                imageView.setImageBitmap(image)
                capturedImageUri = savedImageUri
                sharedViewModel.setPhotoUrl(capturedImageUri.toString())
                Log.e("This is the captured image's url" , "${capturedImageUri}")
            }
        }
    }

    fun getCurrentTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
    }
    private fun saveImageToFile(bitmap: Bitmap?): Uri? {
        if (bitmap == null) {
            return null
        }

        // Convert the bitmap to grayscale
        val grayscaleBitmap = toGrayscale(bitmap)

        // Get the directory for saving pictures in the external storage
        val imagesDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // Generate a unique image name based on the current time
        var imageName = getCurrentTime() + "captured_image.png"

        // Create a File object representing the destination file for the image
        val imageFile = File(imagesDir, imageName)

        try {
            // Use runBlocking to perform blocking I/O operations (e.g., file writing) in a coroutine
            runBlocking {
                // Open a FileOutputStream for the image file
                FileOutputStream(imageFile).use { out ->
                    // Compress the grayscale bitmap and write it to the FileOutputStream
                    grayscaleBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    Log.e("This is the picture saved part", "arrived")
                }
            }
        } catch (e: IOException) {
            // Handle IOException, typically by printing the stack trace
            e.printStackTrace()
            return null
        }

        // Return the URI of the saved image file
        return Uri.fromFile(imageFile)
    }

    // Function to convert a color bitmap to grayscale
    private fun toGrayscale(src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height

        val grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayBitmap)
        val paint = Paint()

        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f) // Set saturation to 0 for grayscale

        val colorMatrixFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixFilter

        canvas.drawBitmap(src, 0f, 0f, paint)

        return grayBitmap
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.patientData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.editDiagnosis.setText(sharedViewModel.patientData.value?.diagnosis)
            binding.editHealthStatus.setText(sharedViewModel.patientData.value?.healthStatus)
            val fileUriString = sharedViewModel.patientData?.value?.photoUrl
            val fileUri = Uri.parse(fileUriString)
            if (fileUri != null) {
                // Set the image to the ImageView using the file URI
                binding.photoView.setImageURI(fileUri)
            } else {
                binding.photoView.setImageResource(R.drawable.ic_menu_gallery)
            }
        })
    }

}