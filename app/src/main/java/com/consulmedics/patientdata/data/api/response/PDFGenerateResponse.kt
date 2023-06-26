package com.consulmedics.patientdata.data.api.response

import com.google.gson.annotations.SerializedName
import java.io.File

data class PDFGenerateResponse(
    @SerializedName("pdf_file_name")
    var result_file: File,
)
