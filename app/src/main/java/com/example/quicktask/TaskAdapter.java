package com.example.quicktask;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import android.widget.Toast;

/**
 * TaskAdapter connects our task data to the RecyclerView.
 * It displays each task inside a card using item_task.xml layout.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> tasks; // The list of tasks to display
    private TaskDatabaseHelper db; // We use this to update task info like flag/completion
    private Context context;       // Needed to launch new activities like EditTaskActivity

    // Constructor to initialize the adapter with context and list of tasks
    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
        this.db = new TaskDatabaseHelper(context); // Initialize database access
    }

    /**
     * ViewHolder holds the UI components for each item in the list.
     * This is like a template for each row (or card) in the RecyclerView.
     */
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, dueDate, time, priority;
        CheckBox completeCheck;
        ImageView flagIcon;

        public TaskViewHolder(View itemView) {
            super(itemView);

            // Connect the XML views from item_task.xml to Java variables
            title = itemView.findViewById(R.id.taskTitle);
            dueDate = itemView.findViewById(R.id.taskDueDate);
            time = itemView.findViewById(R.id.taskTime);
            priority = itemView.findViewById(R.id.taskPriority);
            completeCheck = itemView.findViewById(R.id.completeCheck);
            flagIcon = itemView.findViewById(R.id.flagIcon);
        }
    }

    /**
     * This method creates each item card using the layout file item_task.xml.
     * It runs only when the RecyclerView needs a new card to be shown.
     */
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    /**
     * Here we fill the card with actual data from a Task object.
     * This method is called automatically for every visible task on screen.
     */
    @Override
    public void onBindViewHolder(TaskAdapter.TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        // Set task title, due date, and priority
        holder.title.setText(task.getTitle());
        holder.dueDate.setText("Due: " + task.getDueDate());
        holder.priority.setText("Priority: " + task.getPriority());
        holder.completeCheck.setChecked(task.isCompleted());

        // Show the star icon for flagging tasks
        holder.flagIcon.setVisibility(View.VISIBLE);
        holder.flagIcon.setImageResource(task.isFlagged() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

        // If the user taps the star, toggle the flag on/off
        holder.flagIcon.setOnClickListener(v -> {
            boolean newFlag = !task.isFlagged();
            task.setFlagged(newFlag);
            db.updateTask(task); // update the flag in the database
            notifyItemChanged(position); // refresh the item
            Toast.makeText(context, newFlag ? "Marked as flagged" : "Unflagged", Toast.LENGTH_SHORT).show();
        });

        // If user clicks the whole card, open EditTaskActivity with all task details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditTaskActivity.class);
            intent.putExtra("id", task.getId());
            intent.putExtra("title", task.getTitle());
            intent.putExtra("description", task.getDescription());
            intent.putExtra("dueDate", task.getDueDate());
            intent.putExtra("priority", task.getPriority());
            intent.putExtra("time", task.getTime());
            intent.putExtra("tag", task.getTag());
            intent.putExtra("note", task.getNote());
            intent.putExtra("list", task.getList());
            intent.putExtra("flagged", task.isFlagged());
            intent.putExtra("completed", task.isCompleted());
            context.startActivity(intent); // Launch edit screen
        });

        // When the checkbox is toggled, update the task's completed status
        holder.completeCheck.setOnClickListener(v -> {
            task.setCompleted(holder.completeCheck.isChecked());
            db.updateTask(task);
            notifyItemChanged(position);
        });
    }

    // This tells RecyclerView how many task items are in the list
    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
