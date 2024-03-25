package com.example.loginpage;

public interface Callback<T> {
    void onSuccess(T result);
    void onFailure(String errorMessage);
}
