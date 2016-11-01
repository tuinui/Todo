package com.example.user.todo.flow.todo.list.model;

import android.support.annotation.IntDef;

import com.example.user.todo.database.TodoDB;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Saran on 1/11/2559.
 */

public interface TodoViewModel {
//    @SerializedName("id")
//    private int id;
//    @SerializedName("name")
//    private String name;
//    @SerializedName("State")
//    private int State;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PENDING, DONE, NONE})
    public @interface State {
    }

    public int PENDING = 0;
    public int DONE = 1;
    public int NONE = -404;


    long getId();

    String getName();

    @State
    int getState();

    void setChecked(boolean isCheck);

    boolean isChecked();

    TodoDB getData();
}
