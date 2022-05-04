package com.fitnessfundoo.newActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fitnessfundoo.Fragments.ShowMap;
import com.fitnessfundoo.R;
import com.fitnessfundoo.adapter.InterestLevelAdapter;
import com.fitnessfundoo.adapter.InterestLevelAdapterEditable;
import com.fitnessfundoo.adapter.JoinedUserListAdapter;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.helper.CircleTransform;
import com.fitnessfundoo.helper.ObservableScrollable;
import com.fitnessfundoo.helper.OnScrollChangedCallback;
import com.fitnessfundoo.helper.ProfileImageLoader;
import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.helper.SessionManager;
import com.fitnessfundoo.helper.SystemBarTintManager;
import com.fitnessfundoo.model.InterestLevel;
import com.fitnessfundoo.model.JoinedUser;
import com.fitnessfundoo.www.fitnessfundoo.MainActivity;
import com.fitnessfundoo.www.fitnessfundoo.SearchResultsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

/**
 * Created by Anubhav on 28-03-2016.
 */

public class HostOfEvent extends AppCompatActivity  {
    private String imageUrl, title, address, lat, lon, city, contact;
    private double rating;
    private boolean home_flag = false;
    private InterestLevel interestLevel;
    private List<InterestLevel> interestLevelList = new ArrayList<InterestLevel>();
    private ProgressDialog pDialog;
    private TextView followers_count;
    private String host_id;
    private String uid,hCity,hState,hCountry,hInterest;
    private List<JoinedUser> userList = new ArrayList<JoinedUser>();
    // private ListView listView;
    private InterestLevelAdapter adapter;
    private Toolbar mToolbar;
    private Drawable mActionBarBackgroundDrawable;
    private static boolean follow_flag = false;
    private ImageView mHeader;
    private SessionManager session;
    private String iName,iEmail,iMessage,iContact;
    private SQLiteHandler db;
    //private NetworkImageView mHeader;
    private int mLastDampedScroll;
    private int mInitialStatusBarColor;
    private int mFinalStatusBarColor;
    private boolean loadreview = true;
    private SystemBarTintManager mStatusBarManager;
    private Button loadReview;
    private boolean getreviewList = false;
    private Button freeTrialBtn;
    private static int iCount = 0;
    private String name,interest_level;
    private TextView city_tv;
    private TextView titleText;
    private TextView bio_tv;
    private TextView state_tv;
    private TextView country_tv;
    private int loader;
    private Button follow_user;
    private ProfileImageLoader imgLoader;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_of_event);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBarBackgroundDrawable = mToolbar.getBackground();
        setSupportActionBar(mToolbar);

        mStatusBarManager = new SystemBarTintManager(this);
        mStatusBarManager.setStatusBarTintEnabled(true);
        mInitialStatusBarColor = Color.BLACK;
        mFinalStatusBarColor = getResources().getColor(R.color.colorPrimaryDark);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(this);

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

       if(getSupportActionBar()!= null) {
           getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           getSupportActionBar().setHomeButtonEnabled(true);
       }


        MainActivity mainActivity = new MainActivity();
        mainActivity.setFalse();

        Intent intent = getIntent();
        if (intent == null)
            Log.d("***DEBUG****", "Intent was null");
        else {
            Log.d("**** DEBUG ***", "Intent OK");
            host_id = intent.getStringExtra("host_id");
        }




        getHostDetail();

        titleText = (TextView) findViewById(R.id.title);
        //titleText.setText(name);

        mHeader = (ImageView) findViewById(R.id.thumbnail);

        bio_tv = (TextView) findViewById(R.id.interest);

        followers_count = (TextView) findViewById(R.id.followers_count);
        //interest_tv.setText(interest);

        //city_tv = (TextView) findViewById(R.id.city);
       // city_tv.setText(city);

        // state_tv = (TextView) findViewById(R.id.state);
       // state_tv.setText(state);

        // country_tv = (TextView) findViewById(R.id.country);
       // country_tv.setText(country);

        follow_user = (Button) findViewById(R.id.follow_user);
        follow_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(follow_flag){
                    Log.d("i am in","removefollow");
                   removeFollow();
                }else {
                    updateFollowers();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("Show Map") != null) {
            getSupportFragmentManager().popBackStackImmediate("Show Map",0);
        } else {
            super.onBackPressed();
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

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            pDialog.dismiss();
            Toast.makeText(this, "No Internet connection!", Toast.LENGTH_LONG).show();
            //     pDialog.setMessage("Please Connect to the Internet...");
            //       showDialog();
            return false;
        }
        return true;
    }



    private void updateFollowers (){
        isOnline();
        // Tag used to cancel the request
        String tag_string_req = "Host_Detail";

        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_FOLLOWERS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Id Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        if(jObj.getString("followers_count").equals("1")){
                            followers_count.setText(jObj.getString("followers_count") + " Follower");
                        }else{
                            followers_count.setText(jObj.getString("followers_count") + " Followers");
                        }

                       // Toast.makeText(HostOfEvent.this, "Count is :"+ jObj.getString("followers_count"), Toast.LENGTH_SHORT).show();
                        follow_user.setText("Followed");
                        follow_flag = true;
                        //   finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Submit Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("hId",host_id);
                params.put("uId",uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void removeFollow (){
        isOnline();
        // Tag used to cancel the request
        String tag_string_req = "Host_Detail";

        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REMOVE_FOLLOW, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Id Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        if(jObj.getString("followers_count").equals("1")){
                            followers_count.setText(jObj.getString("followers_count") + " Follower");
                        }else{
                            followers_count.setText(jObj.getString("followers_count") + " Followers");
                        }
                        // Toast.makeText(HostOfEvent.this, "Count is :"+ jObj.getString("followers_count"), Toast.LENGTH_SHORT).show();
                        follow_user.setText("Follow");
                        follow_flag =false;
                        //   finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Submit Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("hId",host_id);
                params.put("uId",uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void getHostDetail (){
        isOnline();
        // Tag used to cancel the request
        String tag_string_req = "Host_Detail";

        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_HOST_DETAIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Id Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        titleText.setText(jObj.getString("name"));

                        // Loading profile image
                        Glide.with(getApplicationContext()).load(jObj.getString("dp_url"))
                                .crossFade()
                                .thumbnail(0.5f)
                                .bitmapTransform(new CircleTransform(getApplicationContext()))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(mHeader);

                        //interest_tv.setText(jObj.getString("interest_level"));
          //              city_tv.setText( jObj.getString("city"));
            //            state_tv.setText(jObj.getString("state"));
              //          country_tv.setText( jObj.getString("country"));
                            bio_tv.setText(jObj.getString("bio"));
                            if (jObj.getString("followers_count").equals("1")) {
                                followers_count.setText(jObj.getString("followers_count") + " Follower");
                            } else {
                                followers_count.setText(jObj.getString("followers_count") + " Followers");
                            }

                            if(jObj.getString("followers_count").isEmpty()){
                                followers_count.setText("0" + " Follower");
                            }

                        String user_followers = jObj.getString("user_followers");
                        if(!user_followers.isEmpty()){
                        String followers[] = user_followers.split(",");
                        if(Arrays.asList(followers).contains(uid)){
                            follow_user.setText("Followed");
                            follow_flag = true;
                        }
                        }else {
                            follow_flag = false;
                        }

                        interest_level = jObj.getString("interest_level");
                        if(!interest_level.isEmpty()) {
                            String strArray[] = interest_level.split(",");

                            Log.d("array string value :", interest_level);
                            for (int i = 0; i < strArray.length; i++) {
                                Log.d("array " + i + "  :", strArray[i]);
                                interestLevel = new InterestLevel();
                                String strArray1[] = strArray[i].split(":");
                                for (int j = 0; j < strArray1.length; j++) {
                                    Log.d("array1 " + j + "  :", strArray1[j]);
                                    //           interestLevel.setSportsPosition(strArray1[0],i);
                                    interestLevel.setSports_name(strArray1[0]);
                                    interestLevel.setLevel(strArray1[1]);
                                }
                                interestLevelList.add(0, interestLevel);
                            }
                        }
                        if(getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(jObj.getString("name"));
                        }
                        loadList();
                        //   finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Submit Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("hId",host_id);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public void loadList(){
        //listView = (ListView) findViewById(R.id.list);
        adapter = new InterestLevelAdapter(this, interestLevelList);
        //listView.setAdapter(adapter);
        //listView.setScrollContainer(false);
        LinearLayout listViewReplacement = (LinearLayout) findViewById(R.id.list);
        adapter = new InterestLevelAdapter(this, interestLevelList);
        //  NamesRowItemAdapter adapter = new NamesRowItemAdapter(this, namesInList);
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.getView(i, null, listViewReplacement);
            listViewReplacement.addView(view);
        }
    }

}
