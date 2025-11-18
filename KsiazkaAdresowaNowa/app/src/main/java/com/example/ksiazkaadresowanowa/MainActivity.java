package com.example.ksiazkaadresowanowa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelperContacts dbHelper;
    private EditText nameInput, phoneInput;
    private Button saveButton;

    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private List<Contact> contactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelperContacts(this);

        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        saveButton = findViewById(R.id.saveButton);

        recyclerView = findViewById(R.id.recyclerViewContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ContactsAdapter(contactList);
        recyclerView.setAdapter(adapter);

        saveButton.setOnClickListener(v -> addContact());

        loadContacts();
    }

    private void addContact() {
        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();

        if (name.isEmpty() || phone.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelperContacts.COLUMN_NAME, name);
        values.put(DatabaseHelperContacts.COLUMN_PHONE, phone);
        db.insert(DatabaseHelperContacts.TABLE_CONTACTS, null, values);
        db.close();

        nameInput.setText("");
        phoneInput.setText("");

        loadContacts();
    }

    private void loadContacts() {
        contactList.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelperContacts.TABLE_CONTACTS,
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelperContacts.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelperContacts.COLUMN_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelperContacts.COLUMN_PHONE));

            contactList.add(new Contact(id, name, phone));
        }

        cursor.close();
        db.close();

        adapter.updateData(contactList);
    }
}
