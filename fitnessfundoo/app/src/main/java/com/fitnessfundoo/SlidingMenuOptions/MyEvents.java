package com.fitnessfundoo.SlidingMenuOptions;

/**
 * Created by Anubhav on 15-01-2016.
 */

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
        import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.fitnessfundoo.R;
import com.fitnessfundoo.adapter.EventListAdapter;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.helper.DividerItemDecoration;
import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.model.SportsEvent;
import com.fitnessfundoo.newActivity.EditEvent;
import com.fitnessfundoo.newActivity.EventPage;
import com.fitnessfundoo.www.fitnessfundoo.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class MyEvents extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public MyEvents(){}
    private String limit_val = "0",uid;
    private static final String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog pDialog;
    private List<SportsEvent> eventList = new ArrayList<SportsEvent>();
    private ListView listView;
    private SQLiteHandler db;
    private EventListAdapter adapterList;
    private RecyclerView recyclerView;
    private SportsEvent  event;
    private  String dist_val1;
    private View view1,view2;
    private Drawable toolbarBackgroundDrawable;
    private  Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_events, container, false);
        super.getActivity();
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setFalse();
        // Toolbar toolbar =  (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbarBackgroundDrawable = toolbar.getBackground();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        // enabling action bar app icon and behaving it as toggle button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);


        // SQLite database handler
        db = new SQLiteHandler(getActivity());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view1);
        adapterList = new EventListAdapter(eventList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterList);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                SportsEvent event = eventList.get(position);
                Intent intent = new Intent(getActivity(), EventPage.class);
                intent.putExtra("id", event.getEventId());
                intent.putExtra("title", event.getTitle());
                intent.putExtra("address", event.getAddress());
                intent.putExtra("imgae", event.getThumbnailUrl());
                intent.putExtra("event_date", event.getEventDate());
                intent.putExtra("lat", event.getLat());
                intent.putExtra("lon", event.getLon());
                intent.putExtra("contact", event.getContact_no());
                intent.putExtra("host_id", event.getEvent_host_id());
                intent.putExtra("category", event.getCat());
                intent.putExtra("cntc_no_visible", event.getNumber_visible());
                intent.putExtra("country", event.getCountry());
                intent.putExtra("street", event.getStreet());
                intent.putExtra("city", event.getCity());
                intent.putExtra("pin", event.getPin());
                intent.putExtra("start_time", event.getStartTime());
                intent.putExtra("end_time", event.getEndTime());
                intent.putExtra("description", event.getDesc());
                intent.putExtra("email", event.getEmailId());
                intent.putExtra("date_created", event.getDate_created());
                intent.putExtra("colony", event.getColony());
                intent.putExtra("state", event.getState());
                intent.putExtra("no_participants", event.getNumberParticipant());
                intent.putExtra("url", event.getUrl());
                intent.putExtra("email_visible", event.getEmail_visible());
                startActivity(intent);

                Toast.makeText(getActivity(), event.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, final int position) {
                event = eventList.get(position);

                if (event.getEvent_host_id().equals(uid)) {
                    view1 = view;
                    registerForContextMenu(view1);
                } else {
                    view2 = view;
                    registerForContextMenu(view2);
                }

                Toast.makeText(getActivity(), event.getTitle() + " is Long Pressed!", Toast.LENGTH_SHORT).show();

            }
        }));

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

                                        //                                getNearByEvent();
                                        getMyEvent();
                                    }
                                }
        );


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
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if( v.equals(view1) ){
            menu.setHeaderTitle(event.getTitle());
            menu.add(0, v.getId(), 0, "Edit Event");
            menu.add(0, v.getId(), 0, "Delete Event");
            menu.add(0, v.getId(), 0, "Share On Facebook");
        }else if ( v.equals(view2) ) {
            menu.setHeaderTitle(event.getTitle());
            menu.add(0, v.getId(), 0, "Share On Facebook");
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()=="Edit Event"){function1(item.getItemId());}
        else if(item.getTitle()=="Delete Event"){function2(item.getItemId());}
        else if(item.getTitle()=="Share On Facebook"){function3(item.getItemId());}
        else {return false;}
        return true;
    }

    public void function1(int id){
        Intent intent = new Intent(getActivity(),EditEvent.class);
        intent.putExtra("id", event.getEventId());
        intent.putExtra("title", event.getTitle());
        intent.putExtra("address", event.getAddress());
        intent.putExtra("imgae", event.getThumbnailUrl());
        intent.putExtra("event_date", event.getEventDate());
        intent.putExtra("lat", event.getLat());
        intent.putExtra("lon", event.getLon());
        intent.putExtra("contact", event.getContact_no());
        intent.putExtra("host_id", event.getEvent_host_id());
        intent.putExtra("category", event.getCat());
        intent.putExtra("cntc_no_visible", event.getNumber_visible());
        intent.putExtra("country", event.getCountry());
        intent.putExtra("street", event.getStreet());
        intent.putExtra("city", event.getCity());
        intent.putExtra("pin", event.getPin());
        intent.putExtra("start_time", event.getStartTime());
        intent.putExtra("end_time", event.getEndTime());
        intent.putExtra("description", event.getDesc());
        intent.putExtra("email", event.getEmailId());
        intent.putExtra("date_created", event.getDate_created());
        intent.putExtra("colony", event.getColony());
        intent.putExtra("state", event.getState());
        intent.putExtra("no_participants", event.getNumberParticipant());
        intent.putExtra("url", event.getUrl());
        intent.putExtra("email_visible", event.getEmail_visible());

        startActivity(intent);

    }
    public void function2(int id){
        //popup window code here
        AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());

        // set the message to display
        alertbox.setMessage("Do you want to delete this Event!");

        // add a neutral button to the alert box and assign a click listener
        alertbox.setNeutralButton("Yes", new DialogInterface.OnClickListener() {

            // click listener on the alert box
            public void onClick(DialogInterface arg0, int arg1) {
                // the button was clicked
                deleteEvent(event.getEventId());
                Toast.makeText(getActivity(), event.getEventId() + " is id of event. ", Toast.LENGTH_SHORT).show();
            }
        })
                // Button Cancel
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // show it
        alertbox.show();


    }

    public void function3(int id){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/sharer/sharer.php?u=http://www.fitnessfundoo.com/Events/" + event.getUrl()));
        startActivity(browserIntent);
    }

    @Override
    public void onRefresh() {
        getMyEvent();
    }


    private void getMyEvent() {

        isOnline();
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        StringRequest eventReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOAD_MY_EVENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Response ", response);
                // int limit_val1 = 0;
                eventList.clear();
                // Parsing json
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        for (int a = 0; a <= jObj.length(); a++) {
                            SportsEvent event = new SportsEvent();
                            JSONObject obj = jObj.getJSONObject(String.valueOf(a));
                            //int rank = obj.getInt("sportsevents_id");
                            event.setEventId(obj.getString("sportsevents_id"));
                            JSONObject jsonArray = obj.getJSONObject("sportsevents");

                            // updating offset value to highest value
                       /*     if (rank >= limit_val1)
                                limit_val1 = rank;
                            limit_val = String.valueOf(limit_val1) ; */
                            event.setTitle(jsonArray.getString("title"));
                            event.setThumbnailUrl(jsonArray.getString("image"));
                            event.setAddress(jsonArray.getString("address"));

                            event.setEventDate(jsonArray.getString("event_date"));

                            event.setLat(jsonArray.getString("lat"));
                            event.setLon(jsonArray.getString("lon"));
                            event.setEvent_host_id(jsonArray.getString("event_host"));
                            event.setCat(jsonArray.getString("category"));
                            event.setNumber_visible(jsonArray.getString("cntc_no_visible"));
                            event.setDesc(jsonArray.getString("descrption"));
                            event.setStartTime(jsonArray.getString("event_start_time"));
                            event.setEndTime(jsonArray.getString("event_end_time"));
                            event.setNumberParticipant(jsonArray.getString("no_participant"));
                            event.setState(jsonArray.getString("state"));
                            event.setStreet(jsonArray.getString("street"));
                            event.setCountry(jsonArray.getString("country"));
                            event.setPin(jsonArray.getString("pin"));
                            event.setCity(jsonArray.getString("city"));
                            event.setContact_no(jsonArray.getString("contact_no"));
                            event.setColony(jsonArray.getString("colony"));
                            event.setEmailId(jsonArray.getString("email"));
                            event.setDate_created(jsonArray.getString("date_created"));
                            event.setUrl(jsonArray.getString("url"));
                            event.setEmail_visible(jsonArray.getString("email_visible"));
                            // adding event to SportsEvents array
                            eventList.add(0, event);
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
                //params.put("limit_val", limit_val);
                params.put("uid", uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(eventReq);
    }


    private void deleteEvent (final String event_id){

        // Tag used to cancel the request
        String tag_string_req = "Host_Detail";

        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_DELETE_EVENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Id Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(getActivity(), "Event is deleted successfully.", Toast.LENGTH_SHORT).show();
                        //   finish();
                        Intent intent = new Intent(getActivity(),MainActivity.class);
                        startActivity(intent);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Submit Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("event_id",event_id);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MyEvents.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MyEvents.ClickListener clickListener) {
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
