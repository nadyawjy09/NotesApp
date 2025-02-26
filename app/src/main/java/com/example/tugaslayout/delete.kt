package com.example.tugaslayout

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class delete : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var edtIdToDelete: EditText
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_delete)

        dbHelper = DatabaseHelper(this)
        edtIdToDelete = findViewById(R.id.edtIdToDelete)
        btnDelete = findViewById(R.id.btnDelete)

        // Set listener for the delete button
        btnDelete.setOnClickListener {
            val idToDelete = edtIdToDelete.text.toString()

            if (idToDelete.isNotEmpty()) {
                val id = idToDelete.toInt()
                val rowsDeleted = dbHelper.deleteUserById(id)

                if (rowsDeleted > 0) {
                    Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No user found with this ID", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a valid ID", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
