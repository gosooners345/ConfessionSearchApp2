package com.confessionsearch.release1

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
class Notes : Parcelable, Cloneable {
    @ColumnInfo(name = "title")
    var name: String? = null

    @JvmField
    @PrimaryKey(autoGenerate = true)
    var noteID = 0

    @ColumnInfo(name = "content")
    var content: String? = null

    constructor()
    constructor(newname: String?, newcontent: String?, noteID: Int) {
        name = newname
        content = newcontent
        this.noteID = noteID
    }

    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        noteID = `in`.readInt()
        content = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeInt(noteID)
        dest.writeString(content)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Notes{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}'
    }

    @Throws(CloneNotSupportedException::class)
    override fun clone(): Any {
        return super.clone()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Notes) return false
        val notes = o
        return name == notes.name && content == notes.content
    }


    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Notes?> = object : Parcelable.Creator<Notes?> {
            override fun createFromParcel(`in`: Parcel): Notes? {
                return Notes(`in`)
            }

            override fun newArray(size: Int): Array<Notes?> {
                return arrayOfNulls(size)
            }
        }
    }
}