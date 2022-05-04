package com.fitnessfundoo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fitnessfundoo.R;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.model.SearchResult;
import com.fitnessfundoo.model.SportsEvent;

import java.util.List;

/**
 * Created by Anubhav on 04-02-2016.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MyViewHolder> {

    private List<SearchResult> searchList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date, address;
        public NetworkImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            address = (TextView) view.findViewById(R.id.address);
            thumbnail = (NetworkImageView) view.findViewById(R.id.thumbnail);
        }
    }

    public SearchListAdapter(List<SearchResult> searchList) {
        this.searchList = searchList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_event_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SearchResult result = searchList.get(position);
        holder.title.setText(result.getTitle());
        // date
        holder.date.setText("Date: " + String.valueOf(result.getEventDate()));
        holder.address.setText(result.getAddress());
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        // thumbnail image
        holder.thumbnail.setImageUrl(result.getThumbnailUrl(), imageLoader);

    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

}
