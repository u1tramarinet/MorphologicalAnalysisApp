package com.u1tramarinet.morphologicalanalysisapp;

import android.app.Application;

import com.u1tramarinet.twitter_api.TwitterApi;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initializeRealm();
        initializeTwitter();
    }

    private void initializeRealm() {
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

    private void initializeTwitter() {
        TwitterApi twitterApi = TwitterApi.getInstance();
        twitterApi.initialize(this);
    }
}
