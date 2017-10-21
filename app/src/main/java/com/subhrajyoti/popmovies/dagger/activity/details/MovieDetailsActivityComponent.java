package com.subhrajyoti.popmovies.dagger.activity.details;

import com.subhrajyoti.popmovies.MovieDetailActivity;
import com.subhrajyoti.popmovies.MovieDetailFragment;
import com.subhrajyoti.popmovies.dagger.app.MovieApplicationComponent;

import dagger.Component;

@MovieDetailsScope
@Component(dependencies = MovieApplicationComponent.class)
public interface MovieDetailsActivityComponent {

    void injectMovieDetailsActivity(MovieDetailActivity movieDetailActivity);

    void injectMovieDetailsFragment(MovieDetailFragment movieDetailFragment);
}
