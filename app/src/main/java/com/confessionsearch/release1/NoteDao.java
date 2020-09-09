package com.confessionsearch.release1;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

//Thanks to Mitch at SQLite Learning for providing code to help set up this interface here
@Dao
public interface NoteDao{

    @Insert
    long[] insertNotes(Notes... notes);
    @Query("SELECT * FROM notes")
    LiveData<List<Notes>> getNotes();
    @Delete
    int delete(Notes... notes);
    @Update
    int updateNotes(Notes... notes);
}
