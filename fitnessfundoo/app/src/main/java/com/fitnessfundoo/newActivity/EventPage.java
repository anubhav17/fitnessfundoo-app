package com.fitnessfundoo.newActivity;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fitnessfundoo.Fragments.LoadJoinedUser;
import com.fitnessfundoo.Fragments.ShowMap;
import com.fitnessfundoo.R;
import com.fitnessfundoo.SlidingMenuOptions.MyFollowers;
import com.fitnessfundoo.adapter.JoinedUserListAdapter;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.app.EndPoints;
import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.model.JoinedUser;

import android.support.v7.widget.Toolbar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.fitnessfundoo.helper.*;
import com.fitnessfundoo.model.SportsEvent;
import com.fitnessfundoo.www.fitnessfundoo.MainActivity;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Anubhav on 08-02-2016.
 */
//public class EventPage extends AppCompatActivity implements OnScrollChangedCallback {
  public class EventPage extends AppCompatActivity  {

    private String imageUrl, title, address, lat, lon, city, contact;
    private Button interestButton;
    private double rating;
    private boolean home_flag = false;
    private String id;
    private ProgressDialog pDialog;
    TextView countText;
    private ImageView startImage;
    EditText editText,editText1,editText2,editText3;
    int count = 0;
    float curRate = 0;
    String sRating,sid;
    private String email,state,cntc_no_visible,email_visible,description,country,street,pin,colony,no_participants,date_created,start_time,end_time,host_id,event_date,category;;
    private String uid,hCity,hState,hCountry,hInterest;
    private List<JoinedUser> userList = new ArrayList<JoinedUser>();
    // private ListView listView;
    private JoinedUserListAdapter adapter;
    private Toolbar mToolbar;
    private Drawable mActionBarBackgroundDrawable;
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
    private String uName,uEmail,uContact,uMessage,hName,hEmail,hImageUrl,url,favourite_val,tname,eventId;
//    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_page);

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

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         //   getSupportActionBar().setHomeButtonEnabled(true);
        }
        MainActivity mainActivity = new MainActivity();
        mainActivity.setFalse();

        Intent intent = getIntent();
        if (intent == null)
            Log.d("***DEBUG****", "Intent was null");
        else {
            Log.d("**** DEBUG ***", "Intent OK");
            eventId = intent.getStringExtra("id");
            imageUrl = intent.getStringExtra("imgae");
            title = intent.getStringExtra("title");
            address = intent.getStringExtra("address");
            lat = intent.getStringExtra("lat");
            lon = intent.getStringExtra("lon");
            contact = intent.getStringExtra("contact");
            email = intent.getStringExtra("email");
            cntc_no_visible = intent.getStringExtra("cntc_no_visible");
            country = intent.getStringExtra("country");
            street = intent.getStringExtra("street");
            city = intent.getStringExtra("city");
            pin = intent.getStringExtra("pin");
            state = intent.getStringExtra("state");
            colony = intent.getStringExtra("colony");
            no_participants = intent.getStringExtra("no_participants");
            date_created = intent.getStringExtra("date_created");
            start_time = intent.getStringExtra("start_time");
            end_time = intent.getStringExtra("end_time");
            host_id = intent.getStringExtra("host_id");
            event_date = intent.getStringExtra("event_date");
            category = intent.getStringExtra("category");
            url = intent.getStringExtra("url");
            email_visible = intent.getStringExtra("email_visible");
            description = intent.getStringExtra("description");
        }

       // getEventId();
        getUserCount();

        getUpdateStar();
       // getHostDetail();

//        mHeader = (ImageView)findViewById(R.id.thumbnail);

        getSupportActionBar().setTitle(title);

        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(title);


        TextView description_tv = (TextView) findViewById(R.id.event_desc);
        description_tv.setText(description);


        TextView no_participantstv = (TextView) findViewById(R.id.no_participants);
        no_participantstv.setText(no_participants);


        TextView addresText = (TextView) findViewById(R.id.address);
        addresText.setText(address);

        TextView contactText = (TextView) findViewById(R.id.contactCall);
        if(cntc_no_visible.equals("1")) {
            contactText.setText(contact);
        }else {
            contactText.setText("Contact number is secret.");
        }

        TextView contactEmail = (TextView) findViewById(R.id.contactEmail);
        if(email_visible.equals("1")) {
            contactEmail.setText(email);
        }else {
            contactEmail.setText("Email Id is secret.");
        }

        TextView startTime = (TextView) findViewById(R.id.startTime);
        startTime.setText(start_time);

        TextView eDate = (TextView) findViewById(R.id.eDate);
        eDate.setText(event_date);

        TextView endTime = (TextView) findViewById(R.id.endTime);
        endTime.setText(end_time);

//        TextView dateText = (TextView) findViewById(R.id.rating);
//        dateText.setText(String.valueOf(rating));

//        int loader = R.drawable.user;
        mHeader = (ImageView) findViewById(R.id.thumbnail);



        Glide.with(getApplicationContext())
                .load(imageUrl)
                .fitCenter()
                .placeholder(R.drawable.user)
                .into(mHeader);


        // session manager
        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            session.logoutUser();
            db.deleteUsers();
        }
        // Fetching user details from SQLite
        HashMap<String, String> user1 = session.getUserDetails();

        iName = user1.get(SessionManager.KEY_NAME);

        iEmail = user1.get(SessionManager.KEY_EMAIL);


        Button shareOnfb = (Button) findViewById(R.id.shareOnfb);
        shareOnfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/sharer/sharer.php?u=http://www.fitnessfundoo.com/Events/" + url));
                startActivity(browserIntent);
            }
        });

        Button hostOfEvent = (Button) findViewById(R.id.hostOfEvent);
        hostOfEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventPage.this, HostOfEvent.class);
                intent.putExtra("host_id", host_id);

              /*  intent.putExtra("name", hName);
                intent.putExtra("dp_url", hImageUrl);
                intent.putExtra("interest", hInterest);
                intent.putExtra("city", hCity);
                intent.putExtra("state", hState);
                intent.putExtra("country", hCountry); */
                startActivity(intent);

            }
        });


        Button rateButton = (Button) findViewById(R.id.joinEvent);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventPage.this);
                // Get the layout inflater
                LayoutInflater inflater = getLayoutInflater();
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the
                // dialog layout
                builder.setTitle(title);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.user_icon);
                //builder.setView(inflater.inflate(R.layout.rate_dialog, null));
                View customDialogView = inflater.inflate(R.layout.join_event, null);
                editText = (EditText) customDialogView.findViewById(R.id.user_name);
                editText.setText(iName);
                editText1 = (EditText) customDialogView.findViewById(R.id.user_email);
                editText1.setText(iEmail);
                editText2 = (EditText) customDialogView.findViewById(R.id.user_contact);
                editText3 = (EditText) customDialogView.findViewById(R.id.user_message);
               /* Drawable progress1 = setRatingBar.getProgressDrawable();
                DrawableCompat.setTint(progress1, Color.BLUE); */
                builder.setView(customDialogView);
                // Add action buttons
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                         uName = editText.getText().toString().trim();
                         uEmail = editText1.getText().toString().trim();
                         uContact = editText2.getText().toString().trim();
                         uMessage = editText3.getText().toString().trim();
                        // Check for empty data in the form
                        if (uName.isEmpty() || uEmail.isEmpty() || uMessage.isEmpty()) {
                            // Prompt user to enter credentials
                            Toast.makeText(getApplicationContext(),
                                    "Please enter the Required Field!", Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            joinEvent(uName, uEmail, uContact, uMessage);
                            dialog.cancel();
         //                   finish();
                            startActivity(getIntent());

                        }
                    }
                })
                        // Button Cancel
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                builder.create();
                builder.show();
            }
        });



        TextView showMap = (TextView) findViewById(R.id.mapView);
        showMap.setText("MAP > ");
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                Bundle mBundle = new Bundle();
                fragment = new ShowMap();
                mBundle.putString("title", title);
                mBundle.putString("lat", lat);
                mBundle.putString("lng", lon);
                fragment.setArguments(mBundle);
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                   /* fragmentManager.beginTransaction()
                            .replace(R.id.container, fragment).commit(); */
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                    transaction.replace(R.id.container, fragment);
                    //transaction.addToBackStack(null);
                    getFragmentManager().popBackStackImmediate();
                    transaction.commit();
                } else {
                    // error in creating fragment
                    Log.e("Show Map", "Error in creating fragment");
                }
                //  getFragmentManager().popBackStack();
                //Toast.makeText(getApplicationContext(), "You Clicked Show Map.", Toast.LENGTH_SHORT).show();
            }
        });

     //   ObservableScrollable scrollView = (ObservableScrollable) findViewById(R.id.scrollview);
      //  scrollView.setOnScrollChangedCallback(EventPage.this);

        //onScroll(-1, 0);

        //ProgressBar loadGif = (ProgressBar) findViewById(R.id.loadingPanel);

        startImage = (ImageView) findViewById(R.id.favorite);
         interestButton = (Button) findViewById(R.id.interestedEvent);
/*        ViewGroup.LayoutParams params = interestButton.getLayoutParams();
        try {
            synchronized (params) {
                params.wait(10);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } */

           //   }
        /*else {
            interestButton.setText("Be First to join this event");
        }*/
            interestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
             /*   Intent intent = new Intent(EventPage.this, InterestedUser.class);
                startActivity(intent);*/
                  //  loadJoinedUser();
                    Fragment fragment = null;
                    fragment = new LoadJoinedUser();
                    if (fragment != null) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("eventId", Integer.valueOf(eventId));
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, fragment).commit();
                    } else {
                        // error in creating fragment
                        Log.e("View Profile", "Error in creating fragment");
                    }
                }
            });

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("Show Map") != null) {
            getSupportFragmentManager().popBackStackImmediate("Show Map",0);
        } else {
            mStatusBarManager.setTintColor(R.color.colorPrimaryDark);
            super.onBackPressed();
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_page, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_home:
                //setContentView(R.layout.activity_main);
                if (home_flag) {
                    Toast.makeText(EventPage.this, "You are already at Home.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                    Toast.makeText(EventPage.this, "Action Home button Clicked.", Toast.LENGTH_SHORT).show();
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
/*
    private void getHostDetail (){

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
                        hName = jObj.getString("name");
                        hEmail = jObj.getString("email");
                        hImageUrl = jObj.getString("dp_url");
                        hCity = jObj.getString("city");
                        hState = jObj.getString("state");
                        hCountry = jObj.getString("country");
                        hInterest = jObj.getString("interest");
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
*/

/*    private void getEventId (){

        final String tname = "event";
        // Tag used to cancel the request
        String tag_string_req = "Facility_Id";

        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FACILITY_ID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Id Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        id = jObj.getString("id");

                        getUserCount();

                       // finish();
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
                params.put("lat",lat);
                params.put("lng",lon);
                params.put("tname",tname);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

*/
    private void joinEvent( final String iName,final String iEmail,final String iContact,final String iMessage){

        // Tag used to cancel the request
        String tag_string_req = "rating_review";

        pDialog.setMessage("Submitting ...");
        showDialog();
        Log.d("url", EndPoints.URL_JOIN_EVENT);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.URL_JOIN_EVENT, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                Log.d("TAG :", "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(getApplicationContext(),
                                "Your Request has been Submitted Successfully .", Toast.LENGTH_LONG).show();

                        joinChatRoom();
                        FirebaseMessaging.getInstance().subscribeToTopic(eventId);
                       // finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                 //   Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Submit Error: " + error.getMessage());
          //      Toast.makeText(getApplicationContext(),
            //            error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("e_name", title);
                params.put("name", iName);
                params.put("email", iEmail);
                params.put("contact", iContact);
                params.put("message",iMessage);
                params.put("uid",uid);
                params.put("event_id",eventId);
                params.put("host_email",hEmail);
               // return params;
               return checkParams(params);
            }
            private Map<String, String> checkParams(Map<String, String> map){
                Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
                    if(pairs.getValue()==null){
                        map.put(pairs.getKey(), "");
                    }
                }
                return map;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void joinChatRoom( ){

        // Tag used to cancel the request
        String tag_string_req = "join_chat_room";

        pDialog.setMessage("Joining Chat Room ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.JOIN_CHAT_ROOM, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", " Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(EventPage.this, "You have successfully joined ChatRoom " + title +"-"+city + "!!", Toast.LENGTH_SHORT).show();
                        // finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
               //     Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Submit Error: " + error.getMessage());
            //    Toast.makeText(getApplicationContext(),
              //          error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("name", title +"-"+city);
                params.put("join_user_id",uid);
                params.put("host_id",host_id);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void getUserCount (){

        // Tag used to cancel the request
        String tag_string_req = "user_count";

        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_USER_COUNT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Count Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                       String Count = jObj.getString("joined_user_count");
                       iCount = new Integer(Count.toString());

                        if (iCount > 0) {
                            // if (iCount > 0) {
                            if (iCount == 1) {
                                interestButton.setText(iCount + " User has joined this Event");
                            } else {
                                interestButton.setText(iCount + " Users have joined this Event");
                            }
                        }else {
                            interestButton.setText( "No One has joined this Event.");
                        }
                            //   finish();
                     //   Toast.makeText(EventPage.this, "Value of iCount is" + iCount, Toast.LENGTH_SHORT).show();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                 //   Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Getting count Error: " + error.getMessage());
            //    Toast.makeText(getApplicationContext(),
              //          error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("event_id",eventId);
                params.put("user_id",uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void getUpdateStar (){

        // Tag used to cancel the request
        String tag_string_req = "user_count";

        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_STAR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Count Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        String fav_facility_id = jObj.getString("fav_facility_id");
                        String followers[] = fav_facility_id.split(",");
                        if(Arrays.asList(followers).contains(eventId)){
                            //follow_user.setText("Followed");
                            startImage.setActivated(true);
                        }else {
                            startImage.setActivated(false);
                        }

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
                Log.e("TAG", "Getting count Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("user_id",uid);
                params.put("indc","event");
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void loadJoinedUser(){

      //  Toast.makeText(EventPage.this, "value of count :" + iCount, Toast.LENGTH_SHORT).show();
        // Tag used to cancel the request
        String tag_string_req = "review_count";

        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_JOINED_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Count Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    userList.clear();
                    // Check for error node in json
                    if (!error) {
                        for (int i=0;i<iCount;i++){
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
                        loadList();
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
                params.put("event_id",eventId);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void loadList(){
        if (loadreview) {
            adapter = new JoinedUserListAdapter(userList);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.FILL_PARENT, adapter.getItemCount() * 210);
            p.addRule(RelativeLayout.BELOW, R.id.rellayout1);
            recyclerView.setLayoutParams(p);

            recyclerView.setAdapter(adapter);


            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    JoinedUser user = userList.get(position);
                    Intent intent = new Intent(EventPage.this, HostOfEvent.class);
                    intent.putExtra("host_id", user.getUser_id());
                    startActivity(intent);

                    // Toast.makeText(getActivity(), event.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongClick(View view, final int position) {

                    //    Toast.makeText(getActivity(), event.getTitle() + " is Long Pressed!", Toast.LENGTH_SHORT).show();

                }
            }));

        }
        //listView = (ListView) findViewById(R.id.list);

        //listView.setAdapter(adapter);
        //listView.setScrollContainer(false);
        //LinearLayout listViewReplacement = (LinearLayout) findViewById(R.id.list);
/*        RecyclerView listViewReplacement = (RecyclerView) findViewById(R.id.list);
        adapter = new JoinedUserListAdapter(EventPage.this, userList);
        //  NamesRowItemAdapter adapter = new NamesRowItemAdapter(this, namesInList);
        if (loadreview){
            for (int i = 0; i < adapter.getCount(); i++) {
                View view = adapter.getView(i, null, listViewReplacement);
                listViewReplacement.addView(view);
                loadreview = false;
            }
*/

//        }
    }
/*
    @Override
    public void onScroll(int l, int scrollPosition) {
        int headerHeight = mHeader.getHeight() - mToolbar.getHeight();
        float ratio = 0;
        if (scrollPosition > 0 && headerHeight > 0)
            ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;

        updateActionBarTransparency(ratio);
        updateStatusBarColor(ratio);
        updateParallaxEffect(scrollPosition);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateActionBarTransparency(float scrollRatio) {
        int newAlpha = (int) (scrollRatio * 255);
        mActionBarBackgroundDrawable.setAlpha(newAlpha);
        mToolbar.setBackground(mActionBarBackgroundDrawable);
    }

    private void updateStatusBarColor(float scrollRatio) {
        int r = interpolate(Color.red(mInitialStatusBarColor), Color.red(mFinalStatusBarColor), 1 - scrollRatio);
        int g = interpolate(Color.green(mInitialStatusBarColor), Color.green(mFinalStatusBarColor), 1 - scrollRatio);
        int b = interpolate(Color.blue(mInitialStatusBarColor), Color.blue(mFinalStatusBarColor), 1 - scrollRatio);
        mStatusBarManager.setTintColor(Color.rgb(r, g, b));
    }

    private void updateParallaxEffect(int scrollPosition) {
        float damping = 0.5f;
        int dampedScroll = (int) (scrollPosition * damping);
        int offset = mLastDampedScroll - dampedScroll;
        mHeader.offsetTopAndBottom(-offset);

        mLastDampedScroll = dampedScroll;
    }

    private int interpolate(int from, int to, float param) {
        return (int) (from * param + to * (1 - param));
    } */

    public void onToggleStar(View view)
    {
        Log.d("STAR", view.isSelected() + "");
        //view.setSelected(!view.isSelected());
        //view.setEnabled(true);
        if (!view.isActivated()){
            view.setActivated(true);
            favourite_val = "1";
            upodateFavorite();
            Toast.makeText(EventPage.this, title + " is Favourited.", Toast.LENGTH_SHORT).show();
        }else {
            view.setActivated(false);
            favourite_val = "0";
            upodateFavorite();
            Toast.makeText(EventPage.this, title + " is removed from Favourite.", Toast.LENGTH_SHORT).show();
        }
    }

    private void upodateFavorite(){

        tname = "event";

        // Tag used to cancel the request
        String tag_string_req = "update_fav";

        pDialog.setMessage("Updating ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_FAV, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "update Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                  //      Toast.makeText(getApplicationContext(),
                    //            "Updated Successfully .", Toast.LENGTH_LONG).show();

                        //finish();
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
                Log.e("TAG", "Update Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("fav_val",eventId);
                params.put("uid",uid);
                params.put("tname",tname);
                params.put("fav_unfav",favourite_val);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private EventPage.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final EventPage.ClickListener clickListener) {
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

