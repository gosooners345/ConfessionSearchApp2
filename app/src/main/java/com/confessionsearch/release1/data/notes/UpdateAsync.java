package com.confessionsearch.release1.data.notes;

import android.os.AsyncTask;

public class UpdateAsync extends AsyncTask<Notes, Void, Void> {

    private final NoteDao mNoteDao;

    public UpdateAsync(NoteDao dao) {
        mNoteDao = dao;
    }

    @Override
    protected Void doInBackground(Notes... notes) {
        mNoteDao.updateNotes(notes);
        return null;
    }
}
