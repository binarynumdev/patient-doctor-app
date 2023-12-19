package com.consulmedics.patientdata.fragments.addeditpatient

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.consulmedics.patientdata.data.api.response.BaseResponse
import com.consulmedics.patientdata.databinding.FragmentPrintBinding
import androidx.lifecycle.Observer
import android.content.Context
import android.net.Uri
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentInfo
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PrintFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrintFragment : BaseAddEditPatientFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentPrintBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentPrintBinding.inflate(inflater, container, false)


        sharedViewModel.printResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is BaseResponse.Success -> {
                    val pdfFile = it.data?.result_file
                    if (pdfFile != null) {
                        val uriPdfPath = FileProvider.getUriForFile(
                            requireContext(),
                            requireActivity().applicationContext.packageName + ".provider",
                            pdfFile
                        )

                        // Create an intent to open the PDF file
                        val pdfOpenIntent = Intent(Intent.ACTION_VIEW)
                        pdfOpenIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        pdfOpenIntent.clipData = ClipData.newRawUri("", uriPdfPath)
                        pdfOpenIntent.setDataAndType(uriPdfPath, "application/pdf")
                        pdfOpenIntent.addFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )

                        try {
                            //startActivity(pdfOpenIntent)

                            // Print the PDF using the PrintManager
                            val printManager =
                                requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager
                            val jobName = "PDF Print Job"

                            val printAdapter = PdfPrintDocumentAdapter(
                                requireContext(),
                                uriPdfPath
                            )

                            printManager.print(
                                jobName,
                                printAdapter,
                                null
                            )

                        } catch (activityNotFoundException: ActivityNotFoundException) {
                            Toast.makeText(
                                requireContext(),
                                "There is no app to load corresponding PDF",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                else -> {
                    // Handle other cases (if any)
                }
            }
        })


        binding.btnPrintLabel.setOnClickListener{
            Log.e("PRINT LABEL", "PRINT LABEL")
            sharedViewModel.printInsurance()
        }
        binding.btnPrintMedical.setOnClickListener{
            Log.e("PRINT MEDICALS", "PRINT MEDICALS")
            sharedViewModel.printReceipt()
        }
//        return inflater.inflate(R.layout.fragment_print, container, false)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PrintFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrintFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}



class PdfPrintDocumentAdapter(
    private val context: Context,
    private val documentUri: Uri
) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }

        val info = PrintDocumentInfo.Builder("document.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .build()

        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {
        var input: InputStream? = null
        var output: FileOutputStream? = null

        try {
            input = context.contentResolver.openInputStream(documentUri)
            output = FileOutputStream(destination.fileDescriptor)

            if (input != null) {
                val buffer = ByteArray(4096)
                var bytesRead: Int

                while (input.read(buffer).also { bytesRead = it } >= 0) {
                    output.write(buffer, 0, bytesRead)
                }

                callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            } else {
                callback.onWriteFailed("Error opening PDF file")
            }
        } catch (e: IOException) {
            callback.onWriteFailed(e.message)
        } finally {
            try {
                input?.close()
                output?.close()
            } catch (e: IOException) {
                // Handle error closing streams
            }
        }
    }
}