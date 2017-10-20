package com.subhrajyoti.popmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.subhrajyoti.popmovies.R;
import com.subhrajyoti.popmovies.models.MovieModel;
import com.subhrajyoti.popmovies.utils.URLUtils;

import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import io.realm.RealmResults;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Picasso picasso;
    private ArrayList<MovieModel> data = new ArrayList<>();

    @Inject
    MovieAdapter(Picasso picasso) {
        this.picasso = picasso;
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

        String imageURL = URLUtils.makeImageURL(data.get(position).getposter_path());
        picasso.load(imageURL).into(((MyItemHolder) holder).imageView);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addAll(ArrayList<MovieModel> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(RealmResults<MovieModel> realmResults) {
        Iterator<MovieModel> iterator = realmResults.iterator();
        data.clear();
        while (iterator.hasNext())
            data.add(iterator.next());
        notifyDataSetChanged();
    }

    public MovieModel get(int position) {
        return data.get(position);
    }

    public ArrayList<MovieModel> getData() {
        return data;
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView imageView;


        MyItemHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.listImage);
        }

    }

}