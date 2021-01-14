package com.u1tramarinet.twitter_api;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.services.StatusesService;
import com.u1tramarinet.twitter_api.model.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import retrofit2.Call;

public class TwitterApi {
    private static final TwitterApi INSTANCE = new TwitterApi();

    private TwitterApi() {

    }

    public static TwitterApi getInstance() {
        return INSTANCE;
    }

    public boolean needLogin() {
        return (TwitterCore.getInstance().getSessionManager().getActiveSession() == null);
    }

    public void initialize(@NonNull Context context) {
        String consumeKey = BuildConfig.CONSUMER_KEY;
        String consumeSecret = BuildConfig.CONSUMER_SECRET;
        TwitterConfig config = new TwitterConfig.Builder(context)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(consumeKey, consumeSecret))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    public void getHomeTimeline(@NonNull com.u1tramarinet.twitter_api.Callback<List<Tweet>> callback) {
        StatusesService service = getTweetService();
        Call<List<com.twitter.sdk.android.core.models.Tweet>> call = service.homeTimeline(200, null, null, false, false, true, true);
        call.enqueue(new Callback<List<com.twitter.sdk.android.core.models.Tweet>>() {
            @Override
            public void success(Result<List<com.twitter.sdk.android.core.models.Tweet>> result) {
                callback.onSuccess(convertTweets(result.data));
            }

            @Override
            public void failure(TwitterException exception) {
                callback.onFailure();
            }
        });
    }

    public void postTweet(@NonNull String text, @NonNull com.u1tramarinet.twitter_api.Callback<Tweet> callback) {
        StatusesService service = getTweetService();
        Call<com.twitter.sdk.android.core.models.Tweet> call = service.update(text, null, null, null, null, null, null, false, null);
        call.enqueue(new Callback<com.twitter.sdk.android.core.models.Tweet>() {
            @Override
            public void success(Result<com.twitter.sdk.android.core.models.Tweet> result) {
                callback.onSuccess(convertTweet(result.data));
            }

            @Override
            public void failure(TwitterException exception) {
                callback.onFailure();
            }
        });
    }

    private StatusesService getTweetService() {
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        TwitterApiClient client = new TwitterApiClient(session);
        return client.getStatusesService();
    }

    @NonNull
    private Tweet convertTweet(@NonNull com.twitter.sdk.android.core.models.Tweet in) {
        return new Tweet(in.id, in.text);
    }

    @NonNull
    private List<Tweet> convertTweets(@NonNull List<com.twitter.sdk.android.core.models.Tweet> in) {
        List<Tweet> out = new ArrayList<>();
        for (com.twitter.sdk.android.core.models.Tweet tweet : in) {
            out.add(convertTweet(tweet));
        }
        return out;
    }
}
