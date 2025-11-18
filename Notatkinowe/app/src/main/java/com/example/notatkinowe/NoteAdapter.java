package com.example.notatkinowe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(long id);
    }

    public NoteAdapter(List<Note> notes, OnNoteClickListener listener) {
        this.notes = notes;
        this.listener = listener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = notes.get(position);

        holder.noteId.setText("ID: " + note.getId());
        holder.noteTitle.setText("Tytuł: " + note.getTitle());
        holder.noteText.setText("Treść: " + note.getText());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onNoteClick(note.getId());
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteId, noteTitle, noteText;

        public NoteViewHolder(View itemView) {
            super(itemView);
            noteId = itemView.findViewById(R.id.noteId);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteText = itemView.findViewById(R.id.noteText);
        }
    }
}

