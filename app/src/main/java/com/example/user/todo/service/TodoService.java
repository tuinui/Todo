package com.example.user.todo.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Saran on 1/11/2559.
 */

public interface TodoService {
    //    https://dl.dropboxusercontent.com/u/6890301/tasks.json
    @GET("u/{userId}/tasks.json")
    Call<TodoGson> getTodoData(@Path("userId") String userId);
}
