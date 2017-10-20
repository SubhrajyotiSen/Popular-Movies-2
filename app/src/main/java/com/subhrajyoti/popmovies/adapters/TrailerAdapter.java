package com.subhrajyoti.popmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.subhrajyoti.popmovies.R;
import com.subhrajyoti.popmovies.models.TrailerModel;
import com.subhrajyoti.popmovies.utils.URLUtils;

import java.util.ArrayList;

import javax.inject.Inject;

public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<TrailerModel> data;
    private Picasso picasso;

    @Inject
    TrailerAdapter(Picasso picasso) {
        this.picasso = picasso;
        this.data = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.trailer_list_item, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        String thumbnailURL = URLUtils.makeThumbnailURL(data.get(position).getKey());
        picasso.load(thumbnailURL).placeholder(R.drawable.thumbnail).into(((MyItemHolder) holder).imageView);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addAll(ArrayList<TrailerModel> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public TrailerModel get(int position) {
        return data.get(position);
    }

    public ArrayList<TrailerModel> getData() {
        return data;
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView imageView;


        MyItemHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.trailerImage);
        }

    }


}