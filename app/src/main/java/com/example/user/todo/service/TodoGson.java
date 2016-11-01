package com.example.user.todo.service;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saran on 1/11/2559.
 */

public class TodoGson {
    /*
       "datas":[
      {
         "id":1,
         "name":"First task",
         "State": 1
      },
      {
         "id":2,
         "name":"Second task",
         "State": 0
      },
      {
         "id":3,
         "name":"Third task",
         "State": 1
      }
   ]
     */
    @SerializedName("data")
    private List<Data> datas = new ArrayList<>();

    public TodoGson() {
    }

    public List<Data> getDatas() {
        return datas;
    }

    public static class Data {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("State")
        private int state;

        public Data() {
        }



        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getState() {
            return state;
        }
    }

}
