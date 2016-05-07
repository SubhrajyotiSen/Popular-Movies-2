package com.subhrajyoti.popmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.subhrajyoti.popmovies.R;
import com.subhrajyoti.popmovies.models.TrailerModel;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<TrailerModel> data = new ArrayList<>();


    public TrailerAdapter(Context context, ArrayList<TrailerModel> data) {
        this.context = context;
        this.data = data;
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

        String id = data.get(position).getKey();
        String thumbnailURL = "http://img.youtube.com/vi/".concat(id).concat("/hqdefault.jpg");
        Picasso.with(context).load(thumbnailURL).placeholder(R.drawable.thumbnail).into(((MyItemHolder) holder).imageView);



    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView imageView;


        public MyItemHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.trailerImage);
        }

    }


}