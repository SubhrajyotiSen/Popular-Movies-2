package com.subhrajyoti.popmovies.dagger.activity.list;

import android.content.Context;

import com.subhrajyoti.popmovies.MovieListPresenter;
import com.subhrajyoti.popmovies.MovieListView;
import com.subhrajyoti.popmovies.dagger.app.activity.home.MovieListScope;
import com.subhrajyoti.popmovies.retrofit.MovieService;

import dagger.Module;
import dagger.Provides;

@MovieListScope
@Module
public class MovieListModule {

    private final MovieListView movieListView;

    public MovieListModule(MovieListView movieListView) {
        this.movieListView = movieListView;
    }

    @Provides
    @MovieListScope
    public MovieListView movieListView() {
        return movieListView;
    }

    @Provides
    @MovieListScope
    public MovieListPresenter movieListPresenter(MovieListView movieListView, MovieService movieService, Context context) {
        return new MovieListPresenter(movieListView, movieService, context);
    }


}
