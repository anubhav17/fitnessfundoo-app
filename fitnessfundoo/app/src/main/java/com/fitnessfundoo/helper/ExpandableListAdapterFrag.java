package com.fitnessfundoo.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fitnessfundoo.model.Sports;
import com.fitnessfundoo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Anubhav on 31-01-2016.
 */
public class ExpandableListAdapterFrag extends BaseExpandableListAdapter {

    private SparseBooleanArray mSelectedItemsIds;
    private Context _context;
    private List<Sports> sportsList;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title and icon id, child title
    private HashMap<String, List<Sports>> _listDataChild;
    final ArrayList<String> positionArr = new ArrayList<String>();

    public ExpandableListAdapterFrag(Context context, List<String> listDataHeader,
                                     HashMap<String, List<Sports>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }


    @Override
    public Sports getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, final ViewGroup parent) {

        final Sports childText =  getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText._title);

        ImageView imgListChild = (ImageView) convertView.findViewById(R.id.lblImageItem);

        imgListChild.setImageResource(childText._photoId);

       final CheckBox chk = (CheckBox) convertView
                .findViewById(R.id.lblChildCheckbox);

        //chk.setChecked(false);
        chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(v.getContext(), "Hi i am being checked.", Toast.LENGTH_SHORT).show();
                //         createArray(getChildId(groupPosition, childPosition));
                if(chk.isChecked()) {
                    if(!positionArr.contains(childText._title))
                    positionArr.add(childText._title);
                    //positionArr.toString();
                } else {
                    positionArr.remove(getChild(groupPosition, childPosition)._title);
                }
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public String displayString(){
    return  positionArr.toString();
    }

}