package com.confessionsearch.release1.searchhandlers

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.notes.Notes
import com.confessionsearch.release1.helpers.NotesAdapter
import com.confessionsearch.release1.helpers.OnNoteListener
import com.confessionsearch.release1.helpers.RecyclerViewSpaceExtender
import com.confessionsearch.release1.ui.notesActivity.NotesComposeActivity
import com.confessionsearch.release1.ui.notesActivity.NotesFragment

class SearchNotesActivity : AppCompatActivity(), OnNoteListener {

    lateinit var notesList: RecyclerView
    var resultsList = ArrayList<Notes>()
    lateinit var clearButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_results_notes)
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                searchNotes(query)
            }
            notesList = findViewById(R.id.notesListView)
            notesList.layoutManager = LinearLayoutManager(this)
            notesList.itemAnimator = DefaultItemAnimator()
            notesList.adapter = NotesAdapter(resultsList, this, this)
            val divider = RecyclerViewSpaceExtender(8)
            notesList.addItemDecoration(divider)
            clearButton = findViewById(R.id.clearButton)
            clearButton.setOnClickListener(cancelButtonClickListener)

        }


    }


    fun searchNotes(query: String) {
        for (entry in NotesFragment.notesArrayList) {
            var counter = 0
            val searchArray = ArrayList<String>()
            searchArray.add(entry.title!!)
            searchArray.add(entry.content!!)
            for (i in 0..searchArray.size - 1) {
                if (searchArray[i].contains(query)) {
                    counter++
                }
            }
            if (counter > 0)
                resultsList.add(entry)
        }

    }

    private fun cancelButton() {
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        cancelButton()
    }

    var cancelButtonClickListener = View.OnClickListener {
        cancelButton()
    }

    override fun onNoteClick(position: Int) {
        val intent = Intent(this, NotesComposeActivity::class.java)
        intent.putExtra("activity_ID", 68)
        intent.putExtra("note_selected", resultsList[position])
        startActivity(intent)
    }
}