package com.subhrajyoti.popmovies.dagger.component;

import com.subhrajyoti.popmovies.MovieDetailFragment;
import com.subhrajyoti.popmovies.MovieListActivity;
import com.subhrajyoti.popmovies.dagger.scope.MovieActivityScope;

import dagger.Component;

@MovieActivityScope
@Component(dependencies = MovieApplicationComponent.class)
public interface MovieActivityComponent {

    void injectMovieListActivity(MovieListActivity movieListActivity);

    void injectMovieDetailsFragment(MovieDetailFragment movieDetailFragment);

}
