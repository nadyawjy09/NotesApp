package com.example.tugaslayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var sqliteHelper: DatabaseHelper
    private var loginAttempts = 0
    private val maxLoginAttempts = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sqliteHelper = DatabaseHelper(this)

        // Perbaikan: Cek apakah user sudah terdaftar sebelum menampilkan layout
        if (!sqliteHelper.isUserExist()) {
            startActivity(Intent(this, SignupActivity::class.java))
            finish() // Tutup LoginActivity agar tidak bisa kembali
            return
        }

        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(R.id.username)
        val passwordInput = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (sqliteHelper.readUser(username, password)) { // Menggunakan readUser sebagai pengganti checkLogin
                Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                loginAttempts = 0
            } else {
                loginAttempts++
                if (loginAttempts >= maxLoginAttempts) {
                    showMaxAttemptsDialog()
                } else {
                    showLoginErrorDialog()
                }
            }
        }

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus semua data pengguna?")
            .setPositiveButton("Ya") { _, _ ->
                if (sqliteHelper.deleteAllUsers()) { // Menggunakan deleteAllUsers yang baru ditambahkan
                    Toast.makeText(this, "Semua data berhasil dihapus", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignupActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showLoginErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("Login Gagal")
            .setMessage("Username atau Password salah. Percobaan tersisa: ${maxLoginAttempts - loginAttempts}")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showMaxAttemptsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Terlalu Banyak Percobaan")
            .setMessage("Anda telah mencoba login sebanyak $maxLoginAttempts kali. Aplikasi akan keluar.")
            .setPositiveButton("Keluar") { _, _ -> finishAffinity() }
            .setCancelable(false)
            .show()
    }
}

// Tambahkan fungsi berikut di DatabaseHelper:

fun DatabaseHelper.deleteAllUsers(): Boolean {
    val db = writableDatabase
    val deletedRows = db.delete("data", null, null)
    db.close()
    return deletedRows > 0
}
