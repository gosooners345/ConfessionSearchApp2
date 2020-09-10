package com.confessionsearch.release1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.confessionsearch.release1.MainActivity.notesArrayList;

public class NotesActivity extends AppCompatActivity implements NotesAdapter.OnNoteListener {
//ArrayAdapter<Notes> arrayAdapter;
    private static final String TAG = "NotesActivity";
    static NotesAdapter adapter;
    public static final int ACTIVITY_ID = 32;
    public NoteRepository noteRepository;
    //static ArrayList<Notes> notesArrayList = new ArrayList<>(),secondList;
RecyclerView notesList;
    ExtendedFloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set theme to app theme
        if(MainActivity.themeID==R.style.LightMode)
            setTheme(R.style.LightMode_NoActionBar);
        if(MainActivity.themeID==R.style.DarkMode)
            setTheme(R.style.DarkMode_NoActionBar);
        super.onCreate(savedInstanceState);
        //set layout to notes list
        setContentView(R.layout.activity_notes);
        //not sure what this is useful for
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        noteRepository = new NoteRepository(this);
        fetchNotes();
        //Initialize notes RecyclerView
        notesList = findViewById(R.id.notesListView);
        adapter = new NotesAdapter(notesArrayList, this);
        notesList.setLayoutManager(new LinearLayoutManager(this));
        notesList.setItemAnimator(new DefaultItemAnimator());
        notesList.setAdapter(adapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notesList);
        fab = findViewById(R.id.newNote);

    }

    private void fetchNotes() {
        noteRepository.fetchNotes().observe(this, new Observer<List<Notes>>() {
                    @Override
                    public void onChanged(List<Notes> notes) {
                        if (notesArrayList.size() > 0)
                            notesArrayList.clear();
                        if (notes != null) {
                            notesArrayList.addAll(notes);

                        }
                        adapter.notifyDataSetChanged();

                    }
                }
        );
    }

    //Fetch notes from storage
    public void NewNote(View view) {
        Intent intent = new Intent(getApplicationContext(), NotesComposeActivity.class);
        intent.putExtra("activity_ID", ACTIVITY_ID);
        startActivity(intent);
    }

    @Override
    public void onNoteClick(int position) {
        notesArrayList.get(position);
        String title = notesArrayList.get(position).getName(), content = notesArrayList.get(position).getContent();
        Intent intent = new Intent(this, NotesComposeActivity.class);
        intent.putExtra("activity_ID", ACTIVITY_ID);
        intent.putExtra("note_selected", notesArrayList.get(position));
        startActivity(intent);

    }
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            deleteNote(notesArrayList.get(viewHolder.getAdapterPosition()));
        }
    };
    //Note deletion
private void deleteNote(Notes note) {
    notesArrayList.remove(note);
    noteRepository.deleteNote(note);
    adapter.notifyDataSetChanged();

}


}