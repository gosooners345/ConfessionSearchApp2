package com.confessionsearch.release1.data.notes

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {
    @Insert
    fun insertNotes(vararg notes: Notes?): LongArray?

    @Query("SELECT * FROM notes")
    fun fetchNotes(): LiveData<List<Notes>>

    @Delete
    fun delete(vararg notes: Notes?): Int

    @Update
    fun updateNotes(vararg notes: Notes?): Int
}