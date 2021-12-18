package com.confessionsearch.release1.data.notes;

import android.os.AsyncTask;

public class UpdateAsync extends AsyncTask<Notes, Void, Void> {

    private final NoteDao mNotesDao;

    public UpdateAsync(NoteDao dao) {
        mNotesDao = dao;
    }

    @Override
    protected Void doInBackground(Notes... notes) {
        mNotesDao.updateNotes(notes);
        return null;
    }
}