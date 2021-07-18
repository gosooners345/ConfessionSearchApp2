package com.confessionsearch.release1.data.notes;

import android.os.AsyncTask;

public class InsertAsync extends AsyncTask<Notes, Void, Void> {

    private final NoteDao mNoteDao;

    public InsertAsync(NoteDao dao) {
        mNoteDao = dao;
    }

    @Override
    protected Void doInBackground(Notes... notes) {
        mNoteDao.insertNotes(notes);
        return null;
    }}