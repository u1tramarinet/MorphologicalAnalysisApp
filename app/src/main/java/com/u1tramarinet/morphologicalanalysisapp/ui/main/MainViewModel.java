package com.u1tramarinet.morphologicalanalysisapp.ui.main;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.u1tramarinet.morphologicalanalysisapp.model.Analyzer;
import com.u1tramarinet.morphologicalanalysisapp.model.Generator;
import com.u1tramarinet.morphologicalanalysisapp.model.Sentence;
import com.u1tramarinet.morphologicalanalysisapp.model.Word;
import com.u1tramarinet.twitter_api.Callback;
import com.u1tramarinet.twitter_api.TwitterApi;
import com.u1tramarinet.twitter_api.model.Tweet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class MainViewModel extends ViewModel {
    @NonNull
    private final MutableLiveData<String> inputData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<String> outputAnalyzeData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<String> outputGenerateData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<String> errorData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<String> progressData = new MutableLiveData<>();
    @NonNull
    private final MainObservable observable;
    @NonNull
    private final Analyzer analyzer;
    @NonNull
    private final Generator generator;
    @NonNull
    private final TwitterApi twitterApi;
    @NonNull
    private final Set<Tweet> tweets = new HashSet<>();
    @NonNull
    private final Set<Sentence> sentences = new HashSet<>();
    @NonNull
    private final Calendar calendar = Calendar.getInstance();

    public MainViewModel() {
        observable = new MainObservable(new MainObservable.Callback() {
            @Override
            public void onTextChanged(String text) {
                d("onTextChanged() text=" + text);
                inputData.postValue(text);
            }
        });
        analyzer = Analyzer.getInstance();
        generator = Generator.getInstance();
        twitterApi = TwitterApi.getInstance();
    }

    @NonNull
    public MainObservable getObservable() {
        return observable;
    }

    @NonNull
    public LiveData<String> getOutputAnalyze() {
        return outputAnalyzeData;
    }

    @NonNull
    public LiveData<String> getOutputGenerate() {
        return outputGenerateData;
    }

    @NonNull
    public LiveData<String> getError() {
        return errorData;
    }

    @NonNull
    public LiveData<String> getProgress() {
        return progressData;
    }

    public void onClickExecuteButtonFromInput() {
        d("onClickAnalyzeButton()");
        String input = inputData.getValue();
        if (input != null) {
            analyzeAndGenerateSentence(input);
        }
    }

    public void onClickExecuteButtonFromTwitter() {
        onClickClearButton();
        if (tweets.isEmpty() || calendar.before(Calendar.getInstance())) {
            twitterApi.getHomeTimeline(new Callback<List<Tweet>>() {
                @Override
                public void onSuccess(List<Tweet> data) {
                    tweets.addAll(data);
                    generateSentenceFromTweets();
                }

                @Override
                public void onFailure() {
                    errorData.postValue("Failed to get tweets");
                }
            });
            calendar.add(Calendar.MINUTE, 5);
        } else {
            generateSentenceFromTweets();
        }
    }

    public void onClickClearButton() {
        d("onClickClearButton()");
        inputData.postValue("");
        outputAnalyzeData.postValue("");
        outputGenerateData.postValue("");
    }

    public void onClickShareButton() {
        String text = outputGenerateData.getValue();
        if (text == null || text.length() == 0) {
            errorData.postValue("No text to post tweet.");
            return;
        } else if (text.length() > 140) {
            text = text.substring(0, 139);
        }
        twitterApi.postTweet(text, new Callback<Tweet>() {
            @Override
            public void onSuccess(Tweet data) {

            }

            @Override
            public void onFailure() {
                errorData.postValue("Failed to post tweet");
            }
        });
    }

    private void analyzeAndGenerateSentences(@NonNull List<String> inputs) {
        progressData.postValue("Analyzing....");
        analyzer.analyze(inputs, new Analyzer.AnalyzeCallback() {
            @Override
            public void onProgress(int progress, int max) {
                progressData.postValue("Analyzing...(" + progress + "/" + max + ")");
            }

            @Override
            public void onSuccess(@NonNull List<Sentence> data) {
                d("onSuccess() data=" + data.size());
                sentences.addAll(data);

                Sentence output = new Sentence();
                for (Sentence sentence : sentences) {
                    output.append(sentence);
                }
                d("onSuccess() result=" + output.fullText);
                printAnalyzeSentence(output);
                progressData.postValue("");
                generateSentence(output);
            }

            @Override
            public void onFailure() {
                progressData.postValue("");
                errorData.postValue("Failed to analyze...");
            }
        });
    }

    private void analyzeAndGenerateSentence(@NonNull String input) {
        progressData.postValue("Analyzing....");
        analyzer.analyze(input, new com.u1tramarinet.morphologicalanalysisapp.model.Callback<Sentence>() {
            @Override
            public void onSuccess(@NonNull Sentence data) {
                printAnalyzeSentence(data);
                progressData.postValue("");
                generateSentence(data);
            }

            @Override
            public void onFailure() {
                progressData.postValue("");
                errorData.postValue("Failed to analyze...");
            }
        });
    }

    private void printAnalyzeSentence(Sentence sentence) {
        outputAnalyzeData.postValue(sentence.toString());
    }

    private void generateSentence(Sentence sentence) {
        progressData.postValue("Generating...");
        generator.generate(sentence, new com.u1tramarinet.morphologicalanalysisapp.model.Callback<Sentence>() {
            @Override
            public void onSuccess(@NonNull Sentence data) {
                d("onSuccess() data=" + data.fullText);
                printGenerateSentence(data);
                progressData.postValue("");
            }

            @Override
            public void onFailure() {
                progressData.postValue("");
                errorData.postValue("Failed to generate...");
            }
        });
    }

    private void printGenerateSentence(Sentence sentence) {
        outputGenerateData.postValue(sentence.fullText);
    }

    private void generateSentenceFromTweets() {
        List<String> texts = new ArrayList<>();
        for (Tweet tweet : tweets) {
            if (tweet.text.contains("@")) {
                continue;
            }
            String text = excludeWordsStartingWith(tweet.text, "http", "t.co", "#");
//            text = text.replaceAll("\n", "");
            if (isNotAnalyzed(text)) {
                texts.add(text);
            }
        }
        analyzeAndGenerateSentences(texts);
    }

    private String excludeWordsStartingWith(String text, String... keys) {
        d("excludeWordsStartingWith() text=" + text + ", key=" + Arrays.toString(keys));
        String input = text;
        for (String key : keys) {
            input = excludeWordsStartingWith(input, key);
        }
        d("excludeWordsStartingWith() result=" + input);
        return input;
    }

    private String excludeWordsStartingWith(String text, String key) {
        String[] parts = text.split(key);
        if (parts.length == 1) return text;
        StringBuilder builder = new StringBuilder();
        builder.append(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            int firstBlankIndex = part.indexOf(" ");
            int firstIndentionIndex = part.indexOf("\n");
            if (firstBlankIndex > 0) {
                part = part.substring(firstBlankIndex + 1);
            } else if (firstIndentionIndex > 0) {
                part = part.substring(firstIndentionIndex + 1);
            } else {
                continue;
            }
            builder.append(part);
        }
        return builder.toString();
    }

    private boolean isNotAnalyzed(String text) {
        for (Sentence sentence : sentences) {
            if (TextUtils.equals(sentence.fullText, text)) {
                return false;
            }
        }
        return true;
    }

    private void d(String message) {
        Log.d(MainViewModel.class.getSimpleName(), message);
    }

    public static class MainObservable extends BaseObservable {
        @NonNull
        private final Callback callback;
        @Bindable
        private String input;
        private MainObservable(@NonNull Callback callback) {
            this.callback = callback;
        }

        public String getInput() {
            return this.input;
        }

        public void setInput(String input) {
            d("setInput() input=" + input);
            if (!TextUtils.equals(this.input, input)) {
                this.input = input;
                callback.onTextChanged(input);
            }
        }

        interface Callback {
            void onTextChanged(String text);
        }

        private void d(String message) {
            Log.d(MainObservable.class.getSimpleName(), message);
        }
    }
}