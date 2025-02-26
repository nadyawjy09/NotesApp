package com.example.tugaslayout

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import  android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import  androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private var notes: List<Note>,context: Context) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {


    private val db:DatabaseHelper=DatabaseHelper(context)


    class NoteViewHolder(ItemView:View):RecyclerView.ViewHolder(ItemView){
        val titleTextView:TextView=ItemView.findViewById(R.id.titleTextView)
        val contentTextView:TextView=ItemView.findViewById(R.id.contentTextView)
        val updateButton:ImageView=ItemView.findViewById(R.id.updateButton)
        val deleteButton:ImageView=ItemView.findViewById(R.id.deleteButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item,parent,false)
        return  NoteViewHolder(view)
    }

    override fun getItemCount(): Int =notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note =notes[position]
        holder.titleTextView.text=note.title
        holder.contentTextView.text=note.content

        holder.updateButton.setOnClickListener{
            val intent = Intent(holder.itemView.context,UpdateNote::class.java).apply {
                putExtra("note_id",note.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener{
            db.deleteNote(note.id)
            refreshData(db.getAllNotes())
            Toast.makeText(holder.itemView.context,"Note Deleted",Toast.LENGTH_SHORT).show()
        }

    }

    fun refreshData(newNotes: List<Note>){
        notes = newNotes
        notifyDataSetChanged()
    }
}