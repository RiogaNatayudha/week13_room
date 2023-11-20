package com.example.week13_room.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert (notes: Notes)

    @Update
    fun update(notes: Notes)

    @Delete
    fun delete(notes: Notes)

    @get:Query("SELECT * from note_table ORDER BY id ASC")
    val  allNotes: LiveData<List<Notes>>

    @Query("SELECT * FROM note_table WHERE id = :id")
    fun getNoteById(id: Int): Notes?

}