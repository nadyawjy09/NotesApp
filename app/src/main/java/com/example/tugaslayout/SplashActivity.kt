package com.example.tugaslayout

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Pastikan layout ini ada

        val dbHelper = DatabaseHelper(this)

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                if (!dbHelper.isUserExist()) {
                    // Jika user belum ada, registrasi dulu lalu langsung ke login
                    startActivity(Intent(this, SignupActivity::class.java))
                } else {
                    // Jika user sudah ada, langsung ke login tanpa registrasi
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }, 1000) // Delay 1 detik
    }
}
