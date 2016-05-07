package com.subhrajyoti.popmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.subhrajyoti.popmovies.adapters.ReviewAdapter;
import com.subhrajyoti.popmovies.adapters.TrailerAdapter;
import com.subhrajyoti.popmovies.application.App;
import com.subhrajyoti.popmovies.models.MovieModel;
import com.subhrajyoti.popmovies.models.ReviewModel;
import com.subhrajyoti.popmovies.models.TrailerModel;
import com.subhrajyoti.popmovies.retrofit.MovieAPI;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class MovieDetailFragment extends Fragment {


    public MovieDetailFragment() {
    }

    MovieModel movieModel;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.titleView)
    TextView titleView;
    @Bind(R.id.rating)
    TextView rating;
    @Bind(R.id.ratingBar)
    RatingBar ratingBar;
    @Bind(R.id.overview)
    TextView overview;
    @Bind(R.id.releaseText)
    TextView releaseText;
    @Bind(R.id.trailersRecyclerView)
    RecyclerView trailersRecyclerView;
    @Bind(R.id.reviewsRecyclerView)
    RecyclerView reviewsRecyclerView;
    ArrayList<TrailerModel> trailerList;
    ArrayList<ReviewModel> reviewList;
    ReviewAdapter reviewAdapter;
    TrailerAdapter trailerAdapter;
    Realm realm = Realm.getDefaultInstance();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments().containsKey("movie")) {

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.collapsing_toolbar);
            if (appBarLayout != null) {
                appBarLayout.setTitle("");
           }
            movieModel = getArguments().getParcelable("movie");
            assert movieModel != null;
        }
        trailerList = new ArrayList<>();
        reviewList = new ArrayList<>();
        getReviews(movieModel.getId());
        getTrailers(movieModel.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this,rootView);

        titleView.setText(movieModel.getoriginal_title());


        Picasso.with(getActivity()).load(BuildConfig.IMAGE_URL+"/w342" + movieModel.getposter_path() + "?api_key?=" + BuildConfig.API_KEY).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(imageView);

        rating.setText(Float.toString(movieModel.getvote_average()).concat("/10"));
        ratingBar.setMax(5);
        ratingBar.setRating(movieModel.getvote_average() / 2f);

        overview.setText(movieModel.getOverview());
        releaseText.setText("Release Date: ".concat(movieModel.getrelease_date()));

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(getContext());

        trailersRecyclerView.setLayoutManager(trailerLayoutManager);
        reviewsRecyclerView.setLayoutManager(reviewLayoutManager);

        reviewAdapter = new ReviewAdapter(getContext(),reviewList);
        trailerAdapter = new TrailerAdapter(getContext(),trailerList);

        trailersRecyclerView.setAdapter(trailerAdapter);
        reviewsRecyclerView.setAdapter(reviewAdapter);

        trailersRecyclerView.addOnItemTouchListener(new RecyclerClickListener(getContext(), new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String url = "https://www.youtube.com/watch?v=".concat(trailerList.get(position).getKey());
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

        }));
        return rootView;
    }

    private void getTrailers(final String id) {
        App.getMovieClient().getMovieAPI().loadTrailers(id, BuildConfig.API_KEY).enqueue(new Callback<MovieAPI.Trailers>() {
            @Override
            public void onResponse(Response<MovieAPI.Trailers> response, Retrofit retrofit) {
                Log.v("Trailers",String.valueOf(response.body().results.size()));
                for (int i = 0; i < response.body().results.size(); i++) {
                    trailerList.add(response.body().results.get(i));
                    Log.v(id, response.body().results.get(i).getKey());
                }
                trailerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Throwable t) {

                Log.v("Trailers","Fail");


            }
        });
    }

    private void getReviews(final String id) {
        App.getMovieClient().getMovieAPI().loadReviews(id, BuildConfig.API_KEY).enqueue(new Callback<MovieAPI.Reviews>() {
            @Override
            public void onResponse(Response<MovieAPI.Reviews> response, Retrofit retrofit) {
                for (int i = 0; i < response.body().results.size(); i++) {
                    reviewList.add(response.body().results.get(i));
                    Log.v(id, response.body().results.get(i).getAuthor());
                }
                trailerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.share).setVisible(true);
        MenuItem item = menu.findItem(R.id.fav);
        item.setVisible(true);
        item.setIcon(!isFavourite() ? R.drawable.fav_remove : R.drawable.fav_add);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.share:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, movieModel.getoriginal_title());
                share.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=".concat(trailerList.get(0).getKey()));
                startActivity(Intent.createChooser(share, "Share Trailer!"));
                break;


            case R.id.fav:
                if (realm.isInTransaction())
                    realm.cancelTransaction();
                if (!isFavourite()) {
                    realm.beginTransaction();
                    item.setIcon(R.drawable.fav_add);

                    realm.copyToRealm(movieModel);
                    realm.commitTransaction();

                } else {
                    realm.beginTransaction();
                    item.setIcon(R.drawable.fav_remove);
                    realm.where(MovieModel.class).contains("id", movieModel.getId()).findFirst().deleteFromRealm();
                    realm.commitTransaction();

                }
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isFavourite(){

        return realm.where(MovieModel.class).contains("id", movieModel.getId()).findAll().size() != 0;
    }




}
