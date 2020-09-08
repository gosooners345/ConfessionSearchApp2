package com.confessionsearch.release1;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {
//ArrayAdapter<Notes> arrayAdapter;
    static NotesAdapter  adapter;
static ArrayList<Notes> notesArrayList;
RecyclerView notesList;

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
        notesList = findViewById(R.id.notesListView);

        notesArrayList = Notes.createNotesList(10);
        adapter = new NotesAdapter(notesArrayList);
        notesList.setLayoutManager(new LinearLayoutManager(this));
        notesList.setItemAnimator(new DefaultItemAnimator());
        notesList.setAdapter(adapter);
notesList.setOnClickListener(listItemListener);
        Intent intent = getIntent();
        //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

         ExtendedFloatingActionButton fab = findViewById(R.id.newNote);

    }

    public void NewNote(View view)
    {
Intent intent = new Intent(getApplicationContext(),NotesComposeClass.class);
intent.putExtra("noteID", -1);
intent.putExtra("subject","");
intent.putExtra("Content","");
startActivity(intent);


    }
RecyclerView.OnClickListener listItemListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    Intent intent = new Intent(getApplicationContext(),NotesComposeClass.class);
        TextView idView= (TextView)view.findViewById(R.id.noteIDnum);
    int noteID = Integer.parseInt(idView.getText().toString());
    TextView titleView = view.findViewById(R.id.content_Title);
    String noteTitle = titleView.getText().toString();
TextView contentView = view.findViewById(R.id.content_text);
String noteContent = contentView.getText().toString();
    intent.putExtra("noteID",noteID);
    intent.putExtra("subject", noteTitle);
    intent.putExtra("Content",noteContent);
    startActivity(intent);


    }
};


}