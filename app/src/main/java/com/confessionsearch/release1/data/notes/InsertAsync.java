package com.confessionsearch.release1.data.notes;

import android.os.AsyncTask;

public class InsertAsync extends AsyncTask<Notes, Void, Void> {

    private final NoteDao mNotesDao;

    public InsertAsync(NoteDao dao) {
        mNotesDao = dao;
    }

    @Override
    protected Void doInBackground(Notes... notes) {
        mNotesDao.insertNotes(notes);
        return null;
    }}