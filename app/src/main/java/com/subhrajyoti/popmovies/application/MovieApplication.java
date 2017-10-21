package com.subhrajyoti.popmovies.application;


import android.app.Activity;
import android.app.Application;

import com.subhrajyoti.popmovies.dagger.app.ContextModule;
import com.subhrajyoti.popmovies.dagger.app.DaggerMovieApplicationComponent;
import com.subhrajyoti.popmovies.dagger.app.MovieApplicationComponent;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MovieApplication extends Application {

    private MovieApplicationComponent movieApplicationComponent;

    public static MovieApplication get(Activity activity) {
        return (MovieApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);

        movieApplicationComponent = DaggerMovieApplicationComponent
                .builder()
                .contextModule(new ContextModule(this))
                .build();

    }

    public MovieApplicationComponent getMovieApplicationComponent() {
        return movieApplicationComponent;
    }

}
