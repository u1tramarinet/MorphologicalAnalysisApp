package com.u1tramarinet.twitter_api;

public interface Callback<T> {
    void onSuccess(T data);
    void onFailure();
}
