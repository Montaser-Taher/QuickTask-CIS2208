package com.example.quicktask;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    EditText titleInput, descriptionInput, dueDateInput, timeInput, tagInput, noteInput;
    Spinner listSpinner;
    RadioGroup priorityGroup;
    CheckBox flagCheck;
    Button saveBtn, cancelBtn;
    TaskDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Grabbing references to all the UI inputs (title, description, date, time, etc.)
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dueDateInput = findViewById(R.id.dueDateInput);
        timeInput = findViewById(R.id.timeInput);
        tagInput = findViewById(R.id.tagInput);
        noteInput = findViewById(R.id.noteInput);
        listSpinner = findViewById(R.id.listSpinner);
        flagCheck = findViewById(R.id.flagCheck);
        priorityGroup = findViewById(R.id.priorityGroup);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        // Setting up the database helper to save the task later
        db = new TaskDatabaseHelper(this);

        // Load all saved list names from database to populate the spinner
        ArrayList<String> lists = new ArrayList<>();
        lists.add("No List"); // Default fallback option
        lists.addAll(db.getAllUserLists());

        // Set the spinner to show list options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lists);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listSpinner.setAdapter(adapter);

        // Open date and time picker dialogs when fields are clicked
        dueDateInput.setOnClickListener(v -> showDatePicker(dueDateInput));
        timeInput.setOnClickListener(v -> showTimePicker(timeInput));

        // Save button logic - collect all input values and save the task
        saveBtn.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String desc = descriptionInput.getText().toString().trim();
            String dueDate = dueDateInput.getText().toString().trim();
            String time = timeInput.getText().toString().trim();
            String tag = tagInput.getText().toString().trim();
            String note = noteInput.getText().toString().trim();

            String selectedList = listSpinner.getSelectedItem() != null ? listSpinner.getSelectedItem().toString() : "No List";
            String list = selectedList.equals("No List") ? "" : selectedList;

            boolean flagged = flagCheck.isChecked();
            boolean completed = false; // new tasks start incomplete

            // Validation: title and priority are required
            if (title.isEmpty()) {
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = priorityGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a priority", Toast.LENGTH_SHORT).show();
                return;
            }

            // Getting selected priority value
            RadioButton selectedRadio = findViewById(selectedId);
            String priority = selectedRadio.getText().toString();

            // Create and save the task to the database
            Task task = new Task(title, desc, dueDate, priority, time, tag, note, list, flagged, completed);
            db.addTask(task);
            Toast.makeText(this, "Task Saved!", Toast.LENGTH_SHORT).show();
            finish(); // Close the screen
        });

        // Cancel button just closes the page without saving
        cancelBtn.setOnClickListener(v -> finish());
    }

    // Opens the date picker and sets selected date in the input field
    private void showDatePicker(EditText target) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format("%04d-%02d-%02d", year, month + 1, day);
            target.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Opens the time picker and sets selected time in the input field
    private void showTimePicker(EditText target) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            String time = String.format("%02d:%02d", hour, minute);
            target.setText(time);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }
}
