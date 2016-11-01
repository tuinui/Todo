package com.example.user.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 1/11/2559.
 */
public class MyDbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "tododata";
    private static final String TABLE_TODO = "todo";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_STATE = "shop_address";

    public MyDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static MyDbHelper with(Context context) {
        return new MyDbHelper(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TODO + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_STATE + " INT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    public void addData(TodoDB data) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, data.getName());
        values.put(KEY_STATE, data.getState());
        values.put(KEY_ID, data.getId());
        db.replace(TABLE_TODO, null, values);
    }


    public List<TodoDB> getAllTodoList() {
        List<TodoDB> todoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TODO;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                TodoDB data = new TodoDB();
                data.setId(Long.parseLong(cursor.getString(0)));
                data.setName(cursor.getString(1));
                data.setState(cursor.getInt(2));
                todoList.add(data);
            } while (cursor.moveToNext());
        }
        return todoList;
    }

    public void deleteTodo(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        for (Long id : ids) {
            db.delete(TABLE_TODO, KEY_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }


    }

    public long getLatestId() {
        List<TodoDB> todoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TODO + " ORDER BY " + KEY_ID + " DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                TodoDB data = new TodoDB();
                data.setId(Long.parseLong(cursor.getString(0)));
                data.setName(cursor.getString(1));
                data.setState(cursor.getInt(2));
                todoList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (todoList.isEmpty()) {
            return 0;
        } else {
            return todoList.get(0).getId();
        }
    }

    // Updating a shop
    public int updateTodo(TodoDB data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, data.getName());
        values.put(KEY_STATE, data.getState());
        values.put(KEY_ID, data.getId());
        return db.update(TABLE_TODO, values, KEY_ID + " = ?",
                new String[]{String.valueOf(data.getId())});
    }
}
