package com.u1tramarinet.morphologicalanalysisapp.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Sentence {
    @NonNull
    public String fullText;
    @NonNull
    public List<Word> words;

    public Sentence() {
        this("", new ArrayList<>());
    }

    public Sentence(@NonNull String fullText, @NonNull List<Word> words) {
        this.fullText = fullText;
        this.words = words;
    }

    public void append(@NonNull Sentence sentence) {
        for (Word word : sentence.words) {
            append(word);
        }
    }

    public void append(@NonNull Word word) {
        fullText += word.getValue();
        words.add(word);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("/", Sentence.class.getSimpleName() + "[", "]");
        for (Word word : words) {
            joiner.add(String.format("%s(%s)", word.getValue(), word.getFeature()));
        }
        return joiner.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sentence sentence = (Sentence) o;
        return fullText.equals(sentence.fullText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullText);
    }
}
