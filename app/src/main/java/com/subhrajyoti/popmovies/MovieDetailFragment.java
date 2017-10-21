package com.subhrajyoti.popmovies;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.subhrajyoti.popmovies.adapters.ReviewAdapter;
import com.subhrajyoti.popmovies.adapters.TrailerAdapter;
import com.subhrajyoti.popmovies.application.MovieApplication;
import com.subhrajyoti.popmovies.dagger.activity.details.DaggerMovieDetailsActivityComponent;
import com.subhrajyoti.popmovies.dagger.activity.details.MovieDetailsActivityComponent;
import com.subhrajyoti.popmovies.models.MovieModel;
import com.subhrajyoti.popmovies.retrofit.MovieService;
import com.subhrajyoti.popmovies.utils.Constants;
import com.subhrajyoti.popmovies.utils.NetworkUtils;
import com.subhrajyoti.popmovies.utils.RecyclerClickListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;


public class MovieDetailFragment extends Fragment {


    MovieModel movieModel;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.titleView)
    TextView titleView;
    @BindView(R.id.rating)
    TextView rating;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.overview)
    TextView overview;
    @BindView(R.id.releaseText)
    TextView releaseText;
    @BindView(R.id.trailersRecyclerView)
    RecyclerView trailersRecyclerView;
    @BindView(R.id.reviewsRecyclerView)
    RecyclerView reviewsRecyclerView;
    @BindView(R.id.noReviewView)
    TextView noReviewView;
    @BindView(R.id.noTrailerView)
    TextView noTrailerView;
    @BindView(R.id.extras)
    LinearLayout extraLayout;
    @Inject
    ReviewAdapter reviewAdapter;
    @Inject
    TrailerAdapter trailerAdapter;
    @Inject
    Picasso picasso;
    @Inject
    Realm realm;
    @Inject
    MovieService movieService;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments().containsKey("movie")) {

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.collapsing_toolbar);
            if (appBarLayout != null) {
                appBarLayout.setTitle("");
            }
            movieModel = getArguments().getParcelable("movie");
            assert movieModel != null;
        }

        MovieDetailsActivityComponent movieDetailsActivityComponent = DaggerMovieDetailsActivityComponent.builder()
                .movieApplicationComponent(MovieApplication.get(getActivity()).getMovieApplicationComponent())
                .build();

        movieDetailsActivityComponent.injectMovieDetailsFragment(this);

        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            fetchReviews(movieModel.getId());
            fetchTrailers(movieModel.getId());
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this,rootView);

        titleView.setText(movieModel.getoriginal_title());

        picasso.load(Constants.IMAGE_URL + "/w342" + movieModel.getposter_path() + "?api_key?=" + BuildConfig.API_KEY).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(imageView);

        rating.setText(Float.toString(movieModel.getvote_average()).concat("/10"));
        ratingBar.setMax(5);
        ratingBar.setRating(movieModel.getvote_average() / 2f);

        overview.setText(movieModel.getOverview());
        releaseText.setText("Release Date: ".concat(movieModel.getrelease_date()));

        if (!NetworkUtils.isNetworkAvailable(getActivity()))
            extraLayout.setVisibility(View.INVISIBLE);

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(getContext());

        trailersRecyclerView.setLayoutManager(trailerLayoutManager);
        reviewsRecyclerView.setLayoutManager(reviewLayoutManager);

        trailersRecyclerView.setAdapter(trailerAdapter);
        reviewsRecyclerView.setAdapter(reviewAdapter);

        trailersRecyclerView.addOnItemTouchListener(new RecyclerClickListener(getContext(), (view, position) -> {
            String url = "https://www.youtube.com/watch?v=".concat(trailerAdapter.get(position).getKey());
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }));

        reviewsRecyclerView.addOnItemTouchListener(new RecyclerClickListener(getContext(), (view, position) -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(reviewAdapter.get(position).getUrl()));
            startActivity(i);
        }));
        return rootView;
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
                share.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=".concat(trailerAdapter.get(0).getKey()));
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

    private void fetchReviews(final String id) {
        movieService.loadReviews(id, BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reviews -> {
                    reviewAdapter.addAll(reviews.results);
                    if (reviewAdapter.getData().isEmpty()) {
                        reviewsRecyclerView.setVisibility(View.INVISIBLE);
                        noReviewView.setVisibility(View.VISIBLE);
                    }
                });

    }

    private void fetchTrailers(final String id) {
        movieService.loadTrailers(id, BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trailers -> {
                    trailerAdapter.addAll(trailers.results);
                    if (trailerAdapter.getData().isEmpty()) {
                        trailersRecyclerView.setVisibility(View.INVISIBLE);
                        noTrailerView.setVisibility(View.VISIBLE);
                    }
                });

    }
}
