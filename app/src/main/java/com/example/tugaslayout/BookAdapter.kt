package com.example.tugaslayout

import android.app.AlertDialog
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(
    private val books: MutableList<Book>,
    private val onEdit: (Book) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val imgBook: ImageView = view.findViewById(R.id.imgBook)
        val tvNoHp: TextView = itemView.findViewById(R.id.tvNoHp)
        val btnEdit = itemView.findViewById<ImageView>(R.id.btnEdit)
        val btnDelete = itemView.findViewById<ImageView>(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_item, parent, false) // Ubah jadi book_item
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

        // Set teks nama, email, dan no_hp
        holder.tvNama.text = book.nama
        holder.tvEmail.text = book.email
        holder.tvNoHp.text = book.noHp

        // Menampilkan gambar jika ada, jika tidak pakai gambar default
        book.imgBook?.let {
            holder.imgBook.setImageBitmap(it)
        } ?: holder.imgBook.setImageResource(R.drawable.ic_launcher_background) // Gambar default

        // Tombol edit
        holder.btnEdit.setOnClickListener { onEdit(book) }

        // Tombol delete dengan konfirmasi popup
        holder.btnDelete.setOnClickListener {
            val context = holder.itemView.context
            AlertDialog.Builder(context).apply {
                setTitle("Konfirmasi Hapus")
                setMessage("Apakah Anda yakin ingin menghapus ${book.nama} dari database?")
                setPositiveButton("Hapus") { _, _ ->
                    onDelete(book.id)
                }
                setNegativeButton("Tidak", null)
                show()
            }
        }
    }

    override fun getItemCount() = books.size
}
