package com.example.user.todo.flow.todo.main;

import com.example.user.todo.base.BasePresenter;
import com.example.user.todo.base.BaseView;
import com.example.user.todo.database.TodoDB;
import com.example.user.todo.flow.todo.list.model.TodoViewModel;

import java.util.List;

/**
 * Created by Saran on 1/11/2559.
 */

public interface TodoActivityContract {
    public interface View extends BaseView<Presenter> {
        void showLoadingIndicator(boolean active);
        void showTodoListData(List<TodoViewModel> todoGsons);

    }


    public interface Presenter extends BasePresenter {
        void loadData(boolean forceUpdate);
        void deleteDataFromDb(List<Long> id);
        void addDataToDb(String todoName);

        void markDbAsDone(TodoDB data);
    }

}
