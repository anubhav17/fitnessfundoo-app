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
import android.support.v4.graphics.drawable.DrawableCompat;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fitnessfundoo.Fragments.ShowMap;
import com.fitnessfundoo.R;
import com.fitnessfundoo.adapter.RatingListAdapter;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.model.RatingReview;
import com.fitnessfundoo.www.fitnessfundoo.MainActivity;
import android.support.v7.widget.Toolbar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fitnessfundoo.helper.*;

/**
 * Created by Anubhav on 08-02-2016.
 */
public class FacilityPage extends AppCompatActivity  {
    private String imageUrl, title, address, lat, lon, city, contact;
    private double rating;
    private ImageView updateStar;
    private boolean home_flag = false;
    private int id;
    private ProgressDialog pDialog;
    TextView countText;
    EditText editText;
    int count = 0;
    float curRate = 0;
    String sRating,sid;
    private String url,favourite_val,sunOpen, freeTrial, persTraining,tname,open_time,close_time, lockerShower, parking,fac_name;
    private SQLiteHandler db;
    private String uid,fid;
    private List<RatingReview> reviewList = new ArrayList<RatingReview>();
   // private ListView listView;
    private RatingListAdapter adapter;
    private Toolbar mToolbar;
    private Drawable mActionBarBackgroundDrawable;
    private ImageView mHeader;
    //private NetworkImageView mHeader;
    private int mLastDampedScroll;
    private int mInitialStatusBarColor;
    private int mFinalStatusBarColor;
    private boolean loadreview = true;
    private SystemBarTintManager mStatusBarManager;
    private Button loadReview;
    private SessionManager session;
    private boolean getreviewList = false;
    private Button freeTrialBtn;
    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_page);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBarBackgroundDrawable = mToolbar.getBackground();
        setSupportActionBar(mToolbar);

        mStatusBarManager = new SystemBarTintManager(this);
        mStatusBarManager.setStatusBarTintEnabled(true);
        mInitialStatusBarColor = Color.BLACK;
        mFinalStatusBarColor = getResources().getColor(R.color.colorPrimaryDark);

       // mHeader = findViewById(R.id.header);
  /*      mHeader = (NetworkImageView) findViewById(R.id.thumbnail1);
        if (imageLoader == null) {
            imageLoader = AppController.getInstance().getImageLoader();
            Toast.makeText(FacilityPage.this, "image loader is null", Toast.LENGTH_SHORT).show();
        }
        mHeader.setImageUrl(imageUrl, imageLoader);
*/

        // session manager
        session = new SessionManager(this);

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
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        MainActivity mainActivity = new MainActivity();
        mainActivity.setFalse();

        Intent intent = getIntent();
        if (intent == null)
            Log.d("***DEBUG****", "Intent was null");
        else {
            Log.d("**** DEBUG ***", "Intent OK");
            tname = intent.getStringExtra("tname");
            fid = intent.getStringExtra("fid");
            imageUrl = intent.getStringExtra("imgae_url");
            rating = intent.getDoubleExtra("rating", 0.0);
            title = intent.getStringExtra("title");
            address = intent.getStringExtra("address");
            lat = intent.getStringExtra("lat");
            lon = intent.getStringExtra("lon");
            contact = intent.getStringExtra("contact");
            city = intent.getStringExtra("city");
            sunOpen = intent.getStringExtra("sunOpen");
            parking = intent.getStringExtra("parking");
            persTraining = intent.getStringExtra("persTraining");
            lockerShower = intent.getStringExtra("lockerShower");
            freeTrial = intent.getStringExtra("freeTrial");
            open_time = intent.getStringExtra("open_time");
            close_time = intent.getStringExtra("close_time");
            url = intent.getStringExtra("url");

        }

        updateStar = (ImageView) findViewById(R.id.favorite);
        Button sunOpenBtn = (Button) findViewById(R.id.sunopen);
        sunOpenBtn.setEnabled(false);

        freeTrialBtn = (Button) findViewById(R.id.freetrial);
        freeTrialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freeTrialBtn.setEnabled(false);
                Toast.makeText(FacilityPage.this, "Book a free trial Facility is not available right now.", Toast.LENGTH_SHORT).show();
            }
        });

        mHeader = (ImageView)findViewById(R.id.thumbnail);
        if (tname.equals("sports_club"))
        {mHeader.setImageResource(R.drawable.sports);}
        else if (tname.equals("swimming_pool"))
        {mHeader.setImageResource(R.drawable.swimming_pool);}


      /*  Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gym);
        Bitmap blurredBitmap = BlurBuilder.blur( this, originalBitmap );

        mHeader.setBackgroundDrawable( new BitmapDrawable( getResources(), blurredBitmap ) );
         */

     //   getFacilityId();
        getUpdateStar();

        getSupportActionBar().setTitle(title);

        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(title);

        TextView addresText = (TextView) findViewById(R.id.address);
        addresText.setText(address);

        TextView dateText = (TextView) findViewById(R.id.rating);
        dateText.setText(String.valueOf(rating));

        TextView contact_no = (TextView) findViewById(R.id.contact);
        contact_no.setText(contact);

        TextView openTime = (TextView) findViewById(R.id.openTime);
        //if (open_time == null && open_time.isEmpty() && open_time.equals("null"))
        open_time = "06:00:00";
        openTime.setText("Open Time :" + open_time);

        TextView closeTime = (TextView) findViewById(R.id.closeTime);
       /* if(close_time.equals("")) */
        close_time = "22:00:00";
        closeTime.setText("Close Time :" + close_time);

        Button shareOnfb = (Button) findViewById(R.id.shareOnfb);
        shareOnfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tname.equals("gym")) {
                    fac_name = "Gym";
                } else if (tname.equals("sports_club")) {
                    fac_name = "SportsClub";
                } else if (tname.equals("swimming_pool")) {
                    fac_name = "SwimmingPool";
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/sharer/sharer.php?u=http://www.fitnessfundoo.in/" + fac_name + "/" + url));
                startActivity(browserIntent);
            }
        });

        loadReview = (Button) findViewById(R.id.loadReview);
        loadReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadReview.setEnabled(false);
                getreviewList = true;
                getReviwCount();
              /*  if(count == 0) {
                    getReviwCount();
                }else {
                    loadRatinReview();
                } */
            /*    if(count != 0)
                loadRatinReview();  */
/*                //listView = (ListView) findViewById(R.id.list);
                adapter = new RatingListAdapter(FacilityPage.this, reviewList);
                //listView.setAdapter(adapter);
                //listView.setScrollContainer(false);
                LinearLayout listViewReplacement = (LinearLayout) findViewById(R.id.list);
                adapter = new RatingListAdapter(FacilityPage.this, reviewList);
                //  NamesRowItemAdapter adapter = new NamesRowItemAdapter(this, namesInList);
                if (loadreview){
                for (int i = 0; i < adapter.getCount(); i++) {
                    View view = adapter.getView(i, null, listViewReplacement);
                    listViewReplacement.addView(view);
                    loadreview = false;
                }
                }
*/
            }
        });

        //TextView latText = (TextView) findViewById(R.id.lat);
        //latText.setText("Lat :" + lat + " "+ lon + " "+ sunOpen +" "+ city +" "+ contact);
        Button rateButton = (Button) findViewById(R.id.btnDialog);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 0)
                  getReviwCount();
                AlertDialog.Builder builder = new AlertDialog.Builder(FacilityPage.this);
                // Get the layout inflater
                LayoutInflater inflater = getLayoutInflater();
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the
                // dialog layout
                builder.setTitle(title);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.user_icon);
                //builder.setView(inflater.inflate(R.layout.rate_dialog, null));
                View customDialogView = inflater.inflate(R.layout.rate_dialog, null);
                countText = (TextView) customDialogView.findViewById(R.id.countText);
                countText.setText(count + " Ratings");
                editText = (EditText) customDialogView.findViewById(R.id.reviw_edittext);
                RatingBar setRatingBar = (RatingBar) customDialogView.findViewById(R.id.setRating);
               /* Drawable progress1 = setRatingBar.getProgressDrawable();
                DrawableCompat.setTint(progress1, Color.BLUE); */
                float rating1 = (float) rating;
                setRatingBar.setRating(rating1);
                RatingBar ratingBar = (RatingBar) customDialogView.findViewById(R.id.getRating);
                Drawable progress = ratingBar.getProgressDrawable();
                DrawableCompat.setTint(progress, Color.BLUE);
                //ratingBar.setRating(curRate);
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                       // DecimalFormat decimalFormat = new DecimalFormat("#.#");
                        //curRate = Float.valueOf(decimalFormat.format((curRate * count + rating) / ++count));
                        curRate = rating;
                        Toast.makeText(FacilityPage.this, "New Rating: " + curRate, Toast.LENGTH_SHORT).show();
                        ratingBar.setRating(curRate);
                        //countText.setText(count + " Ratings");
                    }
                });
                builder.setView(customDialogView);
                // Add action buttons
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                double rating = curRate;
                                String review = editText.getText().toString().trim();

                                // Check for empty data in the form
                                if (rating != 0 && !review.isEmpty()) {
                                    // login user
                                    submitRating(rating, review);
                                    dialog.cancel();
                                } else {
                                    // Prompt user to enter credentials
                                    Toast.makeText(getApplicationContext(),
                                            "Please enter the Rating And give valuable Review!", Toast.LENGTH_LONG)
                                            .show();
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
        Log.d("**** DEBUG ***", "after submit rating bar");

        TextView showMap = (TextView) findViewById(R.id.lat);
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
                Toast.makeText(getApplicationContext(), "You Clicked Show Map.", Toast.LENGTH_SHORT).show();
            }
        });


    }


    public void onToggleStar(View view)
    {
        Log.d("STAR", view.isSelected() + "");
        //view.setSelected(!view.isSelected());
        //view.setEnabled(true);
        if (!view.isActivated()){
            view.setActivated(true);
            favourite_val = "1";
            upodateFavorite();
            Toast.makeText(FacilityPage.this, title + " is Favourited.", Toast.LENGTH_SHORT).show();
        }else {
            view.setActivated(false);
            favourite_val = "0";
            upodateFavorite();
            Toast.makeText(FacilityPage.this, title + " is removed from Favourite.", Toast.LENGTH_SHORT).show();
        }
    }

    private void upodateFavorite(){

        //sid     = Integer.toString(id);
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
                        Toast.makeText(getApplicationContext(),
                                "Updated Successfully .", Toast.LENGTH_LONG).show();
  //                      favoriteCount();

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
                params.put("fav_val",fid);
                params.put("tname",tname);
                params.put("uid",uid);
                params.put("fav_unfav",favourite_val);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

/*
    private void favoriteCount (){

        // Tag used to cancel the request
        String tag_string_req = "user_count";

        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FAVORITE_COUNT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Count Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        String Count = jObj.getString("fav_count");
                        session.updateFavCount(Count);

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
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
*/

    private void submitRating(final double rating, final String review){

        sRating = String.valueOf(rating);
       // sid     = Integer.toString(id);
        //Toast.makeText(FacilityPage.this, "value of id :" + sid, Toast.LENGTH_SHORT).show();
        // Tag used to cancel the request
        String tag_string_req = "rating_review";

        pDialog.setMessage("Submitting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_RATING, new Response.Listener<String>() {

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
                                "Submitted Successfully .", Toast.LENGTH_LONG).show();
                        getreviewList =true;
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
                params.put("rating", sRating);
                params.put("review", review);
                params.put("fid",fid);
                params.put("tname",tname);
                params.put("uid",uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void getReviwCount(){

        //sRating = String.valueOf(rating);
        //sid     = Integer.toString(id);
        Log.d("fac_id",fid);
        Log.d("tname",tname);
        // Tag used to cancel the request
        String tag_string_req = "review_count";

        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_COUNT_REVIEW, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Count Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        count = jObj.getInt("review_count");
                        loadReview.setText(count + " Rating And reviews.");
                        if(count != 0 && getreviewList)
                            loadRatinReview();

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
                params.put("sid",fid);
                params.put("tname",tname);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void getFacilityId (){

        //sRating = String.valueOf(rating);
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
                        id = jObj.getInt("id");
                    //    getUpdateStar(id);
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
                        if(Arrays.asList(followers).contains(fid)){
                            //follow_user.setText("Followed");
                            updateStar.setActivated(true);
                        }else {
                            updateStar.setActivated(false);
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
//                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("indc",tname);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void loadRatinReview(){

        //sRating = String.valueOf(rating);
       // sid     = Integer.toString(id);
        // Tag used to cancel the request
        String tag_string_req = "review_count";

        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REVIEW_DETAIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Count Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    reviewList.clear();
                    // Check for error node in json
                    if (!error) {
                        if(loadreview){
                            reviewList.clear();
                        }
                        for (int i=0;i<count;i++){
                        RatingReview ratingReview = new RatingReview();
                        JSONObject obj = jObj.getJSONObject(String.valueOf(i));
                        JSONObject jsonArray = obj.getJSONObject("review_detail");
                        ratingReview.setUser_id(jsonArray.getString("user_id"));
                            Log.d("user_id", "user id " + jsonArray.getString("user_id"));
                        ratingReview.setRating(jsonArray.getDouble("rating"));
                        ratingReview.setThumbnailUrl(jsonArray.getString("image"));
                        ratingReview.setTitle(jsonArray.getString("title"));
                        ratingReview.setReview(jsonArray.getString("review"));
                        // adding ratingReview to ratingReview array
                        reviewList.add(0,ratingReview);
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
                params.put("sid",fid);
                params.put("tname",tname);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void loadList(){
   /*     //listView = (ListView) findViewById(R.id.list);
        adapter = new RatingListAdapter(FacilityPage.this, reviewList);
        //listView.setAdapter(adapter);
        //listView.setScrollContainer(false);
        LinearLayout listViewReplacement = (LinearLayout) findViewById(R.id.list);
        adapter = new RatingListAdapter(FacilityPage.this, reviewList);
        //  NamesRowItemAdapter adapter = new NamesRowItemAdapter(this, namesInList);
        if (loadreview){
            for (int i = 0; i < adapter.getCount(); i++) {
                View view = adapter.getView(i, null, listViewReplacement);
                listViewReplacement.addView(view);
                loadreview = false;
            }
        } */
        if (loadreview) {
            adapter = new RatingListAdapter(reviewList);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.FILL_PARENT, adapter.getItemCount() * 200);
            p.addRule(RelativeLayout.BELOW, R.id.layRating);
            recyclerView.setLayoutParams(p);

            recyclerView.setAdapter(adapter);

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    RatingReview ratingReview = reviewList.get(position);
                    Intent intent = new Intent(FacilityPage.this, HostOfEvent.class);
                    intent.putExtra("host_id", ratingReview.getUser_id());
                    Log.d("user_id", "user id " + ratingReview.getUser_id());
                    startActivity(intent);

                    // Toast.makeText(getActivity(), event.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongClick(View view, final int position) {
                    //    Toast.makeText(getActivity(), event.getTitle() + " is Long Pressed!", Toast.LENGTH_SHORT).show();
                }
            }));
        }
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private FacilityPage.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FacilityPage.ClickListener clickListener) {
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


/*    @Override
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
                   // Toast.makeText(FacilityPage.this, "You are already at Home.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                    finish();
                    //Toast.makeText(FacilityPage.this, "Action Home button Clicked.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("Show Map") != null) {
            getSupportFragmentManager().popBackStackImmediate("Show Map", 0);
        }
    }
}
