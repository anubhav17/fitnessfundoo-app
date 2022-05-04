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
import com.fitnessfundoo.model.SportsEvent;

import java.util.List;

/**
 * Created by Anubhav on 04-02-2016.
 */
public class EventListNearByAdapter extends RecyclerView.Adapter<EventListNearByAdapter.MyViewHolder> {

    private List<SportsEvent> eventList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date, address,distance_tv;
        public NetworkImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            address = (TextView) view.findViewById(R.id.address);
            thumbnail = (NetworkImageView) view.findViewById(R.id.thumbnail);
            distance_tv = (TextView) view.findViewById(R.id.distance);
        }
    }

    public EventListNearByAdapter(List<SportsEvent> eventList) {
        this.eventList = eventList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_row_near_by, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SportsEvent event = eventList.get(position);
        holder.title.setText(event.getTitle());
        // date
        holder.date.setText("Date: " + String.valueOf(event.getEventDate()));
        holder.address.setText(event.getAddress());
        holder.distance_tv.setText(event.getDistance() + " K.M.");
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        // thumbnail image
        holder.thumbnail.setImageUrl(event.getThumbnailUrl(), imageLoader);

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

 /*   public EventListAdapter(Activity activity, List<SportsEvent> eventList) {
        this.activity = activity;
        this.eventList = eventList;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int location) {
        return eventList.get(location);
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
            convertView = inflater.inflate(R.layout.list_event_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView address = (TextView) convertView.findViewById(R.id.address);
        // getting movie data for the row
        SportsEvent m = eventList.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        title.setText(m.getTitle());

        // address
        address.setText(m.getAddress());

        // rating
        date.setText("Date: " + String.valueOf(m.getEventDate()));


        return convertView;
    }
*/
}
