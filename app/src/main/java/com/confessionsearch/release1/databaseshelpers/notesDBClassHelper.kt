@file:Suppress("ClassName")

package com.confessionsearch.release1.databaseshelpers

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.confessionsearch.release1.data.notes.NoteDao
import com.confessionsearch.release1.data.notes.Notes

@Database(
    entities = [Notes::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(from = 2, to = 3)]
)
abstract class notesDBClassHelper : RoomDatabase() {
    abstract val noteDao: NoteDao?

    companion object {
        private const val DATABASE_NAME = "confessionsearchNotes_db"
        private var instance: notesDBClassHelper? = null

        @JvmStatic
        fun getInstance(context: Context): notesDBClassHelper? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    notesDBClassHelper::class.java,
                    DATABASE_NAME
                ).build()
                return instance
            }
            return instance
        }
    }
}