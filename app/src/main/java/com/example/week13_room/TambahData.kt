package com.example.week13_room

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.week13_room.database.NoteDao
import com.example.week13_room.database.NoteRoomDatabase
import com.example.week13_room.database.Notes
import com.example.week13_room.databinding.ListLayoutBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TambahData : AppCompatActivity() {
    private lateinit var binding: ListLayoutBinding
    private lateinit var mNotesDao: NoteDao  // Tambahkan ini
    private var updateId: Int = 0
    private lateinit var existingNote: Notes
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mNotesDao = NoteRoomDatabase.getDatabase(this)?.noteDao()!!

        // Mendapatkan updateId jika ada
        updateId = intent.getIntExtra("updateId", 0)

        if (updateId != 0) {
            // Jika updateId ada, berarti ini adalah mode update
            executorService.execute {
                existingNote = mNotesDao.getNoteById(updateId) ?: Notes() // Menggunakan default constructor jika null

                runOnUiThread {
                    binding.txtTitle.setText(existingNote.title)
                    binding.txtDesc.setText(existingNote.description)
                    binding.txtDate.setText(existingNote.date)

                    binding.btn.text = "Update Data"
                }
            }
        }

        binding.btn.setOnClickListener {
            val title = binding.txtTitle.text.toString()
            val desc = binding.txtDesc.text.toString()
            val date = binding.txtDate.text.toString()

            if (updateId != 0) {
                // Jika updateId tidak sama dengan 0, berarti ini mode update
                // Update data ke database
                val updatedNote = Notes(id = updateId, title = title, description = desc, date = date)
                updateData(updatedNote)
            } else {
                // Jika updateId sama dengan 0, berarti ini mode tambah data
                // Tambahkan data baru ke database
                tambahData(title, desc, date)
            }

            // Set result dan akhiri activity
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun tambahData(title: String, desc: String, date: String) {
        val resultIntent = Intent()
        resultIntent.putExtra("title", title)
        resultIntent.putExtra("desc", desc)
        resultIntent.putExtra("date", date)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()  // tambahkan finish() untuk menutup activity setelah menambahkan data
    }

    private fun updateData(updatedNote: Notes) {
        val resultIntent = Intent()
        resultIntent.putExtra("updateId", updatedNote.id)
        resultIntent.putExtra("title", updatedNote.title)
        resultIntent.putExtra("desc", updatedNote.description)
        resultIntent.putExtra("date", updatedNote.date)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()  // tambahkan finish() untuk menutup activity setelah mengupdate data
    }
}