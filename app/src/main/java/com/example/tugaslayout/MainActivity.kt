package com.example.tugaslayout

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tugaslayout.databinding.ActivityMainBinding
import com.example.tugaslayout.HNote
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var aboutMeBtn: Button? = null
    private var kalkulatorBtn: View? = null // Ubah ke View karena onClick di XML menggunakan LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        enableEdgeToEdge()

        // Inisialisasi tombol
        aboutMeBtn = findViewById(R.id.button_about_me)
        kalkulatorBtn = findViewById(R.id.kalkulator) // Sesuaikan dengan ID di XML

        // Menetapkan listener dengan safe call
        aboutMeBtn?.setOnClickListener { showAboutMeDialog() }
        kalkulatorBtn?.setOnClickListener { goToKalkulator() }

        // Menangani window insets (untuk padding)
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            v.setPadding(
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
            insets
        }
    }

    private fun exitApp(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { dialog, _ ->
                finish() // Menutup aplikasi
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss() // Menutup dialog, tidak keluar aplikasi
            }
            .show()
    }

    private fun goToKalkulator() {
        val intent = Intent(this, kalkulator::class.java) // Pastikan nama kelas benar
        startActivity(intent)
    }


    fun goToNotes (view:View) {
        val intent = Intent(this,HNote::class.java)
        startActivity(intent)
    }

    fun goToBook(view: View) {
        val intent = Intent(this,HBook::class.java)
        startActivity(intent)
    }

    private fun showAboutMeDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_about_me, null)
        builder.setView(dialogView)
            .setTitle("About Me")
            .setPositiveButton("Close") { dialog: DialogInterface, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
