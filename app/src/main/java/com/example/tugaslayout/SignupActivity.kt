package com.example.tugaslayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var sqliteHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        sqliteHelper = DatabaseHelper(this)

        val usernameInput = findViewById<EditText>(R.id.username)
        val passwordInput = findViewById<EditText>(R.id.password)
        val signupButton = findViewById<Button>(R.id.signupButton)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        signupButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            when {
                username.isEmpty() || password.isEmpty() -> {
                    Toast.makeText(this, "Username & Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                password.length < 6 -> {
                    Toast.makeText(this, "Password harus minimal 6 karakter", Toast.LENGTH_SHORT).show()
                }
                sqliteHelper.isUserExists(username) -> { // Perbaikan fungsi yang digunakan
                    Toast.makeText(this, "Username sudah digunakan", Toast.LENGTH_SHORT).show()
                }
                sqliteHelper.addUser(username, password) -> {
                    Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                else -> {
                    Toast.makeText(this, "Registrasi Gagal, coba lagi", Toast.LENGTH_SHORT).show()
                }
            }
        }


        tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
