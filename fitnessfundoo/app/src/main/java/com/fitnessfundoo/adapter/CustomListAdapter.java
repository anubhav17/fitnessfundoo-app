package com.fitnessfundoo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fitnessfundoo.R;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.model.FitnessFacility;

import java.util.List;

/**
 * Created by Anubhav on 04-02-2016.
 */

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.MyViewHolder> {
    //private Activity activity;
    //private LayoutInflater inflater;
    private List<FitnessFacility> fitnessFacilityList;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, rating, address;
        public NetworkImageView thumbnail;
        RatingBar setRatingBar  ;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            rating = (TextView) view.findViewById(R.id.rating);
            address = (TextView) view.findViewById(R.id.address);
            thumbnail = (NetworkImageView) view.findViewById(R.id.thumbnail);
            setRatingBar = (RatingBar) view.findViewById(R.id.setRating);
        }
    }

    public CustomListAdapter(List<FitnessFacility> fitnessFacilityList) {
        this.fitnessFacilityList = fitnessFacilityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FitnessFacility facility = fitnessFacilityList.get(position);
        holder.title.setText(facility.getTitle());
        // rating

               /* Drawable progress1 = setRatingBar.getProgressDrawable();
                DrawableCompat.setTint(progress1, Color.BLUE); */
        float rating1 = (float) facility.getRating();
        holder.setRatingBar.setRating(rating1);
        holder.rating.setText(String.valueOf(facility.getRating()));
        holder.address.setText(facility.getAddress());
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        //NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.thumbnail);

        // thumbnail image
        holder.thumbnail.setImageUrl(facility.getThumbnailUrl(), imageLoader);

    }

    @Override
    public int getItemCount() {
        return fitnessFacilityList.size();
    }

 /*
    public CustomListAdapter(Activity activity, List<FitnessFacility> fitnessFacilityList) {
        this.activity = activity;
        this.fitnessFacilityList = fitnessFacilityList;

    } */
/*
    @Override
    public int getCount() {
        return fitnessFacilityList.size();
    }

    @Override
    public Object getItem(int location) {
        return fitnessFacilityList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView address = (TextView) convertView.findViewById(R.id.address);
        // getting movie data for the row
        FitnessFacility m = fitnessFacilityList.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        title.setText(m.getTitle());

        // address
        address.setText(m.getAddress());

        // rating
        rating.setText("Rating: " + String.valueOf(m.getRating()));

       // serial.setBackgroundColor(Color.parseColor(color));

        return convertView;
    }
*/
}
