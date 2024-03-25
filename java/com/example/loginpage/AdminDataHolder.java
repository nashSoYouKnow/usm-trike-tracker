package com.example.loginpage;

import androidx.annotation.NonNull;

public class AdminDataHolder {

    private static AdminDataHolder instance = null;
    private String user;
    private String id;

    AdminDataHolder() {}

    @NonNull
    public static AdminDataHolder getInstance() {
        if (instance == null) {
            instance = new AdminDataHolder();
        }
        return instance;
    }

    @NonNull
    public String getUser() {
        return user;
    }

    @NonNull
    public String getId() {return id;}

    public void setUser(@NonNull String user) {
        this.user = user;
    }
    public void setId(@NonNull String id) {this.id = id;}
}
