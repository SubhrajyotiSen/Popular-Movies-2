package com.subhrajyoti.popmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.subhrajyoti.popmovies.adapters.MovieAdapter;
import com.subhrajyoti.popmovies.application.MovieApplication;
import com.subhrajyoti.popmovies.dagger.activity.list.DaggerMovieListActivityComponent;
import com.subhrajyoti.popmovies.dagger.activity.list.MovieListActivityComponent;
import com.subhrajyoti.popmovies.dagger.activity.list.MovieListModule;
import com.subhrajyoti.popmovies.dagger.app.ContextModule;
import com.subhrajyoti.popmovies.models.MovieModel;
import com.subhrajyoti.popmovies.utils.RecyclerClickListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;


public class MovieListActivity extends AppCompatActivity implements MovieListView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Inject
    MovieAdapter popularAdapter;
    @Inject
    MovieAdapter ratedAdapter;
    @Inject
    MovieAdapter favouriteAdapter;
    @Inject
    Realm realm;
    @Inject
    MovieListPresenter movieListPresenter;

    private GridLayoutManager gridLayoutManager;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        progressBar.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);

        MovieListActivityComponent movieListActivityComponent = DaggerMovieListActivityComponent.builder()
                .movieApplicationComponent(MovieApplication.get(this).getMovieApplicationComponent())
                .movieListModule(new MovieListModule(this))
                .contextModule(new ContextModule(this))
                .build();

        movieListActivityComponent.injectMovieListActivity(this);

        movieListPresenter.setupAdapter(popularAdapter, ratedAdapter, favouriteAdapter);

        movieListPresenter.handleActivityInstance(savedInstanceState);

        if (findViewById(R.id.movie_detail_container) != null) {
            movieListPresenter.setTwoPane();
        }
        setupRecyclerView();

        movieListPresenter.addRealmListener();

        movieListPresenter.registerReceiver();

        recyclerView.addOnItemTouchListener(new RecyclerClickListener(this, (view, position) -> {
            movieListPresenter.handleRecyclerItemClicked(position);
        }));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        movieListPresenter.setMenuItemChecked();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        movieListPresenter.onOptionsItemSelected(item.getItemId());
        item.setChecked(true);
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager.setSpanCount(3);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager.setSpanCount(2);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        movieListPresenter.putInBundle(outState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        movieListPresenter.unregisterReceiver();
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    @Override
    public void startMovieDetailFragment(MovieDetailFragment movieDetailFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, movieDetailFragment)
                .commit();
    }

    @Override
    public void startMovieDetailActivity(MovieModel movieModel) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movie", movieModel);
        startActivity(intent);
    }

    @Override
    public void setMenuItemChecked(int id) {
        menu.findItem(id).setChecked(true);
    }

    @Override
    public void setCorrectAdapter(MovieAdapter movieAdapter) {
        recyclerView.setAdapter(movieAdapter);
    }

    public void setupRecyclerView() {
        gridLayoutManager = new GridLayoutManager(this, 2);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            gridLayoutManager.setSpanCount(2);
        else
            gridLayoutManager.setSpanCount(3);

        movieListPresenter.setCorrectAdapter();
        recyclerView.setLayoutManager(gridLayoutManager);
    }


}
