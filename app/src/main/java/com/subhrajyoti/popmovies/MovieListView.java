package com.subhrajyoti.popmovies;

import com.subhrajyoti.popmovies.models.MovieModel;

import java.util.ArrayList;

public interface MovieListView {

    void setProgressBarVisibility(int visibility);

    void startMovieDetailFragment(MovieDetailFragment movieDetailFragment);

    void startMovieDetailActivity(MovieModel movieModel);

    void setMenuItemChecked(int id);

    void showMovies(ArrayList<MovieModel> movieModels);

}
