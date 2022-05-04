package com.fitnessfundoo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fitnessfundoo.R;
import com.fitnessfundoo.model.InterestLevel;

import java.util.List;

/**
 * Created by Anubhav on 20-02-2016.
 */
public class InterestLevelAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<InterestLevel> interestLevelList;

    public InterestLevelAdapter(Activity activity, List<InterestLevel> interestLevelList) {
        this.activity = activity;
        this.interestLevelList = interestLevelList;
    }

    @Override
    public int getCount() {
        return interestLevelList.size();
    }

    @Override
    public Object getItem(int location) {
        return interestLevelList.get(location);
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
            convertView = inflater.inflate(R.layout.interest_level, null);


        TextView sports_name = (TextView) convertView.findViewById(R.id.sportsName);
        TextView level = (TextView) convertView.findViewById(R.id.level);

        InterestLevel m = interestLevelList.get(position);

        sports_name.setText(m.getSports_name());

        level.setText(m.getLevel());


        return convertView;
    }

}