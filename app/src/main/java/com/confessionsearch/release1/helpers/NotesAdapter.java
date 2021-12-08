package com.confessionsearch.release1.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.confessionsearch.release1.R;
import com.confessionsearch.release1.data.notes.Notes;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private final OnNoteListener onNoteListener;
    private final Context context;
    private final ArrayList<Notes> noteList;
    private int lastPosition = -1;

    public NotesAdapter(ArrayList<Notes> importNotes, OnNoteListener onNoteListener, Context context) {
        noteList = importNotes;
        this.onNoteListener = onNoteListener;
        this.context = context;
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
        noteTitle.setText(note.getTitle());
        TextView contentHolder = holder.contentView;
        contentHolder.setText((note.getContent()));
        TextView timeStamp = holder.timeStamp;
        timeStamp.setText(note.getTime());
        //  setAnimation(holder.itemView, position);
    }


    @Override
    public int getItemCount() {

        return noteList.size();

    }
// Adds animation to the cards
    private void setAnimation(View toAnimate, int position) {
        if (position > lastPosition | position < lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.animate_card_enter);
            animation.scaleCurrentDuration(.5f);
            toAnimate.clearAnimation();

            toAnimate.startAnimation(animation);

            lastPosition = position;
        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView contentView;
        public TextView subjectView;
        public TextView timeStamp;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            subjectView = itemView.findViewById(R.id.content_Title);
            contentView = itemView.findViewById(R.id.content_text);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getBindingAdapterPosition());
        }
    }


}
