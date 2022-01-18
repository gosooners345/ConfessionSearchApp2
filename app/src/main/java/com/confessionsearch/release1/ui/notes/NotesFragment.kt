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
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.confessionsearch.release1.MainActivity
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.notes.NoteRepository
import com.confessionsearch.release1.data.notes.Notes
import com.confessionsearch.release1.databinding.FragmentNotesBinding
import com.confessionsearch.release1.helpers.NotesAdapter
import com.confessionsearch.release1.helpers.OnNoteListener
import com.confessionsearch.release1.helpers.RecyclerViewSpaceExtender
import com.confessionsearch.release1.searchhandlers.SearchNotesActivity
import com.confessionsearch.release1.ui.help.HelpPageFragment
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
        val prefs = requireContext().getSharedPreferences(MainActivity.prefsName, MODE_PRIVATE)
        val layoutChoice = prefs.getString("noteLayoutSelection", "linear")
        val gridSize = prefs.getInt("gridSize", 2)
        var layoutRoller: RecyclerView.LayoutManager?
        if (layoutChoice == "linear")
            layoutRoller = LinearLayoutManager(requireContext())
        else if (layoutChoice == "grid")
            layoutRoller = GridLayoutManager(requireContext(), gridSize)
        else
            layoutRoller = StaggeredGridLayoutManager(gridSize, StaggeredGridLayoutManager.VERTICAL)



        notesList.layoutManager = layoutRoller
        notesList.itemAnimator = DefaultItemAnimator()
        notesList.adapter = adapter
        val divider = RecyclerViewSpaceExtender(8)
        notesList.addItemDecoration(divider)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notesList)
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
            R.id.helpPage -> {
                val helpIntent = Intent(requireContext(), HelpPageFragment::class.java)
                requireContext().startActivity(helpIntent)
                return true
            }
            R.id.dateUpdated
            -> {
                Collections.sort(notesArrayList, Notes.compareDateTime)
                if (!reversed) {
                    notesArrayList.reverse()
                    adapter!!.notifyDataSetChanged()
                    reversed = true
                } else {
                    adapter!!.notifyDataSetChanged()
                    reversed = false
                }
                true
            }
            R.id.alphabetized -> {
                Collections.sort(notesArrayList, Notes.compareAlphabetized)
                if (reversed) {
                    adapter!!.notifyDataSetChanged()
                    reversed = false
                } else {
                    notesArrayList.reverse()
                    adapter!!.notifyDataSetChanged()
                    reversed = true
                }
                true
            }

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