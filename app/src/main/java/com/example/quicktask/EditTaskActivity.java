package com.example.quicktask;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity {

    EditText titleInput, descriptionInput, dueDateInput, timeInput, tagInput, noteInput;
    Spinner listSpinner;
    RadioGroup priorityGroup;
    CheckBox flagCheck, completeCheck;
    Button saveBtn, deleteBtn, cancelBtn;
    TaskDatabaseHelper db;
    int taskId;
    ArrayList<String> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // Hooking up all the UI inputs like title, date, time, list, etc.
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dueDateInput = findViewById(R.id.dueDateInput);
        timeInput = findViewById(R.id.timeInput);
        tagInput = findViewById(R.id.tagInput);
        noteInput = findViewById(R.id.noteInput);
        listSpinner = findViewById(R.id.listSpinner);
        flagCheck = findViewById(R.id.flagCheck);
        completeCheck = findViewById(R.id.completeCheck);
        priorityGroup = findViewById(R.id.priorityGroup);
        saveBtn = findViewById(R.id.saveBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        // Setup database helper
        db = new TaskDatabaseHelper(this);

        // Get all saved lists from database to show in spinner
        lists = new ArrayList<>();
        lists.add("No List");
        lists.addAll(db.getAllUserLists());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lists);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listSpinner.setAdapter(adapter);

        // Open pickers when user taps date or time input
        dueDateInput.setOnClickListener(v -> showDatePicker(dueDateInput));
        timeInput.setOnClickListener(v -> showTimePicker(timeInput));

        // Grab the task details passed from MainActivity (used for editing)
        Intent intent = getIntent();
        taskId = intent.getIntExtra("id", -1);
        titleInput.setText(intent.getStringExtra("title"));
        descriptionInput.setText(intent.getStringExtra("description"));
        dueDateInput.setText(intent.getStringExtra("dueDate"));
        timeInput.setText(intent.getStringExtra("time"));
        tagInput.setText(intent.getStringExtra("tag"));
        noteInput.setText(intent.getStringExtra("note"));
        flagCheck.setChecked(intent.getIntExtra("flagged", 0) == 1);
        completeCheck.setChecked(intent.getIntExtra("completed", 0) == 1);

        // Preselect the list in the spinner based on the task's current list
        String listValue = intent.getStringExtra("list");
        int index = lists.indexOf(listValue);
        if (index >= 0) listSpinner.setSelection(index);
        else listSpinner.setSelection(0); // fallback to default

        // Preselect the priority radio button
        String priority = intent.getStringExtra("priority");
        if ("Low".equals(priority)) priorityGroup.check(R.id.radioLow);
        else if ("Medium".equals(priority)) priorityGroup.check(R.id.radioMedium);
        else if ("High".equals(priority)) priorityGroup.check(R.id.radioHigh);

        // When save is clicked, update the task with new values
        saveBtn.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String desc = descriptionInput.getText().toString().trim();
            String due = dueDateInput.getText().toString().trim();
            String time = timeInput.getText().toString().trim();
            String tag = tagInput.getText().toString().trim();
            String note = noteInput.getText().toString().trim();
            String list = listSpinner.getSelectedItem().toString();
            boolean flagged = flagCheck.isChecked();
            boolean completed = completeCheck.isChecked();

            // Check if user selected a priority
            int selectedId = priorityGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a priority", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get priority value and update the task
            RadioButton selectedRadio = findViewById(selectedId);
            String pri = selectedRadio.getText().toString();

            Task task = new Task(taskId, title, desc, due, pri, time, tag, note, list, flagged, completed);
            db.updateTask(task);
            Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Delete button removes the task from the database
        deleteBtn.setOnClickListener(v -> {
            db.deleteTask(taskId);
            Toast.makeText(this, "Task Deleted", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Cancel just closes the screen without saving anything
        cancelBtn.setOnClickListener(v -> finish());
    }

    // Show a date picker popup and put the result into the input field
    private void showDatePicker(EditText target) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = year + "-" + (month + 1) + "-" + day;
            target.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Show a time picker popup and put the result into the input field
    private void showTimePicker(EditText target) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            String time = String.format("%02d:%02d", hour, minute);
            target.setText(time);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }
}
