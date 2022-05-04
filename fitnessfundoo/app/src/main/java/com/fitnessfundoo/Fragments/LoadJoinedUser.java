package com.fitnessfundoo.Fragments;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fitnessfundoo.R;
import com.fitnessfundoo.adapter.JoinedUserListAdapter;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.helper.DividerItemDecoration;
import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.helper.SessionManager;
import com.fitnessfundoo.model.JoinedUser;
import com.fitnessfundoo.newActivity.HostOfEvent;
import com.fitnessfundoo.www.fitnessfundoo.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Anubhav on 09-05-2016.
 */
public class LoadJoinedUser extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private JoinedUserListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<JoinedUser> userList = new ArrayList<JoinedUser>();
    private SQLiteHandler db;
    private String userId;
    private ProgressDialog pDialog;
    private SessionManager session;
    private  int eventId1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_joined_users, container, false);
        super.getActivity();
//        ((MainActivity) getActivity()).setFalse();
        Toolbar toolbar =  (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        // enabling action bar app icon and behaving it as toggle button
        if(((AppCompatActivity) getActivity()).getSupportActionBar()!= null)
        {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Joined Users");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        }


        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        // SqLite database handler
       // db = new SQLiteHandler(rootView.getContext());
        // Fetching user details from SQLite
      //  HashMap<String, String> user_sqlite = db.getUserDetails();
      //  userId = user_sqlite.get("uid");
      //  Log.d("uid", userId);
        int eventId = 0;
        Bundle bundle = this.getArguments();
        eventId1 = bundle.getInt("eventId", eventId);

        // session manager
        session = new SessionManager(rootView.getContext());

        if (!session.isLoggedIn()) {
            session.logoutUser();
            db.deleteUsers();
        }

        adapter = new JoinedUserListAdapter(userList);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.FILL_PARENT, adapter.getItemCount() * 210);
//        p.addRule(RelativeLayout.BELOW, R.id.rellayout1);
//        recyclerView.setLayoutParams(p);

        recyclerView.setAdapter(adapter);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                JoinedUser user = userList.get(position);
                Intent intent = new Intent(getActivity(), HostOfEvent.class);
                intent.putExtra("host_id", user.getUser_id());
                startActivity(intent);

                // Toast.makeText(getActivity(), event.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, final int position) {

                //    Toast.makeText(getActivity(), event.getTitle() + " is Long Pressed!", Toast.LENGTH_SHORT).show();

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

                                        loadJoinedUser();
                                    }
                                }
        );

        return rootView;
    }


    @Override
    public void onRefresh () {
        loadJoinedUser();
    }

    private void loadJoinedUser(){

        //  Toast.makeText(EventPage.this, "value of count :" + iCount, Toast.LENGTH_SHORT).show();
        // Tag used to cancel the request
        String tag_string_req = "review_count";


        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);


        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_JOINED_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Joined user Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    userList.clear();
                    // Check for error node in json
                    if (!error) {
                        for (int i=0;i<jObj.length();i++){
                            JoinedUser joinedUser = new JoinedUser();
                            JSONObject obj = jObj.getJSONObject(String.valueOf(i));
                            JSONObject jsonArray = obj.getJSONObject("joined_user_detail");
                            joinedUser.setUser_id(jsonArray.getString("user_id"));
                            joinedUser.setCity(jsonArray.getString("city"));
                            joinedUser.setThumbnailUrl(jsonArray.getString("image"));
                            joinedUser.setTitle(jsonArray.getString("name"));
                            joinedUser.setState(jsonArray.getString("state"));
                            joinedUser.setCountry(jsonArray.getString("country"));
                            // adding ratingReview to ratingReview array
                            userList.add(0,joinedUser);
                            //finish();
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
         //           Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                // so that it renders the list view with updated data
                adapter.notifyDataSetChanged();
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Submit Error: " + error.getMessage());
                //Toast.makeText(getActivity(),
                  //      error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
                swipeRefreshLayout.setRefreshing(false);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("event_id",String.valueOf(eventId1));
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


/*    public void loadlist(){
        adapter = new JoinedUserListAdapter(userList);
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.FILL_PARENT, adapter.getItemCount() * 210);
//        p.addRule(RelativeLayout.BELOW, R.id.rellayout1);
//        recyclerView.setLayoutParams(p);

        recyclerView.setAdapter(adapter);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                JoinedUser user = userList.get(position);
                Intent intent = new Intent(getActivity(), HostOfEvent.class);
                intent.putExtra("host_id", user.getUser_id());
                startActivity(intent);

                // Toast.makeText(getActivity(), event.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, final int position) {

                //    Toast.makeText(getActivity(), event.getTitle() + " is Long Pressed!", Toast.LENGTH_SHORT).show();

            }
        }));

    } */


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private LoadJoinedUser.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final LoadJoinedUser.ClickListener clickListener) {
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


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
