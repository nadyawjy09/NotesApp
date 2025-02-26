package com.example.tugaslayout

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 3
        private const val TABLE_NAME = "data"
        private const val TABLE_NAME2 = "note"
        private const val TABLE_NAME3 = "book"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private  const val COLUMN_NAMA ="nama"
        private  const val COLUMN_NAMA_PANGGILAN ="namapanggilan"
        private  const val COLUMN_E_MAIL="email"
        private  const val COLUMN_ALAMAT ="alamat"
        private  const val COLUMN_TGL_LAHIR ="tgllahir"
        private  const val COLUMN_NO_HP ="nohp"
        private  const val COLUMN_IMG_BOOK ="imgbook"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Buat tabel pertama
        val createTable1 = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT,
                $COLUMN_PASSWORD TEXT
            )
        """.trimIndent()

        // Buat tabel kedua
        val createTable2 = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME2 (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT,
                $COLUMN_CONTENT TEXT
            )
        """.trimIndent()


        val createTable3 = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME3 (
             $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAMA TEXT,
                $COLUMN_NAMA_PANGGILAN TEXT,
                $COLUMN_E_MAIL TEXT,
                $COLUMN_ALAMAT TEXT,
                $COLUMN_TGL_LAHIR TEXT,
                $COLUMN_NO_HP TEXT,
                $COLUMN_IMG_BOOK BLOB
            )
        """.trimIndent()

        try {
            db.execSQL(createTable1)
            db.execSQL(createTable2)
            db.execSQL(createTable3)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop tabel jika ada
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME2")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME3")
        // Buat ulang tabel
        onCreate(db)
    }







    ///BOOK


    fun getAllBooks(): List<Book> {
        val bookList = mutableListOf<Book>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, nama, email, nohp, imgbook FROM book", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nama = cursor.getString(cursor.getColumnIndexOrThrow("nama"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val noHp = cursor.getString(cursor.getColumnIndexOrThrow("nohp"))

                val imgBitmap = cursor.getBlob(cursor.getColumnIndexOrThrow("imgbook"))?.let {
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                } // Menghindari error jika NULL

                // Sesuaikan dengan struktur `Book`
                bookList.add(Book(id, nama, "", email, "", "", noHp, imgBitmap))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return bookList
    }


    fun insertBook(book: Book): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA, book.nama)
            put(COLUMN_NAMA_PANGGILAN, book.namaPanggilan)
            put(COLUMN_E_MAIL, book.email)
            put(COLUMN_ALAMAT, book.alamat)
            put(COLUMN_TGL_LAHIR, book.tglLahir)
            put(COLUMN_NO_HP, book.noHp)
            if (book.imgBook != null) {
                val stream = ByteArrayOutputStream()
                book.imgBook.compress(Bitmap.CompressFormat.PNG, 100, stream)
                put(COLUMN_IMG_BOOK, stream.toByteArray())
            }
        }
        val success = db.insert(TABLE_NAME3, null, values)
        db.close()
        return success != -1L
    }


    fun updateBook(book: Book): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA, book.nama)
            put(COLUMN_NAMA_PANGGILAN, book.namaPanggilan)
            put(COLUMN_E_MAIL, book.email)
            put(COLUMN_ALAMAT, book.alamat)
            put(COLUMN_TGL_LAHIR, book.tglLahir)
            put(COLUMN_NO_HP, book.noHp)
            if (book.imgBook != null) {
                val stream = ByteArrayOutputStream()
                book.imgBook.compress(Bitmap.CompressFormat.PNG, 100, stream)
                put(COLUMN_IMG_BOOK, stream.toByteArray())
            }
        }

        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(book.id.toString())
        val success = db.update(TABLE_NAME3, values, whereClause, whereArgs)
        db.close()
        return success > 0
    }



    fun getBookById(id: Int): Book? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM book WHERE id = ?", arrayOf(id.toString()))

        return if (cursor.moveToFirst()) {
            val nama = cursor.getString(cursor.getColumnIndexOrThrow("nama"))
            val namaPanggilan = cursor.getString(cursor.getColumnIndexOrThrow("namapanggilan"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val alamat = cursor.getString(cursor.getColumnIndexOrThrow("alamat"))
            val tglLahir = cursor.getString(cursor.getColumnIndexOrThrow("tgllahir"))
            val noHp = cursor.getString(cursor.getColumnIndexOrThrow("nohp"))

            val imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow("imgbook"))
            val imageBitmap = if (imageBlob != null) {
                BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.size)
            } else {
                null
            }

            cursor.close()
            Book(id, nama, namaPanggilan, email, alamat, tglLahir, noHp, imageBitmap)
        } else {
            cursor.close()
            null
        }
    }









    fun deleteBook(id: Int): Boolean {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(id.toString())
        val success = db.delete(TABLE_NAME3, whereClause, whereArgs)
        db.close()
        return success > 0
    }



    fun getBookByID(bookId: Int): Book? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME3 WHERE $COLUMN_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(bookId.toString()))

        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA))
            val namaPanggilan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_PANGGILAN))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_E_MAIL))
            val alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT))
            val tglLahir = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TGL_LAHIR))
            val noHp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NO_HP))
            val imgBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMG_BOOK))
            val imgBook = imgBlob?.let { decodeBlobToBitmap(it) }

            cursor.close()
            db.close()
            Book(id, nama, namaPanggilan, email, alamat, tglLahir, noHp, imgBook)
        } else {
            cursor.close()
            db.close()
            null
        }
    }



































    // Fungsi untuk mengecek apakah tabel sudah terbuat
    fun checkTables() {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        println("Checking existing tables:")
        if (cursor.moveToFirst()) {
            do {
                val tableName = cursor.getString(0)
                println("Table found: $tableName")
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    fun insertNote(note:Note){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_TITLE,note.title)
            put(COLUMN_CONTENT,note.content)
        }
        db.insert(TABLE_NAME2,null,values)
        db.close()
    }

    fun getAllNotes(): ArrayList<Note> {
        val notesList = ArrayList<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME2"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))

                val note = Note(id, title, content)
                notesList.add(note)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return notesList
    }

    fun updateNote(note:Note){
        val db =writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE,note.title)
            put(COLUMN_CONTENT,note.content)
        }
        val whereClause ="$COLUMN_ID = ?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NAME2,values,whereClause,whereArgs)
        db.close()
    }


    fun getNoteByID(noteId: Int): Note{
        val db =readableDatabase
        val query ="SELECT * FROM $TABLE_NAME2 WHERE $COLUMN_ID = $noteId"
        val cursor=db.rawQuery(query,null)
        cursor.moveToFirst()

        val id =cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title =cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content =cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))


        cursor.close()
        db.close()
        return Note(id,title,content)
    }

    fun deleteNote(noteId: Int){
        val db  =writableDatabase
        val whereClause ="$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TABLE_NAME2,whereClause,whereArgs)
        db.close()
    }
    fun deleteAllUsers(): Boolean {
        val db = writableDatabase
        val deletedRows = db.delete(TABLE_NAME, null, null)
        db.close()
        return deletedRows > 0
    }

    fun insertUser(username: String, password: String): Long {
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }
        val db = writableDatabase
        return db.insert(TABLE_NAME, null, values)
    }

    fun readUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    fun isUserExist(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count > 0
    }

    fun isUsernameExist(username: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
    fun deleteUserById(id: Int): Int {
        val db = writableDatabase
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())

        val deletedRows = db.delete(TABLE_NAME, selection, selectionArgs)
        db.close()
        return deletedRows
    }
    // Fungsi untuk menambahkan pengguna baru
    fun addUser(username: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result != -1L // Mengembalikan true jika insert berhasil
    }

    // Fungsi untuk mengecek apakah username sudah terdaftar
    fun isUserExists(username: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun insertImage(name: String, uri: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("uri", uri)
        }
        db.insert("Images", null, values)
        db.close()
    }



}