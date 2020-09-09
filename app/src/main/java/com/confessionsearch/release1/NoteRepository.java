package com.confessionsearch.release1;

import android.content.Context;
import androidx.lifecycle.LiveData;
import java.util.List;

public class NoteRepository {

    private notesDBClassHelper notesDB;

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
        return notesDB.getNoteDao().getNotes();
    }
}
