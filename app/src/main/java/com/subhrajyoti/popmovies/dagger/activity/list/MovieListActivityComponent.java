package com.subhrajyoti.popmovies.dagger.activity.list;

import com.subhrajyoti.popmovies.MovieListActivity;
import com.subhrajyoti.popmovies.dagger.app.ContextModule;
import com.subhrajyoti.popmovies.dagger.app.MovieApplicationComponent;
import com.subhrajyoti.popmovies.dagger.app.activity.home.MovieListScope;

import dagger.Component;

@MovieListScope
@Component(modules = {MovieListModule.class, ContextModule.class}, dependencies = MovieApplicationComponent.class)
public interface MovieListActivityComponent {

    void injectMovieListActivity(MovieListActivity movieListActivity);

}
