package com.confessionsearch.release1.data.notes;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.confessionsearch.release1.databaseshelpers.notesDBClassHelper;

import java.util.List;

public class NoteRepository {

    private final notesDBClassHelper notesDB;

    public NoteRepository(Context context)
    {
        notesDB= notesDBClassHelper.getInstance(context);
    }
    public void insertNote(Notes note){
new InsertAsync(notesDB.getNoteDao()).execute(note);
    }
    public void updateNote(Notes note){
        new UpdateAsync(notesDB.getNoteDao()).execute(note);
    }

    public void deleteNote(Notes note){
        new DeleteAsync(notesDB.getNoteDao()).execute(note);
    }


    public LiveData<List<Notes>>fetchNotes(){
        return notesDB.getNoteDao().fetchNotes();
    }
}
