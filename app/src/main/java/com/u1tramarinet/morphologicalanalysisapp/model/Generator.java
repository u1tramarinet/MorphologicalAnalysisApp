package com.u1tramarinet.morphologicalanalysisapp.model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Generator {
    private static final Generator INSTANCE = new Generator();
    private final ExecutorService service = Executors.newSingleThreadExecutor();
    private static final String PERIOD = "。";
    private static final String INDENTION = "\n";

    private Generator() {

    }

    public static Generator getInstance() {
        return INSTANCE;
    }

    public void generate(@NonNull List<Sentence> inputs, @NonNull Callback<Sentence> callback) {
        service.submit(new Runnable() {
            @Override
            public void run() {
                Sentence input = inputs.get(0);
                for (int i = 1; i < inputs.size(); i++) {
                    input.append(inputs.get(i));
                }
                Sentence output = generateInternal(input);
                callback.onSuccess(output);
            }
        });
    }

    public void generate(@NonNull Sentence input, @NonNull Callback<Sentence>callback) {
        service.submit(new Runnable() {
            @Override
            public void run() {
                Sentence output = generateInternal(input);
                callback.onSuccess(output);
            }
        });
    }

    private Sentence generateInternal(@NonNull Sentence input){
        Word firstWord = input.words.get(0);
        String value = firstWord.getPreviewValue();
        String feature = firstWord.getPreviewFeature();
        Sentence output = new Sentence();

        while (true) {
            List<Integer> indexList = getIndexList(input.words, value, feature);
            int size = indexList.size();
            d("prevVal=" + value + ", prevFeat=" + feature + ", size=" + size);
            if ((size == 0) || (compare(value, PERIOD)) || (value.contains(INDENTION))) break;

            int i = indexList.get((int)(Math.random() * size));
            Word w = input.words.get(i);
            w.setUsed(true);
            value = w.getValue();
            feature = w.getFeature();
            output.append(w);
        }
        return output;
    }

    private List<Integer> getIndexList(@NonNull List<Word> words, @NonNull String value, @NonNull String feature) {
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            Word word = words.get(i);
            if (compare(word.getPreviewValue(), value) && compare(word.getPreviewFeature(), feature) && !word.isUsed()) {
                indexList.add(i);
            }
        }
        return indexList;
    }

    private boolean compare(@NonNull String string1, @NonNull String string2) {
        return (string1.compareTo(string2) == 0);
    }

    private void d(@NonNull String message) {
        Log.d(Generator.class.getSimpleName(), message);
    }
}
