package com.confessionsearch.release1.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.confessionsearch.release1.R;
import com.confessionsearch.release1.data.notes.Notes;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private final OnNoteListener onNoteListener;
    private ArrayList<Notes> noteList = new ArrayList<>();

    public NotesAdapter(ArrayList<Notes> importNotes, OnNoteListener onNoteListener) {
        noteList = importNotes;
        this.onNoteListener = onNoteListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View noteView = inflater.inflate(R.layout.notes_item_layout, parent, false);

        return new ViewHolder(noteView, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notes note = noteList.get(position);
        TextView noteTitle = holder.subjectView;
        noteTitle.setText(note.getName());
        TextView contentHolder = holder.contentView;
        contentHolder.setText((note.getContent()));//note.getContent());

    }

    @Override
    public int getItemCount() {

        return noteList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView contentView;
        public TextView subjectView;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            subjectView = itemView.findViewById(R.id.content_Title);
            contentView = itemView.findViewById(R.id.content_text);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
