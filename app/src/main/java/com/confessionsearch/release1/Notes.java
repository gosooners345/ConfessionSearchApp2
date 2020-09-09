package com.confessionsearch.release1;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;


import java.util.Objects;

import androidx.annotation.NonNull;


@Entity(tableName = "notes")
public class Notes implements Parcelable {

@ColumnInfo(name = "title")
    private String name;

    @PrimaryKey(autoGenerate = true)
    public int noteID;

    @ColumnInfo(name = "content")
    private String content;


public Notes() {}

    public Notes(String newname, String newcontent,int noteID) {
        name = newname;
        content = newcontent;
        this.noteID=noteID;
    }

    protected Notes(Parcel in) {
        name = in.readString();
        noteID = in.readInt();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(noteID);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Notes> CREATOR = new Creator<Notes>() {
        @Override
        public Notes createFromParcel(Parcel in) {
            return new Notes(in);
        }

        @Override
        public Notes[] newArray(int size) {
            return new Notes[size];
        }
    };

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @NotNull
    @Override
    public String toString() {
        return "Notes{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notes)) return false;
        Notes notes = (Notes) o;
        return name.equals(notes.name) &&
                content.equals(notes.content);
    }


}