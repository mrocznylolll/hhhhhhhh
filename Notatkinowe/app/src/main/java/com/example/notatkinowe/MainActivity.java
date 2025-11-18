package com.example.notatkinowe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText titleInput, noteInput;
    private Button saveButton;

    private EditText deleteIdInput;
    private Button deleteButton;

    private EditText updateIdInput, updateNoteInput;
    private Button updateButton;

    private RecyclerView notesRecyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;

    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        titleInput = findViewById(R.id.titleInput);
        noteInput = findViewById(R.id.noteInput);
        saveButton = findViewById(R.id.saveButton);

        deleteIdInput = findViewById(R.id.deleteIdInput);
        deleteButton = findViewById(R.id.deleteButton);

        updateIdInput = findViewById(R.id.updateIdInput);
        updateNoteInput = findViewById(R.id.updateNoteInput);
        updateButton = findViewById(R.id.updateButton);

        emptyView = findViewById(R.id.emptyView);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);

        noteList = new ArrayList<>();
        adapter = new NoteAdapter(noteList, this::deleteNoteByClick);

        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setAdapter(adapter);

        saveButton.setOnClickListener(v -> addNote());
        deleteButton.setOnClickListener(v -> deleteNoteManual());
        updateButton.setOnClickListener(v -> updateNote());

        loadNotes();
    }

    private void addNote() {
        String title = titleInput.getText().toString();
        String note = noteInput.getText().toString();

        if (note.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, title);
        values.put(DatabaseHelper.COLUMN_NOTE, note);
        db.insert(DatabaseHelper.TABLE_NOTES, null, values);
        db.close();

        titleInput.setText("");
        noteInput.setText("");

        loadNotes();
    }

    private void loadNotes() {
        noteList.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NOTES,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_ID + " DESC"
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
            String text = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE));

            noteList.add(new Note(id, title, text));
        }

        cursor.close();
        db.close();

        adapter.notifyDataSetChanged();
        updateVisibility();
    }

    private void updateVisibility() {
        if (noteList.isEmpty()) {
            notesRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            notesRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }


    private void deleteNoteManual() {
        String id = deleteIdInput.getText().toString();
        if (id.isEmpty()) return;

        deleteNote(Long.parseLong(id));
    }


    private void deleteNoteByClick(long id) {
        deleteNote(id);
    }

    private void deleteNote(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        loadNotes();
    }

    private void updateNote() {
        String id = updateIdInput.getText().toString();
        String newText = updateNoteInput.getText().toString();

        if (id.isEmpty() || newText.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NOTE, newText);

        db.update(DatabaseHelper.TABLE_NOTES, cv, DatabaseHelper.COLUMN_ID + " = ?", new String[]{id});
        db.close();

        loadNotes();
    }
}
