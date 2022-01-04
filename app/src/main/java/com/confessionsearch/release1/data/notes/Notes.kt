package com.confessionsearch.release1.data.notes

import android.icu.text.DateFormat
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notes")
class Notes() : Parcelable, Cloneable, Comparable<Notes> {


    @JvmField
    @PrimaryKey(autoGenerate = true)
    var noteID = 0

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "content")
    var content: String? = null

    @ColumnInfo(name = "tags")
    var noteTags: String? = null

    @ColumnInfo(name = "time_modified")
    var timeModified: Long? = System.currentTimeMillis()

    @RequiresApi(Build.VERSION_CODES.N)
    @ColumnInfo(name = "time_displayed")
    var time: String? = DateFormat.getInstance().format(timeModified)

    constructor(`in`: Parcel) : this() {
        noteID = `in`.readInt()
        title = `in`.readString()
        content = `in`.readString()
        noteTags = `in`.readString()
        time = `in`.readString()
        timeModified = `in`.readLong()
    }

    //Main constructor for Notes Class
    //Added Timestamp for sorting by date and updating.
    @RequiresApi(Build.VERSION_CODES.N)
    constructor(newTitle: String?, newContent: String?, noteID: Int, tags: String?) : this() {
        this.noteID = noteID
        title = newTitle
        content = newContent
        noteTags = tags
        this.timeModified = System.currentTimeMillis()
        this.time = DateFormat.getInstance().format(timeModified)
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeInt(noteID)
        dest.writeString(title)
        dest.writeString(content)
        dest.writeString(noteTags)
        if (timeModified != null) {
            dest.writeLong(timeModified!!)
            dest.writeString(time!!)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Notes{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", tags = '" + noteTags + '\'' +
                ",time updated '=" + time + '\'' +
                ",time modified '='" + timeModified + '\'' +
                '}'
    }

    override fun compareTo(other: Notes): Int {
        return noteID.compareTo(other.noteID)
    }

    @Throws(CloneNotSupportedException::class)
    override fun clone(): Any {
        return super.clone()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Notes) return false
        val notes = other
        return title == notes.title && content == notes.content //&& time == notes.time
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + noteID
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + (noteTags?.hashCode() ?: 0)
        result = 31 * result + (timeModified?.hashCode() ?: 0)
        result = 31 * result + (time?.hashCode() ?: 0)
        return result
    }


    companion object CREATOR : Parcelable.Creator<Notes?> {
        override fun createFromParcel(`in`: Parcel): Notes? {
            return Notes(`in`)
        }

        override fun newArray(size: Int): Array<Notes?> {
            return arrayOfNulls(size)
        }

        var compareDateTime = java.util.Comparator<Notes> { notes1, notes2 ->
            if (notes1.timeModified == null || notes2.timeModified == null) {
                notes1.compareTo(notes2)
            } else {
                if (notes1.timeModified!! == notes2.timeModified!!) notes1.compareTo(notes2)
                else if (notes1.timeModified!! < notes2.timeModified!!) 1
                else -1
            }
        }
        var compareIDs = java.util.Comparator<Notes> { notes1, notes2 ->
            notes1.noteID.compareTo(notes2.noteID)

        }
        var compareAlphabetized = java.util.Comparator<Notes> { notes1, notes2 ->
            if (notes1.title!!.lowercase(Locale.getDefault()).compareTo(
                    notes2.title!!.lowercase(
                        Locale.getDefault()
                    )
                ) == 0
            )
                notes1.compareTo(notes2)
            else
                notes1.title!!.lowercase(Locale.getDefault()).compareTo(
                    notes2.title!!.lowercase(
                        Locale.getDefault()
                    )
                )
        }
    }
}
