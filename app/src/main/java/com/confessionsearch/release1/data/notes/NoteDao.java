package com.confessionsearch.release1.data.notes;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    long[] insertNotes(Notes... notes);


    @Query("SELECT * FROM notes")
    LiveData<List<Notes>> fetchNotes();

    @Delete
    int delete(Notes... notes);

    @Update
    int updateNotes(Notes... notes);
}