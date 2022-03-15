package com.confessionsearch.release1.data.notes

class DeleteAsync(private val mNoteDao: NoteDao) :
    CoroutinesAsyncTask<Notes?, Void?, Void?>("delete")
{
    override fun doInBackground(vararg notes: Notes?): Void?
    {
        mNoteDao.delete(*notes)
        return null
    }
}