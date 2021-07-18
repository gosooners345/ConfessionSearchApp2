package com.confessionsearch.release1.ui.notesActivity

import androidx.lifecycle.ViewModel
import com.confessionsearch.release1.MainActivity
import com.confessionsearch.release1.data.notes.NoteRepository
import com.confessionsearch.release1.data.notes.Notes

class NotesViewModel : ViewModel() {
    var noteRepository: NoteRepository? = null

    //Delete notes from database
    fun deleteNote(note: Notes) {
        MainActivity.notesArrayList.remove(note)
        noteRepository!!.deleteNote(note)
        NotesFragment.adapter!!.notifyDataSetChanged()
    }
}