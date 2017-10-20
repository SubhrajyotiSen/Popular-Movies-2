package com.subhrajyoti.popmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.subhrajyoti.popmovies.adapters.MovieAdapter;
import com.subhrajyoti.popmovies.application.App;
import com.subhrajyoti.popmovies.models.MovieModel;
import com.subhrajyoti.popmovies.retrofit.MovieAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class MovieListActivity extends AppCompatActivity{

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    public static ProgressBar progressBar;
    MovieAdapter popularAdapter;
    MovieAdapter ratedAdapter;
    MovieAdapter favouriteAdapter;
    ArrayList<MovieModel> popularList;
    ArrayList<MovieModel> ratedList;
    ArrayList<MovieModel> favList;
    GridLayoutManager gridLayoutManager;
    private boolean mTwoPane;
    Realm realm = Realm.getDefaultInstance();
    String SORT_BY = "POPULAR";
    NetworkReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);
        progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        setSupportActionBar(toolbar);
        popularList = new ArrayList<>();
        ratedList = new ArrayList<>();
        favList = new ArrayList<>();
        popularAdapter = new MovieAdapter(this, popularList);
        ratedAdapter = new MovieAdapter(this, ratedList);
        favouriteAdapter = new MovieAdapter(this,favList);
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        networkReceiver = new NetworkReceiver();
        registerReceiver(networkReceiver,intentFilter);


        if (savedInstanceState!=null)
        {
            popularAdapter.addAll(savedInstanceState.<MovieModel>getParcelableArrayList("POP"));
            ratedAdapter.addAll(savedInstanceState.<MovieModel>getParcelableArrayList("RATED"));
            SORT_BY = savedInstanceState.getString("SORT_BY");
        }

        else {
            if (isNetworkAvailable()) {

                (new FetchMovies()).execute("popular");
                (new FetchMovies()).execute("top_rated");
            }
        }
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }
        setupRecyclerView();



        realm.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                getFavourites();
            }

        });
        recyclerView.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MovieModel movieModel;
                switch (SORT_BY) {
                    case "POPULAR":
                        movieModel = popularList.get(position);
                        break;
                    case "RATED":
                        movieModel = ratedList.get(position);
                        break;
                    default:
                        movieModel = favList.get(position);
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

            }
        }));

    }

    private class FetchMovies extends AsyncTask<String, Void,
                List<MovieModel>> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected List<MovieModel> doInBackground(String... params) {
            final String sort = params[0];
            App.getMovieClient().getMovieAPI().loadMovies(sort, BuildConfig.API_KEY).enqueue(new Callback<MovieAPI.Movies>() {

                @Override
                public void onResponse(Response<MovieAPI.Movies> response, Retrofit retrofit) {

                    if (sort.equals("popular")) {
                        for (int i = 0; i < response.body().results.size(); i++) {
                            popularList.add(response.body().results.get(i));
                            Log.v(sort, response.body().results.get(i).getoriginal_title());
                        }
                        popularAdapter.notifyDataSetChanged();
                    } else {
                        for (int i = 0; i < response.body().results.size(); i++) {
                            ratedList.add(response.body().results.get(i));
                            Log.v(sort, response.body().results.get(i).getoriginal_title());
                        }
                        ratedAdapter.notifyDataSetChanged();
                    }
                    if (mTwoPane && popularList.size()!=0) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable("movie", popularList.get(0));
                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    }

                }

                @Override
                public void onFailure(Throwable t) {

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(List<MovieModel> movieModels) {

        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        switch (SORT_BY){
            case "POPULAR" : menu.findItem(R.id.action_sort_by_popularity).setChecked(true);
                break;
            case "RATED" : menu.findItem(R.id.action_sort_by_rating).setChecked(true);
                break;
            case "FAVOURITE" : menu.findItem(R.id.action_sort_by_favourite).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort_by_rating:
                recyclerView.setAdapter(ratedAdapter);
                SORT_BY = "RATED";
                break;
            case R.id.action_sort_by_popularity:
                recyclerView.setAdapter(popularAdapter);
                SORT_BY = "POPULAR";
                break;
            case R.id.action_sort_by_favourite:
                recyclerView.setAdapter(favouriteAdapter);
                SORT_BY = "FAVOURITE";
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setupRecyclerView() {
        gridLayoutManager = new GridLayoutManager(this, 2);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            gridLayoutManager.setSpanCount(2);
        else
            gridLayoutManager.setSpanCount(3);

        switch (SORT_BY){
            case "POPULAR" : recyclerView.setAdapter(popularAdapter);
                break;
            case "RATED" : recyclerView.setAdapter(ratedAdapter);
                break;
            case "FAVOURITE" : recyclerView.setAdapter(favouriteAdapter);
        }

        recyclerView.setLayoutManager(gridLayoutManager);
    }

    public void getFavourites() {
            RealmResults<MovieModel> realmResults = realm.where(MovieModel.class).findAll();
            Log.d("Size", String.valueOf(realmResults.size()));
            favList.clear();
            for (int i = 0; i < realmResults.size(); i++) {
                favList.add(realmResults.get(i));
                Log.d("fav add", realmResults.get(i).getoriginal_title());
            }
            favouriteAdapter.notifyDataSetChanged();
        Log.d("Array Size",String.valueOf(favList.size()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("POP", popularList);
        outState.putParcelableArrayList("RATED",ratedList);
        outState.putString("SORT_BY",SORT_BY);


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
                if (isNetworkAvailable() && popularList.size()!=0) {
                    (new FetchMovies()).execute("popular");
                    (new FetchMovies()).execute("top_rated");
                }
            Toast.makeText(getApplicationContext(), "Network chnaged", Toast.LENGTH_SHORT).show();
        }
    }

}
