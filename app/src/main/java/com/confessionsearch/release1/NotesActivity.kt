package com.confessionsearch.release1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.confessionsearch.release1.NotesAdapter.OnNoteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class NotesActivity : AppCompatActivity(), OnNoteListener {
    var bottomNav: BottomNavigationView? = null
    var noteRepository: NoteRepository? = null

    //static ArrayList<Notes> notesArrayList = new ArrayList<>(),secondList;
    var notesList: RecyclerView? = null
    var fab: ExtendedFloatingActionButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        //set theme to app theme
        if (MainActivity.themeID == R.style.LightMode) setTheme(R.style.LightMode_NoActionBar)
        if (MainActivity.themeID == R.style.DarkMode) setTheme(R.style.DarkMode_NoActionBar)
        super.onCreate(savedInstanceState)
        //set layout to notes list
        setContentView(R.layout.activity_notes)
        //not sure what this is useful for
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Notes"
        setSupportActionBar(toolbar)
        noteRepository = NoteRepository(this)
        fetchNotes()
        //Initialize notes RecyclerView
        notesList = findViewById(R.id.notesListView)
        adapter = NotesAdapter(MainActivity.notesArrayList, this)
        notesList!!.layoutManager = LinearLayoutManager(this)
        notesList!!.itemAnimator = DefaultItemAnimator()
        notesList!!.adapter = adapter
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notesList)
        //assign variable to reference for new note
        fab = findViewById(R.id.newNote)
    }

    private fun fetchNotes() {
        noteRepository!!.fetchNotes().observe(this, { notes ->
            if (MainActivity.notesArrayList.size > 0) MainActivity.notesArrayList.clear()
            if (notes != null) {
                MainActivity.notesArrayList.addAll(notes)
            }
            adapter!!.notifyDataSetChanged()
        }
        )
    }

    //Fetch notes from storage
    fun NewNote(view: View?) {
        val intent = Intent(applicationContext, NotesComposeActivity::class.java)
        intent.putExtra("activity_ID", ACTIVITY_ID)
        startActivity(intent)
    }

    override fun onNoteClick(position: Int) {
        MainActivity.notesArrayList[position]
        val title = MainActivity.notesArrayList[position].name
        val content = MainActivity.notesArrayList[position].content
        val intent = Intent(this, NotesComposeActivity::class.java)
        intent.putExtra("activity_ID", ACTIVITY_ID)
        intent.putExtra("note_selected", MainActivity.notesArrayList[position])
        startActivity(intent)
    }

    var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            deleteNote(MainActivity.notesArrayList[viewHolder.adapterPosition])
        }
    }

    //Note deletion
    private fun deleteNote(note: Notes) {
        MainActivity.notesArrayList.remove(note)
        noteRepository!!.deleteNote(note)
        adapter!!.notifyDataSetChanged()
    }

    companion object {
        //ArrayAdapter<Notes> arrayAdapter;
        private const val TAG = "NotesActivity"

        @JvmField
        var adapter: NotesAdapter? = null
        const val ACTIVITY_ID = 32
    }
}