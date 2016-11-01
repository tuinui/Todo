package com.example.user.todo.flow.todo.main;

import com.example.user.todo.database.MyDbHelper;
import com.example.user.todo.database.TodoDB;
import com.example.user.todo.flow.todo.list.model.TodoGsonDataVM;
import com.example.user.todo.flow.todo.list.model.TodoViewModel;
import com.example.user.todo.service.TodoGson;
import com.example.user.todo.service.TodoService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Saran on 1/11/2559.
 */

public class TodoActivityPresenter implements TodoActivityContract.Presenter {

    private final TodoService mService;
    private final String mUserId;
    private final TodoActivityContract.View mView;
    private final MyDbHelper mDbHelper;

    public TodoActivityPresenter(TodoActivityContract.View view, TodoService service, String userId, MyDbHelper dbHelper) {
        this.mService = service;
        this.mUserId = userId;
        this.mView = view;
        this.mDbHelper = dbHelper;
        mView.setPresenter(this);
    }

    @Override
    public void loadData(boolean remote) {
        mView.showLoadingIndicator(true);
        if (remote) {
            mService.getTodoData(mUserId).enqueue(new Callback<TodoGson>() {
                @Override
                public void onResponse(Call<TodoGson> call, Response<TodoGson> response) {
                    if (mView.isActive()) {
                        if (response.isSuccessful()) {
                            List<TodoGson.Data> datas = response.body().getDatas();
                            onLoadDataSuccess(datas);
                        } else {
                            loadAndShowData();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TodoGson> call, Throwable t) {
                    loadAndShowData();
                }
            });
        } else {
            loadAndShowData();
        }

    }

    @Override
    public void deleteDataFromDb(List<Long> ids) {
        mView.showLoadingIndicator(true);
        deleteData(ids);
    }

    @Override
    public void addDataToDb(String todoName) {
        mView.showLoadingIndicator(true);
        saveDataToDbAndReload(new TodoDB(mDbHelper.getLatestId() + 1, todoName, TodoViewModel.PENDING));
    }

    @Override
    public void markDbAsDone(TodoDB data) {
        mView.showLoadingIndicator(true);
        updateDataAndReload(data);
    }

    private void updateDataToDbAsDone(TodoDB data) {
        data.setState(TodoViewModel.DONE);
        mDbHelper.updateTodo(data);
    }

    private void updateDataAndReload(final TodoDB data) {
        rx.Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                updateDataToDbAsDone(data);
                return null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Void>() {
            @Override
            public void call(Void datas) {
                loadAndShowData();
            }
        });
    }

    private void onLoadDataSuccess(final List<TodoGson.Data> serviceDatas) {
        rx.Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                saveServiceData(serviceDatas);
                return null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Void>() {
            @Override
            public void call(Void datas) {
                loadAndShowData();
            }
        });
    }

    private void deleteData(final List<Long> ids) {
        rx.Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mDbHelper.deleteTodo(ids);
                return null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                loadAndShowData();
            }
        });
    }

    private void loadAndShowData() {
        rx.Observable.fromCallable(new Callable<List<TodoViewModel>>() {
            @Override
            public List<TodoViewModel> call() throws Exception {
                return getDatasFromDb();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<TodoViewModel>>() {
            @Override
            public void call(List<TodoViewModel> datas) {
                setData(datas);
            }
        });
    }

    public void setData(List<TodoViewModel> datas) {
        if (mView.isActive()) {
            mView.showLoadingIndicator(false);
            mView.showTodoListData(datas);
        }
    }


    private List<TodoViewModel> getDatasFromDb() {
        List<TodoDB> dbs = mDbHelper.getAllTodoList();
        List<TodoViewModel> models = new ArrayList<>();
        for (TodoDB db : dbs) {
            models.add(new TodoGsonDataVM(db));
        }
        return models;
    }

    private void saveServiceData(List<TodoGson.Data> todoGsons) {
        for (TodoGson.Data data : todoGsons) {
            @TodoViewModel.State int state = TodoViewModel.NONE;
            if (data.getState() == TodoViewModel.PENDING) {
                state = TodoViewModel.PENDING;
            } else if (data.getState() == TodoViewModel.DONE) {
                state = TodoViewModel.DONE;
            }

            if (state != TodoViewModel.NONE) {
                saveDataToDbAndReload(new TodoDB(data.getId(), data.getName(), state));
            }
        }
    }

    private void saveDataToDbAndReload(final TodoDB todo) {
        rx.Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mDbHelper.addData(todo);
                return null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                loadAndShowData();
            }
        });
    }

    @Override
    public void start() {
        loadData(true);
    }
}
