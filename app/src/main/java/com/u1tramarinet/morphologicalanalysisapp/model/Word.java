package com.u1tramarinet.morphologicalanalysisapp.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.atilika.kuromoji.ipadic.Token;

public class Word {
    @NonNull
    private final String value;
    @NonNull
    private final String feature;
    @NonNull
    private final String previewValue;
    @NonNull
    private final String previewFeature;
    private boolean used;
    public Word(@NonNull Token token, @Nullable Token previewToken) {
        this.value = token.getSurface();
        this.feature = token.getAllFeaturesArray()[0];
        this.previewValue = (previewToken == null) ? "" : previewToken.getSurface();
        this.previewFeature = (previewToken == null) ? "" : previewToken.getAllFeaturesArray()[0];
    }

    @NonNull
    public String getValue() {
        return value;
    }

    @NonNull
    public String getFeature() {
        return feature;
    }

    @NonNull
    public String getPreviewValue() {
        return previewValue;
    }

    @NonNull
    public String getPreviewFeature() {
        return previewFeature;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isUsed() {
        return used;
    }
}
