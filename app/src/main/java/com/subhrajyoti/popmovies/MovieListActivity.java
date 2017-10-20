package com.subhrajyoti.popmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.subhrajyoti.popmovies.adapters.MovieAdapter;
import com.subhrajyoti.popmovies.application.MovieApplication;
import com.subhrajyoti.popmovies.dagger.component.DaggerMovieActivityComponent;
import com.subhrajyoti.popmovies.dagger.component.MovieActivityComponent;
import com.subhrajyoti.popmovies.models.MovieModel;
import com.subhrajyoti.popmovies.retrofit.MovieService;
import com.subhrajyoti.popmovies.utils.NetworkUtils;
import com.subhrajyoti.popmovies.utils.RecyclerClickListener;
import com.subhrajyoti.popmovies.utils.SortType;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.subhrajyoti.popmovies.utils.SortType.FAVOURITE;
import static com.subhrajyoti.popmovies.utils.SortType.POPULAR;
import static com.subhrajyoti.popmovies.utils.SortType.RATED;


public class MovieListActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Inject
    MovieAdapter popularAdapter;
    @Inject
    MovieAdapter ratedAdapter;
    @Inject
    MovieAdapter favouriteAdapter;
    @Inject
    MovieService movieService;
    @Inject
    Realm realm;
    private ProgressBar progressBar;
    private GridLayoutManager gridLayoutManager;
    private boolean mTwoPane;
    private SortType SORT_BY = POPULAR;
    private NetworkReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);
        progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        setSupportActionBar(toolbar);

        MovieActivityComponent movieActivityComponent = DaggerMovieActivityComponent.builder()
                .movieApplicationComponent(MovieApplication.get(this).getMovieApplicationComponent())
                .build();

        movieActivityComponent.injectMovieListActivity(this);

        Log.i("TAG", popularAdapter + "");
        Log.i("TAG", ratedAdapter + "");
        Log.i("TAG", favouriteAdapter + "");


        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        networkReceiver = new NetworkReceiver();
        registerReceiver(networkReceiver,intentFilter);


        if (savedInstanceState!=null)
        {
            popularAdapter.addAll(savedInstanceState.getParcelableArrayList("POP"));
            ratedAdapter.addAll(savedInstanceState.getParcelableArrayList("RATED"));
            getFavourites();
            SORT_BY = (SortType) savedInstanceState.get("SORT_BY");
            progressBar.setVisibility(View.INVISIBLE);
        }

        else {
            if (NetworkUtils.isNetworkAvailable(this)) {

                fetchMovies("popular");
                fetchMovies("top_rated");
            }
        }

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }
        setupRecyclerView();

        realm.addChangeListener(realm -> getFavourites());


        recyclerView.addOnItemTouchListener(new RecyclerClickListener(this, (view, position) -> {
            MovieModel movieModel;
            switch (SORT_BY) {
                case POPULAR:
                    movieModel = popularAdapter.get(position);
                    break;
                case RATED:
                    movieModel = ratedAdapter.get(position);
                    break;
                case FAVOURITE:
                    movieModel = favouriteAdapter.get(position);
                    break;
                default:
                    movieModel = null;
                    break;
            }

            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putParcelable("movie", movieModel);
                MovieDetailFragment fragment = new MovieDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment)
                        .commit();
            } else {

                Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
                intent.putExtra("movie", movieModel);

                startActivity(intent);
            }

        }));

    }

    private void fetchMovies(final String sort) {
        movieService
                .loadMovies(sort, BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (sort.equals("popular")) {
                        popularAdapter.addAll(movies.results);
                    } else {
                        ratedAdapter.addAll(movies.results);
                    }
                    if (mTwoPane && popularAdapter.getItemCount() != 0) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable("movie", popularAdapter.get(0));
                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        switch (SORT_BY){
            case POPULAR:
                menu.findItem(R.id.action_sort_by_popularity).setChecked(true);
                break;
            case RATED:
                menu.findItem(R.id.action_sort_by_rating).setChecked(true);
                break;
            case FAVOURITE:
                menu.findItem(R.id.action_sort_by_favourite).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_sort_by_favourite)
            progressBar.setVisibility(View.INVISIBLE);
        else if ((id == R.id.action_sort_by_popularity) || (id == R.id.action_sort_by_rating)) {
            if (popularAdapter.getItemCount() == 0 && ratedAdapter.getItemCount() == 0)
                progressBar.setVisibility(View.VISIBLE);
        }

        switch (id) {
            case R.id.action_sort_by_rating:
                recyclerView.setAdapter(ratedAdapter);
                SORT_BY = RATED;
                break;
            case R.id.action_sort_by_popularity:
                recyclerView.setAdapter(popularAdapter);
                SORT_BY = POPULAR;
                break;
            case R.id.action_sort_by_favourite:
                recyclerView.setAdapter(favouriteAdapter);
                SORT_BY = FAVOURITE;
                getFavourites();
                break;


        }
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

    public void setupRecyclerView() {
        gridLayoutManager = new GridLayoutManager(this, 2);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            gridLayoutManager.setSpanCount(2);
        else
            gridLayoutManager.setSpanCount(3);

        switch (SORT_BY){
            case POPULAR:
                recyclerView.setAdapter(popularAdapter);
                break;
            case RATED:
                recyclerView.setAdapter(ratedAdapter);
                break;
            case FAVOURITE:
                recyclerView.setAdapter(favouriteAdapter);
        }

        recyclerView.setLayoutManager(gridLayoutManager);
    }

    public void getFavourites() {
        RealmResults<MovieModel> realmResults = realm.where(MovieModel.class).findAll();
        favouriteAdapter.addAll(realmResults);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("POP", popularAdapter.getData());
        outState.putParcelableArrayList("RATED", ratedAdapter.getData());
        outState.putSerializable("SORT_BY", SORT_BY);


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkReceiver);
    }

    public class NetworkReceiver extends BroadcastReceiver {
        public NetworkReceiver(){

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtils.isNetworkAvailable(context) && popularAdapter.getItemCount() == 0) {
                fetchMovies("popular");
                fetchMovies("top_rated");
            }
        }
    }

}
