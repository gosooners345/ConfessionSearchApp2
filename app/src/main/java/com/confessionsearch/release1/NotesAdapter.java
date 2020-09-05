package com.confessionsearch.release1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private List<Notes> noteList;
    public NotesAdapter(List<Notes> importNotes)
    {
        noteList=importNotes;
    }




    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View noteView=inflater.inflate(R.layout.notes_item_layout,parent,false);

        return new ViewHolder(noteView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
Notes note = noteList.get(position);
TextView noteTitle = holder.subjectView;
noteTitle.setText(note.getName());
TextView contentHolder = holder.contentView;
contentHolder.setText(note.getContent());

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView contentView;
        public TextView subjectView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        subjectView = itemView.findViewById(R.id.content_Title);
        contentView = itemView.findViewById(R.id.content_text);
        }
    }
}
