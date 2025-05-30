package com.example.quicktask;

/**
 * This class represents a single task/reminder in the app.
 * It stores all the details about the task, like title, date, time, priority, and so on.
 */
public class Task {
    // Unique ID for the task (used in database)
    private int id;

    // Main task details
    private String title;
    private String description;
    private String dueDate;
    private String priority;
    private String time;
    private String tag;
    private String note;
    private String list;

    // Flags to track if the task is marked or completed
    private boolean flagged;
    private boolean completed;

    /**
     * This constructor is used when creating a new task (before it's saved to the database).
     * The ID is not needed yet since it will be auto-generated later.
     */
    public Task(String title, String description, String dueDate, String priority, String time,
                String tag, String note, String list, boolean flagged, boolean completed) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.time = time;
        this.tag = tag;
        this.note = note;
        this.list = list;
        this.flagged = flagged;
        this.completed = completed;
    }

    /**
     * This constructor is used when loading a task from the database.
     * In this case, we already know the task's ID, so we include it.
     */
    public Task(int id, String title, String description, String dueDate, String priority, String time,
                String tag, String note, String list, boolean flagged, boolean completed) {
        // Call the other constructor to set all the values except the ID
        this(title, description, dueDate, priority, time, tag, note, list, flagged, completed);
        this.id = id;
    }

    // Getter methods let us access the values stored in the task
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDueDate() { return dueDate; }
    public String getPriority() { return priority; }
    public String getTime() { return time; }
    public String getTag() { return tag; }
    public String getNote() { return note; }
    public String getList() { return list; }
    public boolean isFlagged() { return flagged; }
    public boolean isCompleted() { return completed; }

    // Setter methods allow us to update values in the task
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setTime(String time) { this.time = time; }
    public void setTag(String tag) { this.tag = tag; }
    public void setNote(String note) { this.note = note; }
    public void setList(String list) { this.list = list; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    // These are technically duplicates of isCompleted/isFlagged,
    // but you may be using getCompleted/getFlagged in other parts of the app
    public boolean getCompleted() {
        return completed;
    }

    public boolean getFlagged() {
        return flagged;
    }
}
