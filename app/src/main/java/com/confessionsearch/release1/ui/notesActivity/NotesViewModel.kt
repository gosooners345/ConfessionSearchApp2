package com.confessionsearch.release1.ui.notesActivity

import androidx.lifecycle.ViewModel
import com.confessionsearch.release1.data.notes.NoteRepository
import com.confessionsearch.release1.data.notes.Notes
import java.util.*

class NotesViewModel : ViewModel() {
    var noteRepository: NoteRepository? = null

    //Delete notes from database
    fun deleteNote(note: Notes) {
        val notePosition = NotesFragment.notesArrayList.indexOf(note)
        NotesFragment.notesArrayList.remove(note)
        noteRepository!!.deleteNote(note)
        NotesFragment.adapter!!.notifyItemRemoved(notePosition)
        Collections.sort(NotesFragment.notesArrayList, Notes.compareDateTime)
    }
}