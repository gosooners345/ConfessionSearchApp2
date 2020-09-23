package com.confessionsearch.release1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class NotesComposeActivity extends AppCompatActivity {
    EditText notesSubject, notesContent;
    int activityID;
    public static final String TAG= "NotesComposeActivity";
    ExtendedFloatingActionButton saveButton, editButton;
    Boolean isNewNote = false;
    String noteContentString = "", noteSubjectString = "";
    String shareList = "";
    Notes newNote, incomingNote;
    NoteRepository noteRepository;
    int mode;
    private static final int EDIT_ON = 1;
    private static final int EDIT_OFF = 0;

//Custom Editor Test Variables
    //Editor notesContentRichText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (MainActivity.themeID == R.style.LightMode) {
            setTheme(R.style.LightMode);

        }
        if (MainActivity.themeID == R.style.DarkMode) {
            setTheme(R.style.DarkMode);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_compose_layout);

        notesContent = findViewById(R.id.contentEditText);
        notesSubject = findViewById(R.id.subjectTitleEditText);
        noteRepository = new NoteRepository(this);
//Load Notes
        if (!getIntentInfo()) {
            notesSubject.setText(newNote.getName());
            notesContent.setText((newNote.getContent()));


        } else {
            notesSubject.setText("");
            notesContent.setText("");


        }
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
        saveButton = findViewById(R.id.saveNote);
        saveButton.setOnClickListener(SaveNote);
        editButton = findViewById(R.id.editButton);
        activityID = getIntent().getIntExtra("activity_ID", -1);
        editButton.setOnClickListener(editNote);
    }

    //Enable or disable Editing
ExtendedFloatingActionButton.OnClickListener editNote= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (mode) { case EDIT_OFF: EnableEdit();break;
                case EDIT_ON:DisableEdit();break;}
        }
};
    //Save Note to device
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

            { //Update or insert new note into database
                if (isNewNote) {
                    noteRepository.insertNote(newNote);
                } else
                    noteRepository.updateNote(newNote);
                if (activityID == 32)
                    NotesActivity.adapter.notifyDataSetChanged();
                Log.i(TAG,"Saving note to storage");
                Snackbar.make(findViewById(R.id.masterLayout), "Note Saved", BaseTransientBottomBar.LENGTH_LONG).show();
            }
            //Close this activity out and head back to parent screen
            finish();
        }
    };


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
            String contentString = getIntent().getStringExtra("search_result_save");
            newNote = new Notes();
            newNote.setContent(contentString);
            mode = EDIT_ON;
            isNewNote = true;
            return false;
        }
        mode = EDIT_ON;
        newNote = new Notes();
        isNewNote = true;
        return true;
    }

    //back button
    @Override
    public void onBackPressed() {
        if (mode == EDIT_OFF) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Save your work?");
            alert.setMessage(String.format(getResources().getString(R.string.save_note_message)));
            alert.setPositiveButton(getResources().getString(R.string.save_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    saveButton.performClick();
                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            alert.setCancelable(true);
            alert.setNeutralButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            Dialog dialog = alert.create();
            if (!isFinishing())
                dialog.show();
        } else
            DisableEdit();
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