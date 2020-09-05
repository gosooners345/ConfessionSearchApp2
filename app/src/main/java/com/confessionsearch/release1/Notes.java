package com.confessionsearch.release1;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;

public class Notes {
    private String name;
    private String content;

    public Notes(String newname, String newcontent) {
        name = newname;
        content = newcontent;
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

    private static int lastNoteID = 0;

    public static ArrayList<Notes> createNotesList(int entries) {
        ArrayList<Notes> notes = new ArrayList<Notes>();
        for (int i = 1; i < entries; i++) {
            notes.add(new Notes("subject:", "content:"));

        }
        return notes;
    }
    
}