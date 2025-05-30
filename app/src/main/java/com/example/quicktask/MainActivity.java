package com.example.quicktask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TaskAdapter adapter;
    ArrayList<Task> tasks;
    TaskDatabaseHelper db;
    FloatingActionButton fabAdd, fabAddList;
    TabLayout tabLayout;

    String currentTab = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup RecyclerView to show the list of tasks
        recyclerView = findViewById(R.id.taskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Floating buttons to add a new task or a new list
        fabAdd = findViewById(R.id.fabAddTask);
        fabAddList = findViewById(R.id.fabAddList);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        fabAddList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddListActivity.class);
            startActivity(intent);
        });

        // Initialize the database and adapter
        db = new TaskDatabaseHelper(this);
        tasks = new ArrayList<>();
        adapter = new TaskAdapter(this, tasks);
        recyclerView.setAdapter(adapter);

        // Set up tabs for filtering the task list
        tabLayout = findViewById(R.id.tabLayout);
        setupTabs();

        // Enable swipe gestures for delete/complete
        setupSwipe();
    }

    private void setupTabs() {
        // Add all the tabs that the user can click to filter tasks
        tabLayout.addTab(tabLayout.newTab().setText("ALL"));
        tabLayout.addTab(tabLayout.newTab().setText("Today"));
        tabLayout.addTab(tabLayout.newTab().setText("Flagged"));
        tabLayout.addTab(tabLayout.newTab().setText("Completed"));
        tabLayout.addTab(tabLayout.newTab().setText("My Lists")); // this one opens a dialog

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                CharSequence tabText = tab.getText();
                currentTab = (tabText != null) ? tabText.toString() : "ALL";

                // If user selects "My Lists", show the dialog to pick a list
                if ("My Lists".equals(currentTab)) {
                    showListSelectionDialog();
                } else {
                    loadTasks(currentTab); // otherwise, load tasks normally
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            // Also allow reopening list selection if the tab is clicked again
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if ("My Lists".equals(tab.getText().toString())) {
                    showListSelectionDialog();
                }
            }
        });
    }

    // This shows a popup with all the user's custom lists
    private void showListSelectionDialog() {
        ArrayList<String> userLists = db.getAllUserLists();
        if (userLists.isEmpty()) {
            Toast.makeText(this, "No lists found. Please add one first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] listArray = userLists.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("Select a List")
                .setItems(listArray, (dialog, which) -> {
                    String selectedList = listArray[which];
                    tasks.clear();
                    tasks.addAll(db.getTasksByList(selectedList));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Showing tasks in: " + selectedList, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Load tasks based on selected tab
    private void loadTasks(String tabFilter) {
        tasks.clear();

        switch (tabFilter) {
            case "Completed":
                tasks.addAll(db.getCompletedTasks());
                break;
            case "Today":
                tasks.addAll(db.getTasksByDate(DateUtils.getTodayDate()));
                break;
            case "Flagged":
                tasks.addAll(db.getFlaggedTasks());
                break;
            default:
                tasks.addAll(db.getAllTasks());
        }

        Log.d("LOAD_TASKS", "Loaded " + tasks.size() + " tasks for Tab: " + tabFilter);
        adapter.notifyDataSetChanged();
    }

    // Allows the user to swipe right to delete or left to mark complete
    private void setupSwipe() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task task = tasks.get(position);

                if (direction == ItemTouchHelper.RIGHT) {
                    // Confirmation popup before deleting the task
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Delete Task")
                            .setMessage("Are you sure you want to delete this task?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                db.deleteTask(task.getId());
                                tasks.remove(position);
                                adapter.notifyItemRemoved(position);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> adapter.notifyItemChanged(position))
                            .show();
                } else if (direction == ItemTouchHelper.LEFT) {
                    // Mark the task as completed
                    task.setCompleted(true);
                    db.updateTask(task);
                    Toast.makeText(MainActivity.this, "Marked as completed", Toast.LENGTH_SHORT).show();
                    tasks.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);
    }

    // Refresh tasks when returning to this screen
    @Override
    protected void onResume() {
        super.onResume();
        if (!"My Lists".equals(currentTab)) {
            loadTasks(currentTab);
        }
    }
}
