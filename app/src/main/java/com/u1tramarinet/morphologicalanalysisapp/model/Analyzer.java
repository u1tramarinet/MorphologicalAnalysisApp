package com.u1tramarinet.morphologicalanalysisapp.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Analyzer {
    private final static Analyzer INSTANCE = new Analyzer();
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    private Analyzer() {

    }

    public static Analyzer getInstance() {
        return INSTANCE;
    }

    public void analyze(@NonNull String input, @NonNull Callback<Sentence> callback) {
        service.submit(new Runnable() {
            @Override
            public void run() {
                Sentence output = analyzeInternal(input);
                callback.onSuccess(output);
            }
        });
    }

    public void analyze(@NonNull List<String> inputs, @NonNull AnalyzeCallback callback) {
        service.submit(new Runnable() {
            @Override
            public void run() {
                List<Sentence> outputs = new ArrayList<>();
                int count = 1;
                int size = inputs.size();
                for (String input : inputs) {
                    callback.onProgress(count, size);
                    d("analyze() [" + count + "/" + size + "] start");
                    outputs.add(analyzeInternal(input));
                    d("analyze() [" + count + "/" + size + "] finish");
                    count++;
                }
                callback.onSuccess(outputs);
            }
        });
    }

    private Sentence analyzeInternal(String input) {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(input);
        return new Sentence(input, convert(tokens));
    }

    private List<Word> convert(List<Token> tokens) {
        List<Word> tokenList = new ArrayList<>();
        Token previewToken = null;
        for (Token token : tokens) {
            tokenList.add(new Word(token, previewToken));
            previewToken = token;
        }
        return tokenList;
    }

    private void d(String message) {
        Log.d(Analyzer.class.getSimpleName(), message);
    }

    public interface AnalyzeCallback extends Callback<List<Sentence>> {
        void onProgress(int progress, int max);
    }
}
