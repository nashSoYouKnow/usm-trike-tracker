package com.example.loginpage;

import androidx.annotation.NonNull;

public class DataHolder {
    private static DataHolder instance = null;
    private String id;

    DataHolder() {}

    @NonNull
    public static DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }
}

