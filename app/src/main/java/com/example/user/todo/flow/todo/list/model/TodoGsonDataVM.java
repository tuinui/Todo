package com.example.user.todo.flow.todo.list.model;

import com.example.user.todo.database.TodoDB;

/**
 * Created by Saran on 1/11/2559.
 */

public class TodoGsonDataVM implements TodoViewModel {
    private TodoDB data;
    private long id;
    private String name;
    private
    @State
    int state;
    private boolean isChecked = false;


    public TodoGsonDataVM(TodoDB db) {
        this.data = db;
        this.id = db.getId();
        this.name = db.getName();
        this.state = parseToState(db);
    }

    private
    @State
    int parseToState(TodoDB data) {
        if (data.getState() == PENDING) {
            return PENDING;
        } else if (data.getState() == DONE) {
            return DONE;
        } else {
            return NONE;
        }
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public
    @State
    int getState() {
        return state;
    }

    @Override
    public void setChecked(boolean isCheck) {
        isChecked = isCheck;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public TodoDB getData() {
        return data;
    }
}
