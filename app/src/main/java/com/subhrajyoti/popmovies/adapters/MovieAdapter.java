package com.subhrajyoti.popmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.subhrajyoti.popmovies.BuildConfig;
import com.subhrajyoti.popmovies.MovieListActivity;
import com.subhrajyoti.popmovies.R;
import com.subhrajyoti.popmovies.models.MovieModel;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<MovieModel> data = new ArrayList<>();


    public MovieAdapter(Context context, ArrayList<MovieModel> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.grid_image, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        String imageURL = BuildConfig.IMAGE_URL+"/w342" + data.get(position).getposter_path() + "?api_key?=" + BuildConfig.API_KEY;
        Picasso.with(context).load(imageURL).into(((MyItemHolder) holder).imageView, new Callback() {
            @Override
            public void onSuccess() {
                MovieListActivity.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView imageView;


        public MyItemHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.listImage);
        }

    }

    public void addAll(ArrayList<MovieModel> list){
        for (int i = 0; i < list.size(); i++)
        data.add(list.get(i));
    }

}