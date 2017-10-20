package com.subhrajyoti.popmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.subhrajyoti.popmovies.R;
import com.subhrajyoti.popmovies.models.ReviewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ReviewModel> data = new ArrayList<>();


    public ReviewAdapter(ArrayList<ReviewModel> data) {
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.review_list_item, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ((MyItemHolder) holder).authorText.setText(data.get(position).getAuthor());
        ((MyItemHolder) holder).reviewText.setText(data.get(position).getcontent());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MyItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.authorText)
        TextView authorText;
        @BindView(R.id.reviewText)
        TextView reviewText;


        MyItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

    }


}