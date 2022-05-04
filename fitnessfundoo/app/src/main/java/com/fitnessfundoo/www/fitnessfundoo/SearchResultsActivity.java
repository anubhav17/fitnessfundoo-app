package com.fitnessfundoo.www.fitnessfundoo;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.fitnessfundoo.R;
import com.fitnessfundoo.adapter.SearchListAdapter;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.helper.DividerItemDecoration;
import com.fitnessfundoo.model.SearchResult;
import com.fitnessfundoo.newActivity.EventPage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

/**
 * Created by Anubhav on 24-02-2016.
 */
public class SearchResultsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private TextView txtQuery;
    private String limit_val = "0";
    private static final String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog pDialog;
    private List<SearchResult> searchList = new ArrayList<SearchResult>();
    private ListView listView;
    private SearchListAdapter adapterList;
    private RecyclerView recyclerView;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Enabling Back navigation on Action Bar icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        adapterList = new SearchListAdapter(searchList);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view2);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterList);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_for_search);

        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                SearchResult result = searchList.get(position);
                Intent intent = new Intent(SearchResultsActivity.this, EventPage.class);
                intent.putExtra("title", result.getTitle());
                intent.putExtra("address", result.getAddress());
                intent.putExtra("imgae", result.getThumbnailUrl());
                intent.putExtra("result_date", result.getEventDate());
                intent.putExtra("lat", result.getLat());
                intent.putExtra("lon", result.getLon());
                intent.putExtra("contact", result.getContact_no());
                intent.putExtra("host_id", result.getEvent_host_id());
                intent.putExtra("category", result.getCat());
                intent.putExtra("cntc_no_visible", result.getNumber_visible());
                intent.putExtra("country", result.getCountry());
                intent.putExtra("street", result.getStreet());
                intent.putExtra("city", result.getCity());
                intent.putExtra("pin", result.getPin());
                intent.putExtra("start_time", result.getStartTime());
                intent.putExtra("end_time", result.getEndTime());
                intent.putExtra("description", result.getDesc());
                intent.putExtra("email", result.getEmailId());
                intent.putExtra("date_created", result.getDate_created());
                intent.putExtra("colony", result.getColony());
                intent.putExtra("state", result.getState());
                intent.putExtra("no_participants", result.getNumberParticipant());
                intent.putExtra("url", result.getUrl());
                intent.putExtra("email_visible", result.getEmail_visible());
                startActivity(intent);
                Toast.makeText(SearchResultsActivity.this, result.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                SearchResult result = searchList.get(position);
                Toast.makeText(SearchResultsActivity.this, result.getTitle() + " is Long Pressed!", Toast.LENGTH_SHORT).show();
            }
        }));



        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        searchResult();
                                    }
                                }
        );

        txtQuery = (TextView) findViewById(R.id.txtQuery);
        handleIntent(getIntent());


    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handling intent data
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
             query = intent.getStringExtra(SearchManager.QUERY);

            /**
             * Use this query to display search results like
             * 1. Getting the data from SQLite and showing in listview
             * 2. Making webrequest and displaying the data
             * For now we just display the query only
             */
            txtQuery.setText("Showing Results For : " + query);

            searchResult();
        }

    }


    @Override
    public void onRefresh () {
        searchResult();
    }



    private void searchResult() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        StringRequest resultReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SEARCH_RESULTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                int limit_val1 = 0;
                // Parsing json
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        for (int a = 0; a <= 15; a++) {
                            SearchResult result = new SearchResult();
                            JSONObject obj = jObj.getJSONObject(String.valueOf(a));
                            int rank = obj.getInt("sportsevents_id");
                            JSONObject jsonArray = obj.getJSONObject("sportsevents");

                            // updating offset value to highest value
                            if (rank >= limit_val1)
                                limit_val1 = rank;
                            limit_val = String.valueOf(limit_val1) ;
                            //Toast.makeText(SearchResultsActivity.this, "Value of limit_val : " + limit_val, Toast.LENGTH_SHORT).show();
                            result.setTitle(jsonArray.getString("title"));
                            result.setThumbnailUrl(jsonArray.getString("image"));
                            result.setAddress(jsonArray.getString("address"));

                            result.setEventDate(jsonArray.getString("event_date"));

                            result.setLat(jsonArray.getString("lat"));
                            result.setLon(jsonArray.getString("lon"));
                            result.setEvent_host_id(jsonArray.getString("event_host"));
                            result.setCat(jsonArray.getString("category"));
                            result.setNumber_visible(jsonArray.getString("cntc_no_visible"));
                            result.setDesc(jsonArray.getString("descrption"));
                            result.setStartTime(jsonArray.getString("event_start_time"));
                            result.setEndTime(jsonArray.getString("event_end_time"));
                            result.setNumberParticipant(jsonArray.getString("no_participant"));
                            result.setState(jsonArray.getString("state"));
                            result.setStreet(jsonArray.getString("street"));
                            result.setCountry(jsonArray.getString("country"));
                            result.setPin(jsonArray.getString("pin"));
                            result.setCity(jsonArray.getString("city"));
                            result.setContact_no(jsonArray.getString("contact_no"));
                            result.setColony(jsonArray.getString("colony"));
                            result.setEmailId(jsonArray.getString("email"));
                            result.setDate_created(jsonArray.getString("date_created"));
                            result.setUrl(jsonArray.getString("url"));
                            result.setEmail_visible(jsonArray.getString("email_visible"));
                            // adding result to Sportsresults array
                            searchList.add(0, result);
                        }

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        makeText(SearchResultsActivity.this,
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
                params.put("limit_val", limit_val);
                params.put("search_query", query);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(resultReq);


    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private SearchResultsActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final SearchResultsActivity.ClickListener clickListener) {
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