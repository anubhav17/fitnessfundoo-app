package com.fitnessfundoo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.view.menu.MenuItemWrapperICS;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fitnessfundoo.R;
import com.fitnessfundoo.model.InterestLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anubhav on 15-04-2016.
 */

public class InterestLevelAdapterEditable extends BaseAdapter {

    private Activity activity;
    private  TextView level;
    private InterestLevel m;
    private InterestLevel m1;
    private LayoutInflater inflater;
    private List<InterestLevel> interestLevelList;
    private ArrayList<String> levelArr = new ArrayList<String>();

    public InterestLevelAdapterEditable(Activity activity, List<InterestLevel> interestLevelList) {
        this.activity = activity;
        this.interestLevelList = interestLevelList;
    }

    @Override
    public int getCount() {
        return interestLevelList.size();
    }

    @Override
    public InterestLevel getItem(int location) {
        return interestLevelList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final InterestLevel lv = getItem(position);

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.interest_level_editable, null);


        TextView sports_name = (TextView) convertView.findViewById(R.id.sportsName);

        m = interestLevelList.get(position);

        sports_name.setText(m.getSports_name());

        level = (TextView) convertView.findViewById(R.id.level);
        level.setText(m.getLevel());
        if (!(levelArr.contains(lv.getSports_name() + ":" + lv.getLevel())))
        levelArr.add(interestLevelList.get(position).getSports_name() + ":" + interestLevelList.get(position).getLevel());

        Log.d("lavel1", levelArr.toString());

        Log.isLoggable("initial level ", this.getCount());

        Log.isLoggable("position ", position);
        //Creating the instance of PopupMenu
        final PopupMenu popup = new PopupMenu(activity, level);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_level, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                lv.setLevel(item.getTitle().toString());
                interestLevelList.remove(position);
                interestLevelList.add(position, lv);
                notifyDataSetChanged();
                levelArr.remove(position);
                levelArr.add(position,lv.getSports_name() + ":" + lv.getLevel());
                //levelArr.add(lv.getSports_name() + ":" + lv.getLevel());

                //m1 = interestLevelList.get(position);
             //   level.setText(getItem(position).getLevel());
                return true;
            }
        });



        level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();//showing popup menu
            }
        });

        return convertView;
    }

    public String displayString(){
        return  levelArr.toString();
    }

}