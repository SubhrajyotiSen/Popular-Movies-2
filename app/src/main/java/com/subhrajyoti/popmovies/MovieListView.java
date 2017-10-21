package com.subhrajyoti.popmovies;

import com.subhrajyoti.popmovies.adapters.MovieAdapter;
import com.subhrajyoti.popmovies.models.MovieModel;

public interface MovieListView {

    void setProgressBarVisibility(int visibility);

    void startMovieDetailFragment(MovieDetailFragment movieDetailFragment);

    void startMovieDetailActivity(MovieModel movieModel);

    void setMenuItemChecked(int id);

    void setCorrectAdapter(MovieAdapter movieAdapter);
}
