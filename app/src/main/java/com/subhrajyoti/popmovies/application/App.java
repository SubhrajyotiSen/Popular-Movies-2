package com.subhrajyoti.popmovies.application;


import android.app.Application;

import com.subhrajyoti.popmovies.retrofit.MovieAPI;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {
    private static MovieAPI.MovieClient movieClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        movieClient = new MovieAPI.MovieClient();
    }

    public static MovieAPI.MovieClient getMovieClient() {
        return movieClient;
    }
}
