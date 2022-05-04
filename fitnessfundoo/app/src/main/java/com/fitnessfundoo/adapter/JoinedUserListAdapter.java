package com.fitnessfundoo.adapter;

import android.app.Activity;
import android.content.Context;
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
import com.fitnessfundoo.model.JoinedUser;
import com.fitnessfundoo.model.RatingReview;

import java.util.List;

/**
 * Created by Anubhav on 20-02-2016.
 */
public class JoinedUserListAdapter extends RecyclerView.Adapter<JoinedUserListAdapter.MyViewHolder>  {

    //    private Activity activity;
//    private LayoutInflater inflater;
    private List<JoinedUser> joinedUsers;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, city, state,country;
        public NetworkImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            city = (TextView) view.findViewById(R.id.city);
            state = (TextView) view.findViewById(R.id.uState);
            country = (TextView) view.findViewById(R.id.uCountry);
            thumbnail = (NetworkImageView) view.findViewById(R.id.thumbnail);
        }
    }

    public JoinedUserListAdapter(List<JoinedUser> joinedUsers) {
        this.joinedUsers = joinedUsers;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.joined_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JoinedUser user = joinedUsers.get(position);
        holder.title.setText(user.getTitle());
        // date
        holder.city.setText( String.valueOf(user.getCity()));
        holder.state.setText(user.getState());
        holder.country.setText(user.getCountry());
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        // thumbnail image
        holder.thumbnail.setImageUrl(user.getThumbnailUrl(), imageLoader);

    }

    @Override
    public int getItemCount() {
        return joinedUsers.size();
    }
/*
    private Activity activity;
    private LayoutInflater inflater;
    private List<JoinedUser> joinedUsers;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public JoinedUserListAdapter(Activity activity, List<JoinedUser> joinedUsers) {
        this.activity = activity;
        this.joinedUsers = joinedUsers;
    }

    @Override
    public int getCount() {
        return joinedUsers.size();
    }

    @Override
    public Object getItem(int location) {
        return joinedUsers.get(location);
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
            convertView = inflater.inflate(R.layout.joined_user, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView city = (TextView) convertView.findViewById(R.id.city);
        TextView state = (TextView) convertView.findViewById(R.id.uState);
        TextView country = (TextView) convertView.findViewById(R.id.uCountry);

        // getting movie data for the row
        JoinedUser m = joinedUsers.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        title.setText(m.getTitle());

        // rating
        city.setText("City: " + String.valueOf(m.getCity()));

        // rating
        state.setText( m.getState());

        country.setText( m.getCountry());

        return convertView;
    }
 */
}