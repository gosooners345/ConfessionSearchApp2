package com.confessionsearch.release1.databaseshelpers;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.confessionsearch.release1.data.notes.NoteDao;
import com.confessionsearch.release1.data.notes.Notes;


@Database(entities = {Notes.class}, version = 2, exportSchema = true,
        autoMigrations = {@AutoMigration(from = 1, to = 2)})

public abstract class notesDBClassHelper extends RoomDatabase {
    public static final String DATABASE_NAME = "confessionsearchNotes_db";
    private static notesDBClassHelper instance;

    public static notesDBClassHelper getInstance(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), notesDBClassHelper.class, DATABASE_NAME).build();
            return instance;
        }
        return instance;
    }


    public abstract NoteDao getNoteDao();
}