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
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.confessionsearch.release1.MainActivity
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.notes.NoteRepository
import com.confessionsearch.release1.data.notes.Notes
import com.confessionsearch.release1.databinding.FragmentNotesBinding
import com.confessionsearch.release1.helpers.NotesAdapter
import com.confessionsearch.release1.helpers.OnNoteListener
import com.confessionsearch.release1.helpers.RecyclerViewSpaceExtender
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotesFragment : Fragment(), OnNoteListener {

    private lateinit var notesViewModel: NotesViewModel
    private var _binding: FragmentNotesBinding? = null
    var notesList: RecyclerView? = null
    var upgrades = 0
    private var sharedPreferences: SharedPreferences =
        MainActivity.appcontext!!.getSharedPreferences(
            MainActivity.appName + ".appPrefs",
            Context.MODE_PRIVATE
        )

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
        notesList!!.layoutManager = LinearLayoutManager(context)
        notesList!!.itemAnimator = DefaultItemAnimator()
        notesList!!.adapter = adapter
        val divider = RecyclerViewSpaceExtender(8)
        notesList!!.addItemDecoration(divider)
        // notesList!!.animation= AnimationUtils.loadAnimation(context,R.anim.slidein)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notesList)

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
        Log.d("TEST", "${notesArrayList[position].timeModified}")
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
        try {

            val defaultVal = 2
            upgrades = sharedPreferences.getInt("dbUpgrades", defaultVal)
            Log.d("Test", "SharedPrefsWorks: $upgrades")
            Log.d("Print", "${sharedPreferences.getInt("dbUpgrades", 2)}")
            Log.d("Print2", "$upgrades")

        } catch (exC: Exception) {
            upgrades = 1
            Log.d("Works", "Shared Prefs isn't working yet")
        }
        notesViewModel.noteRepository!!.fetchNotes().observe(viewLifecycleOwner, { notes ->
            if (notesArrayList.size > 0) notesArrayList.clear()
            if (notes != null) {
                notesArrayList.addAll(notes)
            }
            if (upgrades <= 3) {
                addTimes(upgrades)
                upgrades++
                sharedPreferences.edit {
                    putInt("dbUpgrades", upgrades).commit()
                    Log.d("Print", "${sharedPreferences.getInt("dbUpgrades", 2)}")
                    Log.d("Print2", "$upgrades")
                }
            }
            try {

                Collections.sort(notesArrayList, Notes.compareDateTime)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            adapter!!.notifyDataSetChanged()
        }
        )

    }
    //one-time use

    fun addTimes(upgrade: Int) {
        if (upgrade < 3)
            for (note: Notes in notesArrayList) {
                note.timeModified = System.currentTimeMillis()
                note.time = DateFormat.getInstance().format(note.timeModified)
                notesViewModel.noteRepository!!.updateNote(note)
            }


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
