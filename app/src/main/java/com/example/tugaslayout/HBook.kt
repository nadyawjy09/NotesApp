package com.example.tugaslayout

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tugaslayout.databinding.ActivityHbookBinding


class HBook : AppCompatActivity() {

    private lateinit var binding: ActivityHbookBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var bookAdapter: BookAdapter
    private var bookList: MutableList<Book> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHbookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        loadBooks()

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddBook::class.java)
            startActivity(intent)
        }

        binding.About.setOnClickListener {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }

        binding.btnExit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

    }

    private fun loadBooks() {
        bookList = dbHelper.getAllBooks().toMutableList()
        bookAdapter = BookAdapter(bookList, ::editBook, ::deleteBook)
        binding.bookRecyclerView.adapter = bookAdapter
    }

    private fun editBook(book: Book) {
        val intent = Intent(this, AddBook::class.java)
        intent.putExtra("BOOK_ID", book.id)
        startActivity(intent)
    }




    private fun deleteBook(id: Int) {
        if (dbHelper.deleteBook(id)) {
            Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show()
            loadBooks()
        } else {
            Toast.makeText(this, "Gagal menghapus", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadBooks()
    }


}