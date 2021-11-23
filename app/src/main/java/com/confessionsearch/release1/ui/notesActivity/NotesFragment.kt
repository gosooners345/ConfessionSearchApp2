package com.confessionsearch.release1.ui.notesActivity

/* Author: Brandon Guerin
*
*  Language: Kotlin
*
*  Application: The Reformed Collective
*  Class Name: NotesFragment.kt
*  Purpose: This is the class file that handles events on the Notes section of the application
*/

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.confessionsearch.release1.MainActivity
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.notes.NoteRepository
import com.confessionsearch.release1.databinding.FragmentNotesBinding
import com.confessionsearch.release1.helpers.NotesAdapter
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class NotesFragment : Fragment(), NotesAdapter.OnNoteListener {

    private lateinit var notesViewModel: NotesViewModel
    private var _binding: FragmentNotesBinding? = null

    var notesList: RecyclerView? = null
    var fab: ExtendedFloatingActionButton? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        notesViewModel =
            ViewModelProvider(this).get(NotesViewModel::class.java)

        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        notesViewModel.noteRepository = NoteRepository(context)
        fetchNotes()
        notesList = root.findViewById(R.id.notesListView)
        adapter = NotesAdapter(MainActivity.notesArrayList, this)
        notesList!!.layoutManager = LinearLayoutManager(context)
        notesList!!.itemAnimator = DefaultItemAnimator()
        notesList!!.adapter = adapter
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notesList)
        //fab = root.findViewById(R.id.newNote)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Load notes
    override fun onNoteClick(position: Int) {

        MainActivity.notesArrayList[position]
        val title = MainActivity.notesArrayList[position].name
        val content = MainActivity.notesArrayList[position].content
        val intent = Intent(context, NotesComposeActivity::class.java)
        intent.putExtra("activity_ID", ACTIVITY_ID)
        intent.putExtra("note_selected", MainActivity.notesArrayList[position])
        startActivity(intent)

    }

    //Implement delete functionality
    var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                notesViewModel.deleteNote(MainActivity.notesArrayList[viewHolder.adapterPosition])
            }
        }

    //Critical for retrieving notes for the application
    private fun fetchNotes() {

        notesViewModel.noteRepository!!.fetchNotes().observe(viewLifecycleOwner, { notes ->
            if (MainActivity.notesArrayList.size > 0) MainActivity.notesArrayList.clear()
            if (notes != null) {
                MainActivity.notesArrayList.addAll(notes)
            }
            adapter!!.notifyDataSetChanged()
        }
        )

    }


    companion object {
        @JvmField
        var adapter: NotesAdapter? = null
        const val ACTIVITY_ID = 32
        const val buttonText = "New Note"
        const val buttonPic = R.drawable.ic_add_note
        fun NewNote(context: Context?) {
            val intent = Intent(context, NotesComposeActivity::class.java)
            intent.putExtra("activity_ID", NotesFragment.ACTIVITY_ID)
            context!!.startActivity(intent)
        }
    }
}