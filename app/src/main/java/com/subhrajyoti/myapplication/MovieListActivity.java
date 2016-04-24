package com.subhrajyoti.myapplication;

import android.content.Intent;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.subhrajyoti.myapplication.Retrofit.MovieAPI;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class MovieListActivity extends AppCompatActivity {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    MainAdapter mainAdapter;
    MainAdapter secondAdapter;
    ArrayList<MovieModel> popularList;
    ArrayList<MovieModel> ratedList;
    GridLayoutManager gridLayoutManager;
    MaterialDialog dialog;
    boolean popular;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        popularList = new ArrayList<>();
        ratedList = new ArrayList<>();
        popular=false;
        gridLayoutManager = new GridLayoutManager(this,2);

        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT)
            gridLayoutManager.setSpanCount(2);
        else
            gridLayoutManager.setSpanCount(3);
        mainAdapter = new MainAdapter(this,popularList);
        secondAdapter = new MainAdapter(this,ratedList);
        recyclerView.setAdapter(mainAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        displayDialog();
        getMovies("popular");
        getMovies("top_rated");
        if (findViewById(R.id.movie_detail_container) != null) {

            mTwoPane = true;
        }
        recyclerView.addOnItemTouchListener(new RecyclerClickListener(this,new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MovieModel movieModel;
                if (!popular){
                    movieModel = popularList.get(position);
                }
                else {
                    movieModel = ratedList.get(position);
                }

                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable("movie",movieModel);
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

    private void getMovies(final String sort){


        App.getMovieClient().getMovieAPI().loadMovies(sort,BuildConfig.API_KEY).enqueue(new Callback<MovieAPI.Movies>() {

            @Override
            public void onResponse(Response<MovieAPI.Movies> response, Retrofit retrofit) {
                if (sort.equals("popular")) {
                    for (int i = 0; i < response.body().results.size(); i++) {
                        popularList.add(response.body().results.get(i));
                        Log.v(sort, response.body().results.get(i).getoriginal_title());
                    }
                    mainAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else {
                    for (int i = 0; i < response.body().results.size(); i++) {
                        ratedList.add(response.body().results.get(i));
                        Log.v(sort, response.body().results.get(i).getoriginal_title());
                    }
                    secondAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }

    public void displayDialog(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .content("Fetching Movies").progress(true, 0);

        dialog = builder.build();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item =  menu.findItem(R.id.action_sort_by_popularity);
        item.setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.action_sort_by_rating:
                recyclerView.setAdapter(secondAdapter);
                popular=true;
                break;
            case R.id.action_sort_by_popularity:
                recyclerView.setAdapter(mainAdapter);
                popular=false;
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


}
