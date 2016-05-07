package com.subhrajyoti.popmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.subhrajyoti.popmovies.application.App;
import com.subhrajyoti.popmovies.models.MovieModel;
import com.subhrajyoti.popmovies.retrofit.MovieAPI;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class MovieListActivity extends AppCompatActivity {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    public static ProgressBar progressBar;
    MovieAdapter mainAdapter;
    MovieAdapter secondAdapter;
    ArrayList<MovieModel> popularList;
    ArrayList<MovieModel> ratedList;
    GridLayoutManager gridLayoutManager;
    boolean popular;
    private boolean mTwoPane;
    Realm realm = Realm.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);
        popularList = new ArrayList<>();
        ratedList = new ArrayList<>();
        popular = false;
        setupRecyclerView();
        if (savedInstanceState!=null)
        {
            mainAdapter.addAll(savedInstanceState.<MovieModel>getParcelableArrayList("POP"));
            secondAdapter.addAll(savedInstanceState.<MovieModel>getParcelableArrayList("RATED"));
        }
        else {
            realm.beginTransaction();
            if (isNetworkAvailable()) {
                realm.deleteAll();
                realm.commitTransaction();
                getMovies("popular");
                getMovies("top_rated");
            } else {
                parseRealm("popular");
                parseRealm("rated");
            }
        }
        if (findViewById(R.id.movie_detail_container) != null) {

            mTwoPane = true;
        }
        recyclerView.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MovieModel movieModel;
                if (!popular) {
                    movieModel = popularList.get(position);
                } else {
                    movieModel = ratedList.get(position);
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

            }
        }));

    }

    private void getMovies(final String sort) {

        final Realm realm = Realm.getDefaultInstance();
        App.getMovieClient().getMovieAPI().loadMovies(sort, BuildConfig.API_KEY).enqueue(new Callback<MovieAPI.Movies>() {

            @Override
            public void onResponse(Response<MovieAPI.Movies> response, Retrofit retrofit) {
                realm.beginTransaction();
                if (sort.equals("popular")) {
                    for (int i = 0; i < response.body().results.size(); i++) {
                        response.body().results.get(i).setTag("Popular");
                        popularList.add(response.body().results.get(i));
                        realm.copyToRealm(response.body().results.get(i));
                        Log.v(sort, response.body().results.get(i).getoriginal_title());
                    }
                    mainAdapter.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < response.body().results.size(); i++) {
                        response.body().results.get(i).setTag("Rated");
                        ratedList.add(response.body().results.get(i));
                        realm.copyToRealm(response.body().results.get(i));
                        Log.v(sort, response.body().results.get(i).getoriginal_title());
                    }
                    secondAdapter.notifyDataSetChanged();
                }
                realm.commitTransaction();

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_sort_by_popularity);
        item.setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort_by_rating:
                recyclerView.setAdapter(secondAdapter);
                popular = true;
                break;
            case R.id.action_sort_by_popularity:
                recyclerView.setAdapter(mainAdapter);
                popular = false;
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setupRecyclerView() {
        gridLayoutManager = new GridLayoutManager(this, 2);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            gridLayoutManager.setSpanCount(2);
        else
            gridLayoutManager.setSpanCount(3);
        mainAdapter = new MovieAdapter(this, popularList);
        secondAdapter = new MovieAdapter(this, ratedList);
        recyclerView.setAdapter(mainAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    public void parseRealm(String sort) {

        if (sort.equals("popular")) {
            RealmResults<MovieModel> realmResults = realm.where(MovieModel.class).contains("tag", "Popular").findAll();
            Log.d("Size", String.valueOf(realmResults.size()));
            for (int i = 0; i < realmResults.size(); i++) {
                popularList.add(realmResults.get(i));
                Log.d("pop add", realmResults.get(i).getoriginal_title());
            }
            mainAdapter.notifyDataSetChanged();

        } else {
            RealmResults<MovieModel> realmResults = realm.where(MovieModel.class).contains("tag", "Rated").findAll();
            for (int i = 0; i < realmResults.size(); i++)
                ratedList.add(realmResults.get(i));
            secondAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("POP", popularList);
        outState.putParcelableArrayList("RATED",ratedList);


    }


}
