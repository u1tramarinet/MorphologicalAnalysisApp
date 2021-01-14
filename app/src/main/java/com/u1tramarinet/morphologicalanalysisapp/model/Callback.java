package com.u1tramarinet.morphologicalanalysisapp.model;

import androidx.annotation.NonNull;

public interface Callback<T> {
    void onSuccess(@NonNull T data);
    void onFailure();
}
