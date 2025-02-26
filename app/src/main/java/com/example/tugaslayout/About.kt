package com.example.tugaslayout

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.IOException

class About : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var btnExit: Button
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val PREFS_NAME = "UserPrefs"
        private const val IMAGE_KEY = "profile_image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        profileImage = findViewById(R.id.profileImage)
        btnExit = findViewById(R.id.btnExit)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Load gambar dari SharedPreferences
        loadProfileImage()

        profileImage.setOnClickListener {
            pickImageFromGallery()
        }

        btnExit.setOnClickListener {
            val intent = Intent(this, HBook::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                profileImage.setImageBitmap(bitmap)
                saveProfileImage(bitmap) // Simpan gambar saat diubah
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveProfileImage(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()

        val editor = sharedPreferences.edit()
        editor.putString(IMAGE_KEY, android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT))
        editor.apply()
    }

    private fun loadProfileImage() {
        val imageString = sharedPreferences.getString(IMAGE_KEY, null)
        if (imageString != null) {
            val imageBytes = android.util.Base64.decode(imageString, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            profileImage.setImageBitmap(bitmap)
        }
    }
}
