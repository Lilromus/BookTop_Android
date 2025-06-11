package com.example.booktopprojekt

import android.net.Uri

data class PDFModel(
    val title: String,
    val description: String,
    val thumbnailResId: Int,
    val uriString: String
) {
    val uri: Uri
        get() = Uri.parse(uriString)
}