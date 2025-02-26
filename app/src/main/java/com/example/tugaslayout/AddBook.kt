package com.example.tugaslayout

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.util.*
import android.util.Log

class AddBook : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var nama: EditText
    private lateinit var namaPanggilan: EditText
    private lateinit var email: EditText
    private lateinit var alamat: EditText
    private lateinit var tglLahir: EditText
    private lateinit var noHp: EditText
    private lateinit var submit: Button
    private lateinit var display: Button
    private lateinit var edit: Button
    private lateinit var imgbook: ImageView
    private var bookId: Int? = null
    private var imageUri: Uri? = null


    companion object {
        const val PICK_IMAGE_REQUEST = 1
        const val CAPTURE_IMAGE_REQUEST = 2
        const val CAMERA_PERMISSION_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tbook)

        dbHelper = DatabaseHelper(this)
        findid()

        // Cek apakah ada ID buku yang dikirim untuk edit
        bookId = intent.getIntExtra("BOOK_ID", -1)
        if (bookId != -1) {
            loadBookData(bookId!!)
        }

        insertOrUpdateData()
        setupImageSelection()
        setupDatePicker()
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val selectedImageUri: Uri? = data?.data
                    Log.d("DEBUG", "Gambar dari galeri: $selectedImageUri")

                    if (selectedImageUri != null) {
                        imageUri = selectedImageUri
                        imgbook.setImageURI(selectedImageUri) // Pakai setImageURI langsung
                    }
                }
                CAPTURE_IMAGE_REQUEST -> {
                    Log.d("DEBUG", "Gambar dari kamera: $imageUri")

                    if (imageUri != null) {
                        imgbook.setImageURI(imageUri) // Gunakan setImageURI
                    }
                }
            }
        } else {
            Log.d("DEBUG", "onActivityResult: Gagal atau dibatalkan")
        }
    }





    private fun setupImageSelection() {
        imgbook.setOnClickListener {
            val options = arrayOf("Pilih dari Galeri", "Ambil Foto")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Pilih Gambar")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> pickImageFromGallery()
                    1 -> checkCameraPermission()
                }
            }
            builder.show()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
        } else {
            captureImageFromCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImageFromCamera()
            } else {
                Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun captureImageFromCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        }
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST)
    }


    private fun insertOrUpdateData() {
        submit.setOnClickListener {
            val newName = nama.text.toString().trim()
            val newNamaPanggilan = namaPanggilan.text.toString().trim()
            val newEmail = email.text.toString().trim()
            val newAlamat = alamat.text.toString().trim()
            val newTglLahir = tglLahir.text.toString().trim()
            val newNoHp = noHp.text.toString().trim()

            // Cek apakah ada field yang kosong
            if (newName.isEmpty() || newNamaPanggilan.isEmpty() || newEmail.isEmpty() ||
                newAlamat.isEmpty() || newTglLahir.isEmpty() || newNoHp.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Silakan isi semua data terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imageByte = ImageViewToByte(imgbook)

            val cv = ContentValues().apply {
                put("imgbook", imageByte)
                put("nama", newName)
                put("namapanggilan", newNamaPanggilan)
                put("email", newEmail)
                put("alamat", newAlamat)
                put("tgllahir", newTglLahir)
                put("nohp", newNoHp)
            }

            val sqLiteDatabase = dbHelper.writableDatabase
            if (bookId != -1) {
                val result = sqLiteDatabase.update("book", cv, "id = ?", arrayOf(bookId.toString()))
                if (result > 0) {
                    Toast.makeText(this, "Data diperbarui", Toast.LENGTH_SHORT).show()
                    goToHBook()
                } else {
                    Toast.makeText(this, "Gagal memperbarui", Toast.LENGTH_SHORT).show()
                }
            } else {
                val recInsert = sqLiteDatabase.insert("book", null, cv)
                if (recInsert != -1L) {
                    Toast.makeText(this, "Data disimpan", Toast.LENGTH_SHORT).show()
                    goToHBook()
                }
            }
        }
    }

    private fun goToHBook() {
        val intent = Intent(this, HBook::class.java)
        startActivity(intent)
        finish()
    }

    private fun ImageViewToByte(imgbook: ImageView): ByteArray {
        val bitmap = if (imageUri != null) {
            MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
        } else {
            val drawable = imgbook.drawable
            if (drawable is BitmapDrawable) {
                drawable.bitmap
            } else {
                val width = drawable.intrinsicWidth
                val height = drawable.intrinsicHeight
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bmp)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            }
        }

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return stream.toByteArray()
    }



    private fun findid() {
        imgbook = findViewById(R.id.avatar)
        nama = findViewById(R.id.edit_name)
        namaPanggilan = findViewById(R.id.edit_namaPanggilan)
        email = findViewById(R.id.edit_email)
        alamat = findViewById(R.id.edit_alamat)
        tglLahir = findViewById(R.id.edit_tglLahir)
        noHp = findViewById(R.id.edit_noHp)
        submit = findViewById(R.id.btn_submit)
        display = findViewById(R.id.btn_display)
        edit = findViewById(R.id.btn_edit)
    }

    private fun loadBookData(id: Int) {
        val book = dbHelper.getBookById(id)
        book?.let {
            nama.setText(it.nama)
            namaPanggilan.setText(it.namaPanggilan)
            email.setText(it.email)
            alamat.setText(it.alamat)
            tglLahir.setText(it.tglLahir)
            noHp.setText(it.noHp)

            if (it.imgBook != null) {
                imgbook.setImageBitmap(it.imgBook)
            }
        }
    }

    private fun setupDatePicker() {
        tglLahir.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    tglLahir.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                year,
                month,
                day
            )
            datePicker.show()
        }
    }
}