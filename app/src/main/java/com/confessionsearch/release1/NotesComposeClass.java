package com.confessionsearch.release1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class NotesComposeClass extends AppCompatActivity {
EditText notesSubject, notesContent;
int noteID;
String noteContentString="", noteSubjectString="";
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
noteSubjectString = intent.getStringExtra("subject");
noteContentString = intent.getStringExtra("Content");

if (noteID!=-1)
{
    newNote=new Notes(noteSubjectString,noteContentString,noteID);
    notesSubject.setText(noteSubjectString);
    notesContent.setText(noteContentString);
}
else{
    noteID =NotesActivity.adapter.getItemCount()+1;
    newNote= new Notes();
    newNote.setNoteID(noteID);
    noteContentString="";
    noteSubjectString="";
}
        notesContent.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                noteContentString = String.valueOf(s);


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }


        });
notesSubject.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int after) {
noteSubjectString=String.valueOf(s);

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
});

    }

    public void SaveNote(View view){
      //  newNote.setName();
newNote.setName(noteSubjectString);
newNote.setContent(noteContentString);
        NotesActivity.notesArrayList.set(noteID,newNote);
        NotesActivity.adapter.notifyDataSetChanged();

    }


}