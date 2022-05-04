package com.fitnessfundoo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fitnessfundoo.R;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.model.RatingReview;

import java.util.List;

/**
 * Created by Anubhav on 20-02-2016.
 */
public class RatingListAdapter extends RecyclerView.Adapter<RatingListAdapter.MyViewHolder> {

    //private Activity activity;
   // private LayoutInflater inflater;
    private List<RatingReview> ratingReview;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, rating,review;
        public NetworkImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            rating = (TextView) view.findViewById(R.id.rating);
            review = (TextView) view.findViewById(R.id.review);
            thumbnail = (NetworkImageView) view.findViewById(R.id.thumbnail);
        }
    }

    public RatingListAdapter(List<RatingReview> ratingReview) {
        this.ratingReview = ratingReview;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rating_review, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        RatingReview user = ratingReview.get(position);
        holder.title.setText(user.getTitle());
        // date
        holder.rating.setText("Rating: " + String.valueOf(user.getRating()));
        holder.review.setText(user.getReview());
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        // thumbnail image
        holder.thumbnail.setImageUrl(user.getThumbnailUrl(), imageLoader);

    }

    @Override
    public int getItemCount() {
        return ratingReview.size();
    }

}
