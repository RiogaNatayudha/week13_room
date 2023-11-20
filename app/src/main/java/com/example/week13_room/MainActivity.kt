package com.example.week13_room

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.ListView
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.week13_room.database.NoteDao
import com.example.week13_room.database.NoteRoomDatabase
import com.example.week13_room.database.Notes
import com.example.week13_room.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService
    private var updateId: Int = 0
    private val ADD_DATA_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!

        with(binding) {
            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val item = listView.adapter.getItem(position) as Notes
                updateId = item.id
                val intent = Intent(this@MainActivity, TambahData::class.java)
                intent.putExtra("updateId", updateId)
                startActivityForResult(intent, ADD_DATA_REQUEST_CODE)
            }

            listView.onItemLongClickListener =
                AdapterView.OnItemLongClickListener { adapterView, _, position, _ ->
                    val item = adapterView.adapter.getItem(position) as Notes
                    delete(item)
                    true
                }

            btnGoToMain.setOnClickListener {
                val intent = Intent(this@MainActivity, TambahData::class.java)
                startActivityForResult(intent, ADD_DATA_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_DATA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val title = data?.getStringExtra("title")
            val desc = data?.getStringExtra("desc")
            val date = data?.getStringExtra("date")

            val updatedId = data?.getIntExtra("updateId", -1)
            if (updatedId != null && updatedId != -1) {
                // Logika update dengan menggunakan updatedId
                if (title != null && desc != null && date != null) {
                    update(Notes(id = updatedId, title = title, description = desc, date = date))
                    getNotes()
                }
            } else {
                if (title != null && desc != null && date != null) {
                    insert(Notes(title = title, description = desc, date = date))
                    getNotes()
                }
            }
        }
    }

    private fun getNotes() {
        mNotesDao.allNotes.observe(this) { notes ->
            val adapter: ArrayAdapter<Notes> = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1, notes
            )
            binding.listView.adapter = adapter
        }
    }

    private fun insert(note: Notes) {
        executorService.execute { mNotesDao.insert(note) }
    }

    private fun update(note: Notes) {
        executorService.execute { mNotesDao.update(note) }
    }

    private fun delete(note: Notes) {
        executorService.execute { mNotesDao.delete(note) }
    }

    override fun onResume() {
        super.onResume()
        getNotes()
    }
}

//adapter ini nampilin isi notes

