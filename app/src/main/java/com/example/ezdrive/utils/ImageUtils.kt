package com.example.ezdrive.utils

import android.content.Context
import android.net.Uri
import java.io.InputStream

// Fungsi ini sekarang berada di luar class mana pun (top-level)
// dan secara default bersifat publik.
fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        inputStream?.readBytes()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}