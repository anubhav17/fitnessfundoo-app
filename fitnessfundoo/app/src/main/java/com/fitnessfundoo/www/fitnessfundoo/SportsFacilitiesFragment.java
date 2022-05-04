package com.fitnessfundoo.www.fitnessfundoo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.fitnessfundoo.newActivity.EditEvent;
import com.fitnessfundoo.newActivity.FacilityPage;
import com.fitnessfundoo.R;
import com.fitnessfundoo.adapter.CustomListAdapter;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.model.FitnessFacility;
import android.widget.AdapterView.OnItemSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fitnessfundoo.helper.*;

import static android.widget.Toast.*;

/**
 * Created by Anubhav on 12-01-2016.
 */
public class SportsFacilitiesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View view3;
    private String fac_name;
    private static String limit_val = "15";
    private FitnessFacility facility;
    private static final String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog pDialog;
    private List<FitnessFacility> facilityList = new ArrayList<FitnessFacility>();
    private ListView listView;
    private RecyclerView recyclerView;
    private CustomListAdapter adapterList;
    public static   String table = "";
    public static   String tablename = "gym";
    public static   String city_name = "";
    int rank ;
    // used to store app title
    private CharSequence mTitle;
    private Drawable toolbarBackgroundDrawable;
    private  Toolbar toolbar;

    JSONArray jsonArray = null;
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.facilities_fragment, container, false);
        super.getActivity();
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setFalse();

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbarBackgroundDrawable = toolbar.getBackground();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        // enabling action bar app icon and behaving it as toggle button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        adapterList = new CustomListAdapter(facilityList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterList);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                FitnessFacility facility = facilityList.get(position);
                Intent intent = new Intent(getActivity(),FacilityPage.class);
                intent.putExtra("tname", tablename);
                intent.putExtra("fid", facility.getId());
                intent.putExtra("title", facility.getTitle());
                intent.putExtra("address", facility.getAddress());
                intent.putExtra("rating", facility.getRating());
                intent.putExtra("imgae_url", facility.getThumbnailUrl());
                intent.putExtra("lat", facility.getLat());
                intent.putExtra("lon", facility.getLon());
                intent.putExtra("city", facility.getCity());
                intent.putExtra("contact", facility.getContact());
                intent.putExtra("sunOpen", facility.getSunOpen());
                intent.putExtra("parking", facility.getParking());
                intent.putExtra("persTraining", facility.getPersTraining());
                intent.putExtra("lockerShower", facility.getLockerShower());
                intent.putExtra("freeTrial", facility.getFreeTrial());
                intent.putExtra("open_time", facility.getOpen_time());
                intent.putExtra("close_time", facility.getClose_time());
                intent.putExtra("url", facility.getUrl());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                view3 = view;
                facility = facilityList.get(position);
                registerForContextMenu(view3);
             //   Toast.makeText(getActivity(), facility.getTitle() + " is Long Pressed!", Toast.LENGTH_SHORT).show();

            }
        }));

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        fetchFacilities();
                                    }
                                }
        );

        Spinner spinner_city = (Spinner) rootView.findViewById(R.id.select_city);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.select_city, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_city.setAdapter(adapter);
        spinner_city.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                city_name = parent.getItemAtPosition(position).toString();
                selectFacilities(tablename,city_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        city_name = spinner_city.getSelectedItem().toString();

        Spinner spinner_table = (Spinner) rootView.findViewById(R.id.table_name);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(),
                R.array.table_name, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_table.setAdapter(adapter1);
        spinner_table.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // parent.getItemAtPosition(pos)
                table = parent.getItemAtPosition(position).toString();
                if (table.equals("Gym") ) {
                    tablename = "gym";
                } else if (table.equals("Sports Club") ) {
                    tablename = "sports_club";
                } else if (table.equals("Swimming Pool") ) {
                    tablename = "swimming_pool";
                }
                selectFacilities(tablename,city_name);
                // Showing selected spinner item
            //    makeText(parent.getContext(), "Selected facility: " + table, LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
                makeText(parent.getContext(), "Nothing Selected ", LENGTH_LONG).show();

            }
        });
        table = spinner_table.getSelectedItem().toString();

        return rootView;
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            pDialog.dismiss();
            Toast.makeText(getContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            //     pDialog.setMessage("Please Connect to the Internet...");
            //       showDialog();
            return false;
        }
        return true;
    }

    @Override
    public void onRefresh () {
        fetchFacilities();
    }


    private void fetchFacilities() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        StringRequest facilityReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FACILITY, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                int limit_val1 = 0;
                // Parsing json
                try {
                    Double rating;
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        for (int a = 0; a <= 14; a++) {
                            FitnessFacility facility = new FitnessFacility();
                            JSONObject obj = jObj.getJSONObject(String.valueOf(a));
                            Log.d(TAG, obj.toString());
                            int rank1 = obj.getInt("fitnessfacility_id");

                            JSONObject jsonArray = obj.getJSONObject("fitnessfacility");

                            // updating offset value to highest value

                            if (rank1 >= limit_val1)
                                limit_val1 = rank1;
                            limit_val = String.valueOf(limit_val1) ;

                            //rank = obj.getInt("fitnessfacility_id");
                            facility.setId(obj.getString("fitnessfacility_id"));

                            facility.setTitle(jsonArray.getString("title"));
                            facility.setThumbnailUrl(jsonArray.getString("image"));
                            facility.setAddress(jsonArray.getString("address"));


                            if (jsonArray.getString("rating") == "null") {
                                rating = 0.0;
                            } else {
                                rating = jsonArray.getDouble("rating");
                            }

                            facility.setRating(rating);
                            facility.setLat(jsonArray.getString("lat"));
                            facility.setLon(jsonArray.getString("lon"));
                            facility.setContact(jsonArray.getString("contact_no"));
                            facility.setSunOpen(jsonArray.getString("Sunday_Open"));
                            facility.setParking(jsonArray.getString("Parking"));
                            facility.setFreeTrial(jsonArray.getString("Free_Trial"));
                            facility.setCity(jsonArray.getString("city"));
                            facility.setLockerShower(jsonArray.getString("Locker_n_Shower"));
                            facility.setPersTraining(jsonArray.getString("Personnel_Training"));
                            facility.setOpen_time(jsonArray.getString("open_time"));
                            facility.setClose_time(jsonArray.getString("close_time"));
                            facility.setUrl(jsonArray.getString("url"));
                            // adding facility to fitnessfacility array
                            facilityList.add(0,facility);
                        }


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        makeText(getActivity(),
                                errorMsg, LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // notifying list adapter about data changes
                // so that it renders the list view with updated data
                adapterList.notifyDataSetChanged();
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tablename", tablename);
                params.put("limit_val", limit_val);
                params.put("city", city_name);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(facilityReq);
    }


    private void selectFacilities(final String tablename,final String city_name) {

        final String limit_value = "0";
       // pDialog.setMessage("Loading...");

        //showDialog();
        isOnline();
        StringRequest facilityReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FACILITY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                hideDialog();
                facilityList.clear();
                // Parsing json
                try {
                    Double rating;
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        for (int a = 0; a <= 14; a++) {
                            FitnessFacility facility = new FitnessFacility();
                            JSONObject obj = jObj.getJSONObject(String.valueOf(a));
                            Log.d(TAG, obj.toString());
                            JSONObject jsonArray = obj.getJSONObject("fitnessfacility");
                            facility.setId(obj.getString("fitnessfacility_id"));
                            facility.setTitle(jsonArray.getString("title"));
                            facility.setThumbnailUrl(jsonArray.getString("image"));
                            facility.setAddress(jsonArray.getString("address"));

                            if (jsonArray.getString("rating") == "null") {
                                rating = 0.0;
                            } else {
                                rating = jsonArray.getDouble("rating");
                            }

                            facility.setRating(rating);
                            facility.setLat(jsonArray.getString("lat"));
                            facility.setLon(jsonArray.getString("lon"));
                            facility.setContact(jsonArray.getString("contact_no"));
                            facility.setSunOpen(jsonArray.getString("Sunday_Open"));
                            facility.setParking(jsonArray.getString("Parking"));
                            facility.setFreeTrial(jsonArray.getString("Free_Trial"));
                            facility.setCity(jsonArray.getString("city"));
                            facility.setLockerShower(jsonArray.getString("Locker_n_Shower"));
                            facility.setPersTraining(jsonArray.getString("Personnel_Training"));
                            facility.setOpen_time(jsonArray.getString("open_time"));
                            facility.setClose_time(jsonArray.getString("close_time"));
                            facility.setUrl(jsonArray.getString("url"));
                            // adding facility to fitnessfacility array
                            facilityList.add(0,facility);
                        }


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        makeText(getActivity(),
                                errorMsg, LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // notifying list adapter about data changes
                // so that it renders the list view with updated data
                adapterList.notifyDataSetChanged();
         //       hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tablename", tablename);
                params.put("limit_val", limit_value);
                params.put("city", city_name);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(facilityReq);


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
            menu.setHeaderTitle(facility.getTitle());
            menu.add(1, v.getId(), 1, "Share on FaceBook");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()=="Share on FaceBook"){function1(item.getItemId());}
        else {return false;}
        return true;
    }

    public void function1(int id){
        if (tablename.equals("gym")) {
            fac_name = "Gym";
        } else if (tablename.equals("sports_club")) {
            fac_name = "SportsClub";
        } else if (tablename.equals("swimming_pool")) {
            fac_name = "SwimmingPool";
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.in/sharer/sharer.php?u=http://www.fitnessfundoo.in/" + fac_name + "/" + facility.getUrl()));
        startActivity(browserIntent);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    } */
public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector gestureDetector;
    private SportsFacilitiesFragment.ClickListener clickListener;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final SportsFacilitiesFragment.ClickListener clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, rv.getChildPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}

}
