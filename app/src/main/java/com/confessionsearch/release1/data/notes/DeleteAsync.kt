package com.confessionsearch.release1.data.notes

import android.os.AsyncTask

class DeleteAsync(private val mNoteDao: NoteDao) : AsyncTask<Notes?, Void?, Void?>() {
    override fun doInBackground(vararg notes: Notes?): Void? {
        mNoteDao.delete(*notes)
        return null
    }
}