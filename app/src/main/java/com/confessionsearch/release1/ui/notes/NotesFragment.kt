package com.confessionsearch.release1.ui.notes

/* Author: Brandon Guerin
*
*  Language: Kotlin
*
*  Application: The Reformed Collective
*  Class Name: NotesFragment.kt
*  Purpose: This is the class file that handles events on the Notes section of the application
*/

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.notes.NoteRepository
import com.confessionsearch.release1.data.notes.Notes
import com.confessionsearch.release1.databinding.FragmentNotesBinding
import com.confessionsearch.release1.helpers.NotesAdapter
import com.confessionsearch.release1.helpers.OnNoteListener
import com.confessionsearch.release1.helpers.RecyclerViewSpaceExtender
import com.confessionsearch.release1.searchhandlers.SearchNotesActivity
import java.util.*
import kotlin.collections.ArrayList

class NotesFragment : Fragment(), OnNoteListener {

    private lateinit var notesViewModel: NotesViewModel
    private var _binding: FragmentNotesBinding? = null
    lateinit var notesList: RecyclerView
    var reversed = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        notesViewModel =
            ViewModelProvider(this)[NotesViewModel::class.java]
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        notesViewModel.noteRepository = NoteRepository(context)

        //Retrieve Notes for application
        fetchNotes()
        adapter = NotesAdapter(notesArrayList, this, requireContext())
        notesList = root.findViewById(R.id.notesListView)
        notesList.layoutManager = LinearLayoutManager(context)
        notesList.itemAnimator = DefaultItemAnimator()
        notesList.adapter = adapter
        val divider = RecyclerViewSpaceExtender(8)
        notesList.addItemDecoration(divider)
        setHasOptionsMenu(true)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Load Note for Editing
    override fun onNoteClick(position: Int) {
        val intent = Intent(context, NotesComposeActivity::class.java)
        intent.putExtra("activity_ID", ACTIVITY_ID)
        intent.putExtra("note_selected", notesArrayList[position])
        startActivity(intent)
    }

    //Implement delete functionality
    private var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                notesViewModel.deleteNote(notesArrayList[viewHolder.bindingAdapterPosition])
            }
        }

    //Critical for retrieving notes for the application
    private fun fetchNotes() {
        //This is here for migration testing

        notesViewModel.noteRepository!!.fetchNotes().observe(viewLifecycleOwner, { notes ->
            if (notesArrayList.size > 0) notesArrayList.clear()
            if (notes != null) {
                notesArrayList.addAll(notes)
            }
            notesArrayList.sortWith(Notes.compareDateTime)
            adapter!!.notifyDataSetChanged()
        }
        )

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            /* R.id.dateCreated -> {
                Collections.sort(notesArrayList, Notes.compareIDs)
                adapter!!.notifyDataSetChanged()
                true
            }*/
            R.id.dateUpdated
            -> {
                Collections.sort(notesArrayList, Notes.compareDateTime)
                if (!reversed) {
                    notesArrayList.reverse()
                    adapter!!.notifyDataSetChanged()
                    reversed = true

                } else {
                    //  Collections.sort(notesArrayList, Notes.compareDateTime)
                    adapter!!.notifyDataSetChanged()
                    reversed = false
                }
                true
            }
            R.id.alphabetized -> {
                if (reversed) {
                    Collections.sort(notesArrayList, Notes.compareAlphabetized)
                    adapter!!.notifyDataSetChanged()
                    reversed = false
                } else {
                    Collections.sort(notesArrayList, Notes.compareAlphabetized)
                    notesArrayList.reverse()
                    adapter!!.notifyDataSetChanged()
                    reversed = true
                }
                true
            }
            /*R.id.updatedDescending -> {
                Notes.compareDateTime?.let { notesArrayList.sortWith(it) }
                adapter!!.notifyDataSetChanged()
                true
            }*/
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.notes_sort_menu, menu)
        val searchManager: SearchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        ((menu.findItem(R.id.menu_search).actionView) as SearchView).apply {
            setSearchableInfo(
                searchManager.getSearchableInfo(
                    ComponentName(
                        context,
                        SearchNotesActivity::class.java
                    )
                )
            )
        }

        return super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        @JvmField
        var adapter: NotesAdapter? = null
        var notesArrayList = ArrayList<Notes>()
        const val ACTIVITY_ID = 32
        const val buttonText = "New Note"
        const val buttonPic = R.drawable.ic_add_note
        fun newNote(context: Context?) {
            val intent = Intent(context, NotesComposeActivity::class.java)
            intent.putExtra("activity_ID", NotesFragment.ACTIVITY_ID)
            context!!.startActivity(intent)
        }

    }
}