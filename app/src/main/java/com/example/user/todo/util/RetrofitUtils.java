package com.example.user.todo.util;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Saran on 1/11/2559.
 */

public class RetrofitUtils {

    public static final String HTTP_HOST_PRODUCTION = "https://dl.dropboxusercontent.com/";

    public static Retrofit getRetrofit() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(HTTP_HOST_PRODUCTION);
        builder.client(new OkHttpClient());
        builder.addConverterFactory(GsonConverterFactory.create());

        return builder.build();
    }
}
