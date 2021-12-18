package com.confessionsearch.release1.data.notes

import android.os.AsyncTask

class InsertAsync(private val mNotesDao: NoteDao) : AsyncTask<Notes?, Void?, Void?>() {
    override fun doInBackground(vararg notes: Notes?): Void? {
        mNotesDao.insertNotes(*notes)
        return null
    }
}