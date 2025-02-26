package com.example.tugaslayout

import android.graphics.Bitmap
import android.graphics.BitmapFactory


data class Book(
    val id: Int,  // ID ditambahkan
    val nama: String,
    val namaPanggilan: String,
    val email: String,
    val alamat: String,
    val tglLahir: String,
    val noHp: String,
    val imgBook: Bitmap?
)

// Fungsi untuk decode BLOB ke Bitmap
fun decodeBlobToBitmap(blob: ByteArray): Bitmap? {
    return BitmapFactory.decodeByteArray(blob, 0, blob.size)
}
