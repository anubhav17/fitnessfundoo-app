package com.fitnessfundoo.SlidingMenuOptions;

/**
 * Created by Anubhav on 15-01-2016.
 */

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
        import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.fitnessfundoo.R;
import com.fitnessfundoo.adapter.CustomListAdapter;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.helper.DividerItemDecoration;
import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.model.FitnessFacility;
import com.fitnessfundoo.newActivity.FacilityPage;
import com.fitnessfundoo.www.fitnessfundoo.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class FavoriteFacilities extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public FavoriteFacilities(){}
    private View view3;
    private FitnessFacility facility;
    private Spinner facSpinner;
    private String fac,uid;
    private SQLiteHandler db;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog pDialog;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static String limit_val = "0";
    private List<FitnessFacility> facilityList = new ArrayList<FitnessFacility>();
    private String facArray[] = {"Gym","Sports Club","Swimming Pool"};
    private String tablename;
    private RecyclerView recyclerView;
    private CustomListAdapter adapterList;
    private Drawable toolbarBackgroundDrawable;
    private  Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favorite_fac, container, false);
        super.getActivity();
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setFalse();
        //Toolbar toolbar =  (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbarBackgroundDrawable = toolbar.getBackground();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        // enabling action bar app icon and behaving it as toggle button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);


        // SqLite database handler
        db = new SQLiteHandler(getActivity());
        // Fetching user details from SQLite
        HashMap<String, String> user1 = db.getUserDetails();
        uid = user1.get("uid");
        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

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
//                Toast.makeText(getActivity(), facility.getLat() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                view3 = view;
                facility = facilityList.get(position);
                registerForContextMenu(view3);
                Toast.makeText(getActivity(), facility.getTitle() + " is Long Pressed!", Toast.LENGTH_SHORT).show();

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

                                        fetchFavFacilities();
                                    }
                                }
        );

        facSpinner = (Spinner) rootView.findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, facArray);
        facSpinner.setAdapter(adapter1);
        facSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get select item
                int sid = facSpinner.getSelectedItemPosition();
                fac = facArray[sid].toString().trim();
                if (fac.equals("Gym") ) {
                    tablename = "gym";
                    limit_val = "0";
                    fetchFavFacilities();

                } else if (fac.equals("Sports Club") ) {
                    tablename = "sports_club";
                    limit_val = "0";
                    fetchFavFacilities();
                } else if (fac.equals("Swimming Pool") ) {
                    tablename = "swimming_pool";
                    limit_val = "0";
                    fetchFavFacilities();
                }
                /*
                if(tablename.equals( "gym")){

                }else if(tablename.equals( "swimming_pool")){
                    limit_val = "0";
                }else if (tablename.equals("sports_club"))
                {
                    limit_val = "0";
                } */

              /*  Toast.makeText(getActivity(), "You have selected City : " + facArray[sid],
                        Toast.LENGTH_SHORT).show(); */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return rootView;
    }


    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            pDialog.dismiss();
            Toast.makeText(getActivity(), "No Internet connection!", Toast.LENGTH_LONG).show();
            //     pDialog.setMessage("Please Connect to the Internet...");
            //       showDialog();
            return false;
        }
        return true;
    }

    @Override
    public void onRefresh ()
    {
         fetchFavFacilities();
    }


    private void fetchFavFacilities() {
        isOnline();

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        StringRequest facilityReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FAV_FACILITY, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                int limit_val1 = 0;
                //hideDialog();
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

                          //  int rank1 = obj.getInt("fitnessfacility_id");

                            int rank1 = a;
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

                Log.d("Value of tablename ",tablename);
                Log.d("Value of limit val ",limit_val);
                Log.d("value of user_id ",uid);
                params.put("tablename", tablename);
                params.put("user_id", uid);
                params.put("limit_val", limit_val);
//                params.put("city", city_name);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(facilityReq);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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
            fac = "Gym";
        } else if (tablename.equals("sports_club")) {
            fac = "SportsClub";
        } else if (tablename.equals("swimming_pool")) {
            fac = "SwimmingPool";
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/sharer/sharer.php?u=http://www.fitnessfundoo.com/" + fac + "/" + facility.getUrl()));
        startActivity(browserIntent);
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private FavoriteFacilities.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FavoriteFacilities.ClickListener clickListener) {
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


    public void onResume(){
        super.onResume();
        updateColor();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateColor() {
        toolbarBackgroundDrawable.setAlpha(255); // 0% of transparency
        toolbar.setBackground(toolbarBackgroundDrawable);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //menu.clear();
        menu.findItem(R.id.action_settings).setVisible(false).setEnabled(false);
        menu.findItem(R.id.action_home).setVisible(false).setEnabled(false);
        menu.findItem(R.id.action_search).setVisible(false).setEnabled(false);
        menu.findItem(R.id.create_event).setVisible(false).setEnabled(false);

        return;
    }

}

