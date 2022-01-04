package com.confessionsearch.release1.ui.notes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.notes.NoteRepository
import com.confessionsearch.release1.data.notes.Notes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.text.DateFormat
import java.util.*

class NotesComposeActivity : AppCompatActivity() {
    var notesSubject: TextInputLayout? = null
    var notesContent: TextInputLayout? = null

    //Tags Addition
    lateinit var tagsContainer: TextInputLayout
    var activityID = 0
    private var saveButton: Button? = null
    private var editButton: Button? = null
    var isNewNote = false
    var noteContentString = ""
    var noteSubjectString = ""
    var tagsString = ""
    private var shareList = ""
    var newNote: Notes? = null
    var incomingNote: Notes? = null
    var noteRepository: NoteRepository? = null
    private var mode = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.notes_compose_layout)
        notesContent = findViewById(R.id.contentContainer)
        notesSubject = findViewById(R.id.topicContainer)
        tagsContainer = findViewById(R.id.tagsContainer)
        noteRepository = NoteRepository(this)

        //Load Notes
        if (!intentInfo) {
            notesSubject!!.editText!!.setText(newNote!!.title)
            notesContent!!.editText!!.setText(newNote!!.content)
            if (newNote!!.noteTags != null)
                tagsContainer.editText!!.setText(newNote!!.noteTags)
            else
                tagsContainer.editText!!.setText("")
        } else {
            notesSubject!!.editText!!.setText("")
            notesContent!!.editText!!.setText("")
            //Tags Addition
            tagsContainer.editText!!.setText("")
        }
        notesSubject!!.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {
                noteSubjectString = s.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        notesContent!!.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                noteContentString = s.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        tagsContainer.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {
                tagsString = s.toString()
            }

            override fun afterTextChanged(editable: Editable) {
                tagsString = editable.toString()
            }
        })


        saveButton = findViewById(R.id.saveNote)
        saveButton!!.setOnClickListener(saveNote)
        editButton = findViewById(R.id.editButton)
        activityID = intent.getIntExtra("activity_ID", -1)
        editButton!!.setOnClickListener(editNote)
    }

    //Enable or disable Editing
    private var editNote = View.OnClickListener {
        when (mode) {
            EDIT_OFF -> enableEdit()
            EDIT_ON -> disableEdit()
        }
    }

    //Save Note to device
    private var saveNote: View.OnClickListener = object : View.OnClickListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onClick(view: View) {
            noteSubjectString = notesSubject!!.editText!!.text.toString()
            noteContentString = notesContent!!.editText!!.text.toString()
            //Tags Addition
            tagsString = tagsContainer.editText!!.text.toString()
            //Update or save new content to note
            newNote = if (!isNewNote) Notes(
                noteSubjectString,
                noteContentString,
                incomingNote!!.noteID, tagsString
            )
            else Notes()
            newNote!!.title = noteSubjectString
            newNote!!.content = noteContentString
            newNote!!.timeModified = System.currentTimeMillis()
            newNote!!.time = DateFormat.getInstance().format(newNote!!.timeModified)
            newNote!!.noteTags = tagsString
            run {
                //Update or insert new note into database
                if (isNewNote) {
                    noteRepository!!.insertNote(newNote)
                } else noteRepository!!.updateNote(newNote)
                //If this came from the notes fragment as an update
                if (activityID == 32) {
                    Collections.sort(NotesFragment.notesArrayList, Notes.compareDateTime)
                    NotesFragment.notesArrayList.reverse()
                    NotesFragment.adapter!!.notifyDataSetChanged()
                }
                Log.i(TAG, "Saving note to storage")
            }
            //Close this activity out and head back to parent screen
            finish()
        }
    }

    //find out if the note is new or old
    private val intentInfo: Boolean
        get() {
            if (intent.hasExtra("note_selected")) {
                incomingNote = intent.getParcelableExtra("note_selected")
                newNote = Notes()
                newNote!!.noteID = incomingNote!!.noteID
                newNote!!.content = incomingNote!!.content
                newNote!!.title = incomingNote!!.title
                if (incomingNote!!.noteTags != null)
                    newNote!!.noteTags = incomingNote!!.noteTags
                else
                    newNote!!.noteTags = ""
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

//Confirm User Saving or cancelling note before leaving
        MaterialAlertDialogBuilder(this)
            .setTitle("Save Note?")
            .setMessage(String.format(resources.getString(R.string.save_note_message)))
            .setNegativeButton("No") { _, _ ->
                finish()
            }
            .setPositiveButton("Yes") { _, _ -> saveButton!!.performClick() }
            .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("mode", mode)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mode = savedInstanceState.getInt("mode")
        if (mode == EDIT_ON) {
            val anchorView = findViewById<View>(R.id.masterLayout)
            Snackbar.make(anchorView, "Note Editing Enabled", Snackbar.LENGTH_SHORT).show()
        }
    }

    //Disable Editing
    private fun disableEdit() {
        notesContent!!.isEnabled = false
        notesSubject!!.isEnabled = false
        mode = EDIT_OFF
        val anchorView = findViewById<View>(R.id.masterLayout)
        Snackbar.make(anchorView, "Note Editing Disabled", Snackbar.LENGTH_SHORT).show()

    }

    //Enable Editing
    private fun enableEdit() {
        notesContent!!.isEnabled = true
        notesSubject!!.isEnabled = true
        mode = EDIT_ON
        val anchorView = findViewById<View>(R.id.masterLayout)
        Snackbar.make(anchorView, "Note Editing Enabled", Snackbar.LENGTH_SHORT).show()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.share_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareAction -> {
                shareList = """
                ${notesSubject!!.editText!!.text}
                ${notesContent!!.editText!!.text}
                """.trimIndent()
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                val intentType = "SHARE"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareList)
                shareIntent.type = "text/plain"
                startActivity(Intent.createChooser(shareIntent, intentType))
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