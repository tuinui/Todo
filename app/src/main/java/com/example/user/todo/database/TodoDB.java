package com.example.user.todo.database;

import com.example.user.todo.flow.todo.list.model.TodoViewModel;

/**
 * Created by User on 1/11/2559.
 */

public class TodoDB {
    private long todoId;
    private String name;
    private int state;

    public TodoDB() {
    }

    public TodoDB(long todoId, String name, @TodoViewModel.State int state) {
        this.todoId = todoId;
        this.name = name;
        this.state = state;
    }

    public void setId(long todoId) {
        this.todoId = todoId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(@TodoViewModel.State int state) {
        this.state = state;
    }

    public long getId() {
        return todoId;
    }

    public String getName() {
        return name;
    }

    public @TodoViewModel.State int getState() {
        return state;
    }
}
