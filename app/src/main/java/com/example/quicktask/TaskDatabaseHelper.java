package com.example.quicktask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * This class manages the SQLite database for storing tasks and lists.
 * It helps us create, update, delete, and fetch tasks easily using functions.
 */
public class TaskDatabaseHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "quicktask.db";
    private static final int DATABASE_VERSION = 3;

    // Table and column names
    private static final String TABLE_NAME = "tasks";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_TAG = "tag";
    private static final String COLUMN_NOTE = "note";
    private static final String COLUMN_LIST = "list";
    private static final String COLUMN_FLAGGED = "flagged";
    private static final String COLUMN_COMPLETED = "completed";

    // Constructor
    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // This method runs once when the app creates the database for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a separate table for storing custom list names
        db.execSQL("CREATE TABLE lists (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");

        // Create the main tasks table
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_DUE_DATE + " TEXT," +
                COLUMN_PRIORITY + " TEXT," +
                COLUMN_TIME + " TEXT," +
                COLUMN_TAG + " TEXT," +
                COLUMN_NOTE + " TEXT," +
                COLUMN_LIST + " TEXT," +
                COLUMN_FLAGGED + " INTEGER," +
                COLUMN_COMPLETED + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    // This method runs when we upgrade the version number
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Clear old tables if they exist, then recreate them
        db.execSQL("DROP TABLE IF EXISTS tasks");
        db.execSQL("DROP TABLE IF EXISTS lists");
        onCreate(db);
    }

    // Insert a new list into the 'lists' table
    public void addNewList(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insert("lists", null, values);
        db.close();
    }

    // Get all custom list names from the database
    public ArrayList<String> getAllUserLists() {
        ArrayList<String> lists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM lists", null);
        while (cursor.moveToNext()) {
            lists.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return lists;
    }

    // Add a new task to the database
    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_TIME, task.getTime());
        values.put(COLUMN_TAG, task.getTag());
        values.put(COLUMN_NOTE, task.getNote());
        values.put(COLUMN_LIST, task.getList());
        values.put(COLUMN_FLAGGED, task.isFlagged() ? 1 : 0); // store boolean as 0 or 1
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Update a task's information in the database
    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_TIME, task.getTime());
        values.put(COLUMN_TAG, task.getTag());
        values.put(COLUMN_NOTE, task.getNote());
        values.put(COLUMN_LIST, task.getList());
        values.put(COLUMN_FLAGGED, task.isFlagged() ? 1 : 0);
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    // Delete a task by ID
    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Get all tasks that are NOT completed (used for the main task list)
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE completed = 0", null);
        while (cursor.moveToNext()) {
            taskList.add(extractTaskFromCursor(cursor));
        }
        cursor.close();
        db.close();
        return taskList;
    }

    // Get tasks that belong to a specific list
    public ArrayList<Task> getTasksByList(String listName) {
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE list = ? AND completed = 0", new String[]{listName});
        while (cursor.moveToNext()) {
            taskList.add(extractTaskFromCursor(cursor));
        }
        cursor.close();
        db.close();
        return taskList;
    }

    // Get only the tasks that are completed
    public ArrayList<Task> getCompletedTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE completed = 1", null);
        while (cursor.moveToNext()) {
            taskList.add(extractTaskFromCursor(cursor));
        }
        cursor.close();
        db.close();
        return taskList;
    }

    // Get tasks that are due today
    public ArrayList<Task> getTasksByDate(String date) {
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE due_date = ? AND completed = 0", new String[]{date});
        while (cursor.moveToNext()) {
            taskList.add(extractTaskFromCursor(cursor));
        }
        cursor.close();
        db.close();
        return taskList;
    }

    // Get all tasks that are flagged
    public ArrayList<Task> getFlaggedTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE flagged = 1 AND completed = 0", null);
        while (cursor.moveToNext()) {
            taskList.add(extractTaskFromCursor(cursor));
        }
        cursor.close();
        db.close();
        return taskList;
    }

    // This helper method converts a row from the database into a Task object
    private Task extractTaskFromCursor(Cursor cursor) {
        return new Task(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAG)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FLAGGED)) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1
        );
    }
}
