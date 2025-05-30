package com.example.quicktask;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddListActivity extends AppCompatActivity {

    EditText listNameInput;
    Button saveListBtn, cancelBtn;
    TaskDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        // Linking UI elements from the XML layout
        listNameInput = findViewById(R.id.listNameInput);
        saveListBtn = findViewById(R.id.saveListBtn);
        cancelBtn = findViewById(R.id.cancelListBtn);

        // Connecting to the database
        db = new TaskDatabaseHelper(this);

        // Save button logic: save the list if name is not empty
        saveListBtn.setOnClickListener(v -> {
            String listName = listNameInput.getText().toString().trim();

            // Show message if user leaves the field empty
            if (listName.isEmpty()) {
                Toast.makeText(this, "Please enter a list name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save the list name to the database
            db.addNewList(listName);
            Toast.makeText(this, "List created", Toast.LENGTH_SHORT).show();
            finish(); // Close this screen and go back
        });

        // Cancel just closes the activity without saving
        cancelBtn.setOnClickListener(v -> finish());
    }
}
