package com.confessionsearch.release1.data.notes

class UpdateAsync(private val mNotesDao: NoteDao) :
  CoroutinesAsyncTask<Notes?, Void?, Void?>("update")
{
  override fun doInBackground(vararg notes: Notes?): Void?
  {
    mNotesDao.updateNotes(*notes)
    return null
  }
}