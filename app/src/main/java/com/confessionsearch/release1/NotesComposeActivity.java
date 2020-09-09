package com.confessionsearch.release1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashSet;

public class NotesComposeActivity extends AppCompatActivity {
    EditText notesSubject, notesContent;
    int noteID;
    ExtendedFloatingActionButton saveButton,editButton, shareButton;
    Boolean isNewNote = false;
    String noteContentString = "", noteSubjectString = "";
    String shareList = "";
    Notes newNote, incomingNote;
    NoteRepository noteRepository;
    int mode;
    private static final int EDIT_ON = 1;
    private static final int EDIT_OFF = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (MainActivity.themeID == R.style.LightMode)
            setTheme(R.style.LightMode);
        if (MainActivity.themeID == R.style.DarkMode)
            setTheme(R.style.DarkMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_compose_layout);
        notesContent = findViewById(R.id.contentEditText);
        notesSubject = findViewById(R.id.subjectTitleEditText);
        Intent intent = getIntent();
        noteRepository = new NoteRepository(this);

        if (!getIntentInfo()) {
            notesSubject.setText(newNote.getName());
            notesContent.setText(newNote.getContent());
        } else {
            notesSubject.setText("");
            notesContent.setText("");

        }

//Text Changed Events
        notesContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                noteSubjectString = String.valueOf(s);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        saveButton = findViewById(R.id.saveNote);
        saveButton.setOnClickListener(SaveNote);
        editButton = findViewById(R.id.editButton);
        if(!intent.hasExtra("search_result_save"))
        editButton.setOnClickListener(editNote);
    }
ExtendedFloatingActionButton.OnClickListener editNote= new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        switch (mode)
        { case EDIT_OFF: EnableEdit();break;
            case EDIT_ON:DisableEdit();break;}
    }
};
    ExtendedFloatingActionButton.OnClickListener SaveNote = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            noteSubjectString = notesSubject.getText().toString();
            noteContentString = notesContent.getText().toString();
            //Update or save new content to note
            if (!isNewNote)
                newNote = new Notes(noteSubjectString, noteContentString, incomingNote.getNoteID());
            else
                newNote = new Notes();

            newNote.setName(noteSubjectString);
            newNote.setContent(noteContentString);

            if(getIntent().hasExtra("search_result_save"))
                MainActivity.notesArrayList.add(newNote);
            else { //Update or insert new note into database
                if (isNewNote || newNote.getNoteID() == 0)
                    noteRepository.insertNote(newNote);
                else
                    noteRepository.updateNote(newNote);


                NotesActivity.adapter.notifyDataSetChanged();


                Snackbar.make(findViewById(R.id.masterLayout), "Note Saved", BaseTransientBottomBar.LENGTH_LONG).show();
            }
            //Close this activity out and head back to parent screen
           finish();
        }
    };
//Save note button

    //find out if the note is new or old
    private Boolean getIntentInfo() {
        if (getIntent().hasExtra("note_selected")) {
            incomingNote = getIntent().getParcelableExtra("note_selected");
            newNote = new Notes();

            newNote.setNoteID(incomingNote.getNoteID());
            newNote.setContent(incomingNote.getContent());
            newNote.setName(incomingNote.getName());
            mode = EDIT_ON;
            isNewNote = false;
            return false;
        } else if (getIntent().hasExtra("search_result_save"))
        {
incomingNote = getIntent().getParcelableExtra("search_result_save");
newNote=new Notes();

            newNote.setNoteID(incomingNote.getNoteID());
            newNote.setContent(incomingNote.getContent());
            newNote.setName(incomingNote.getName());
            mode = EDIT_ON;
            isNewNote = true;
            return false;
        }
        mode = EDIT_ON;
        newNote = new Notes();
        isNewNote = true;
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode", mode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mode = savedInstanceState.getInt("mode");
        if (mode == EDIT_ON) {
            Snackbar.make(findViewById(R.id.masterLayout), "Resume Writing", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }

    private void DisableEdit() {
        notesContent.setEnabled(false);
        notesSubject.setEnabled(false);
        mode = EDIT_OFF;
        Snackbar.make(findViewById(R.id.masterLayout),"Save Note",BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    private void EnableEdit()
    {
        notesContent.setEnabled(true);
        notesSubject.setEnabled(true);
        mode=EDIT_ON;
        Snackbar.make(findViewById(R.id.masterLayout), "Resume Writing", BaseTransientBottomBar.LENGTH_SHORT).show();
    }


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
switch (item.getItemId())
{
    case R.id.shareAction:
        shareList = notesSubject.getText().toString() + "\r\n" + notesContent.getText().toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String INTENTNAME = "SHARE";
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareList);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, INTENTNAME));
break;
}
return super.onOptionsItemSelected(item);

    }
}