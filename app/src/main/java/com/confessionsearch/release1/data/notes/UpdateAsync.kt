package com.confessionsearch.release1.data.notes

import android.os.AsyncTask

class UpdateAsync(private val mNotesDao: NoteDao) : AsyncTask<Notes?, Void?, Void?>() {
    override fun doInBackground(vararg notes: Notes?): Void? {
        mNotesDao.updateNotes(*notes)
        return null
    }
}