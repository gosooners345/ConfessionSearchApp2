package com.confessionsearch.release1

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class NotesComposeActivity : AppCompatActivity() {
    var notesSubject: EditText? = null
    var notesContent: EditText? = null
    var activityID = 0
    var saveButton: ExtendedFloatingActionButton? = null
    var editButton: ExtendedFloatingActionButton? = null
    var isNewNote = false
    var noteContentString = ""
    var noteSubjectString = ""
    var shareList = ""
    var newNote: Notes? = null
    var incomingNote: Notes? = null
    var noteRepository: NoteRepository? = null
    var mode = 0

    //Custom Editor Test Variables
    //Editor notesContentRichText;
    override fun onCreate(savedInstanceState: Bundle?) {
        if (MainActivity.themeID == R.style.LightMode) {
            setTheme(R.style.LightMode)
        }
        if (MainActivity.themeID == R.style.DarkMode) {
            setTheme(R.style.DarkMode)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notes_compose_layout)
        notesContent = findViewById(R.id.contentEditText)
        notesSubject = findViewById(R.id.subjectTitleEditText)
        noteRepository = NoteRepository(this)
        //Load Notes
        if (!intentInfo) {
            notesSubject!!.setText(newNote!!.name)
            notesContent!!.setText(newNote!!.content)
        } else {
            notesSubject!!.setText("")
            notesContent!!.setText("")
        }
        notesSubject!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {
                noteSubjectString = s.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        notesContent!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                noteContentString = s.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        saveButton = findViewById(R.id.saveNote)
        saveButton!!.setOnClickListener(SaveNote)
        editButton = findViewById(R.id.editButton)
        activityID = intent.getIntExtra("activity_ID", -1)
        editButton!!.setOnClickListener(editNote)
    }

    //Enable or disable Editing
    var editNote = View.OnClickListener {
        when (mode) {
            EDIT_OFF -> EnableEdit()
            EDIT_ON -> DisableEdit()
        }
    }

    //Save Note to device
    var SaveNote: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(view: View) {
            noteSubjectString = notesSubject!!.text.toString()
            noteContentString = notesContent!!.text.toString()
            //Update or save new content to note
            newNote = if (!isNewNote) Notes(noteSubjectString, noteContentString, incomingNote!!.getNoteID()) else Notes()
            newNote!!.name = noteSubjectString
            newNote!!.content = noteContentString
            run {
                //Update or insert new note into database
                if (isNewNote) {
                    noteRepository!!.insertNote(newNote)
                } else noteRepository!!.updateNote(newNote)
                if (activityID == 32) NotesActivity.adapter!!.notifyDataSetChanged()
                Log.i(TAG, "Saving note to storage")
                Snackbar.make(findViewById(R.id.masterLayout), "Note Saved", BaseTransientBottomBar.LENGTH_LONG).show()
            }
            //Close this activity out and head back to parent screen
            finish()
        }
    }

    //find out if the note is new or old
    private val intentInfo: Boolean
        private get() {
            if (intent.hasExtra("note_selected")) {
                incomingNote = intent.getParcelableExtra("note_selected")
                newNote = Notes()
                newNote!!.setNoteID(incomingNote!!.getNoteID())
                newNote!!.content = incomingNote!!.content
                newNote!!.name = incomingNote!!.name
                mode = EDIT_ON
                isNewNote = false
                return false
            } else if (intent.hasExtra("search_result_save")) {
                val contentString = intent.getStringExtra("search_result_save")
                newNote = Notes()
                newNote!!.content = contentString
                mode = EDIT_ON
                isNewNote = true
                return false
            }
            mode = EDIT_ON
            newNote = Notes()
            isNewNote = true
            return true
        }

    //back button
    override fun onBackPressed() {
        if (mode == EDIT_OFF) {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Save your work?")
            alert.setMessage(String.format(resources.getString(R.string.save_note_message)))
            alert.setPositiveButton(resources.getString(R.string.save_button_text)) { dialog, which ->
                dialog.dismiss()
                saveButton!!.performClick()
            }
            alert.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
                finish()
            }
            alert.setCancelable(true)
            alert.setNeutralButton(R.string.cancel_button) { dialogInterface, i -> dialogInterface.dismiss() }
            val dialog: Dialog = alert.create()
            if (!isFinishing) dialog.show()
        } else DisableEdit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("mode", mode)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mode = savedInstanceState.getInt("mode")
        if (mode == EDIT_ON) {
            Snackbar.make(findViewById(R.id.masterLayout), "Resume Writing", BaseTransientBottomBar.LENGTH_SHORT).show()
        }
    }

    private fun DisableEdit() {
        notesContent!!.isEnabled = false
        notesSubject!!.isEnabled = false
        mode = EDIT_OFF
        Snackbar.make(findViewById(R.id.masterLayout), "Save Note", BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun EnableEdit() {
        notesContent!!.isEnabled = true
        notesSubject!!.isEnabled = true
        mode = EDIT_ON
        Snackbar.make(findViewById(R.id.masterLayout), "Resume Writing", BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.share_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareAction -> {
                shareList = """
                ${notesSubject!!.text}
                ${notesContent!!.text}
                """.trimIndent()
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                val INTENTNAME = "SHARE"
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareList)
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, INTENTNAME))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val TAG = "NotesComposeActivity"
        private const val EDIT_ON = 1
        private const val EDIT_OFF = 0
    }
}