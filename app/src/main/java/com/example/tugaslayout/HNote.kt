package com.example.tugaslayout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tugaslayout.databinding.NodeasBinding
import com.example.tugaslayout.AddNote

class HNote : AppCompatActivity() {
    private lateinit var binding: NodeasBinding
    private lateinit var db: DatabaseHelper
    private lateinit var notesAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NodeasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)
        notesAdapter = NoteAdapter(db.getAllNotes(), this)

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = notesAdapter

        db.checkTables() // Ini akan print nama tabel yang ada

        // Ambil dan tampilkan notes jika ada
        val notes = db.getAllNotes()
        if (notes.isNotEmpty()) {
            println("Found ${notes.size} notes")
        } else {
            println("No notes found")
        }

        // Fungsi untuk menambah note
        binding.addButton.setOnClickListener {
            val intent = Intent(this,AddNote::class.java)
            startActivity(intent)
        }

        // Fungsi keluar dan kembali ke MainActivity
        binding.btnExit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        notesAdapter.refreshData(db.getAllNotes())
    }
}
