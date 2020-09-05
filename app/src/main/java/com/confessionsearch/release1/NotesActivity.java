package com.confessionsearch.release1;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {
//ArrayAdapter<Notes> arrayAdapter;
ArrayList<Notes> notesArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(MainActivity.themeID==R.style.LightMode)
            setTheme(R.style.LightMode_NoActionBar);
        if(MainActivity.themeID==R.style.DarkMode)
            setTheme(R.style.DarkMode_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView notesList = findViewById(R.id.notesListView);
notesArrayList = Notes.createNotesList(10);
NotesAdapter adapter = new NotesAdapter(notesArrayList);
notesList.setAdapter(adapter);
notesList.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

         ExtendedFloatingActionButton fab = findViewById(R.id.newNote);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    public void NewNote(){

    }

}