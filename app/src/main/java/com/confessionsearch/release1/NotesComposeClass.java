package com.confessionsearch.release1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NotesComposeClass extends AppCompatActivity {
EditText notesSubject, notesContent;
int noteID;
Notes newNote;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if(MainActivity.themeID==R.style.LightMode)
            setTheme(R.style.LightMode);
        if(MainActivity.themeID==R.style.DarkMode)
            setTheme(R.style.DarkMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_compose_layout);
        notesContent = findViewById(R.id.contentEditText);
        notesSubject = findViewById(R.id.subjectTitleEditText);
Intent intent = getIntent();
noteID = intent.getIntExtra("noteID",-1);



    }

    public void SaveNote(View view){
      //  newNote.setName();

    }
}