package com.confessionsearch.release1.data.notes

import android.content.Context
import androidx.lifecycle.LiveData
import com.confessionsearch.release1.databaseshelpers.notesDBClassHelper
import com.confessionsearch.release1.databaseshelpers.notesDBClassHelper.Companion.getInstance

class NoteRepository(context: Context?) {
    private val notesDB: notesDBClassHelper? = getInstance(context!!)
    fun insertNote(note: Notes?) {
        InsertAsync(notesDB!!.noteDao!!).execute(note)
    }

    fun updateNote(note: Notes?) {
        UpdateAsync(notesDB!!.noteDao!!).execute(note)
    }

    fun deleteNote(note: Notes?) {
        DeleteAsync(notesDB!!.noteDao!!).execute(note)
    }

    fun fetchNotes(): LiveData<List<Notes>> {
        return notesDB!!.noteDao!!.fetchNotes()
    }

}