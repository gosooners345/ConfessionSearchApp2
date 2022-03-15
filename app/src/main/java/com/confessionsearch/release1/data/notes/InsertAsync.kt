package com.confessionsearch.release1.data.notes


class InsertAsync(private val mNotesDao: NoteDao) :
    CoroutinesAsyncTask<Notes?, Void?, Void?>("insert")
{
    override fun doInBackground(vararg notes: Notes?): Void?
    {
        mNotesDao.insertNotes(*notes)
        return null
    }
}