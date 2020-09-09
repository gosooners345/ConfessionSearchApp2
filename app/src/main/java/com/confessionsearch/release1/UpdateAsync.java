package com.confessionsearch.release1;

import android.os.AsyncTask;

public class UpdateAsync extends AsyncTask<Notes, Void, Void> {

    private NoteDao mNoteDao;

    public UpdateAsync(NoteDao dao) {
        mNoteDao = dao;
    }

    @Override
    protected Void doInBackground(Notes... notes) {
        mNoteDao.updateNotes(notes);
        return null;
    }
}
