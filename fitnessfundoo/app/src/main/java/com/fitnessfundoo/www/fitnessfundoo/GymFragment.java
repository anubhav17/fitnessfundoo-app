package com.fitnessfundoo.www.fitnessfundoo;

/**
 * Created by Anubhav on 15-01-2016.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import com.fitnessfundoo.BuildConfig;
import com.fitnessfundoo.R;
import com.fitnessfundoo.adapter.CustomListAdapter;
import com.fitnessfundoo.adapter.FacilityNearByAdapter;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.helper.DividerItemDecoration;
import com.fitnessfundoo.helper.GPSTracker;
import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.model.FitnessFacility;
import com.fitnessfundoo.newActivity.FacilityPage;
import com.fitnessfundoo.www.fitnessfundoo.MainActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.facebook.FacebookSdk.getApplicationContext;

public class GymFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public GymFragment(){}
    GPSTracker gps;
    private double latitude,longitude;
    private View view3;
    private String dist_val1;
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
    private FacilityNearByAdapter adapterList;
    private Drawable toolbarBackgroundDrawable;
    private  Toolbar toolbar;
    // location last updated time
    private String mLastUpdateTime;

    // location updates interval - 100sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 100000;

    // fastest updates interval - 50 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 50000;

    private static final int REQUEST_CHECK_SETTINGS = 100;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gym, container, false);
        super.getActivity();
    //    setHasOptionsMenu(true);

        ButterKnife.bind(getActivity());


        // initialize the necessary libraries
        init();

        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);

        // SqLite database handler
        db = new SQLiteHandler(getActivity());
        // Fetching user details from SQLite
        HashMap<String, String> user1 = db.getUserDetails();
        uid = user1.get("uid");

        HashMap<String, String> dist_val = db.getDistVal("gym");
        dist_val1 = dist_val.get("dist_val");

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        recyclerView =  rootView.findViewById(R.id.recycler_view_gym);

        adapterList = new FacilityNearByAdapter(facilityList);

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
                Intent intent = new Intent(getActivity(), FacilityPage.class);
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
//                Toast.makeText(getActivity(), facility.getTitle() + " is Long Pressed!", Toast.LENGTH_SHORT).show();

            }
        }));

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_gym);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        startLocationButtonClick();
                                    }
                                }
        );

        tablename = "gym";
        limit_val = "0";

        return rootView;
    }

    @Override
    public void onRefresh () {
        //  getLocation();
        startLocationButtonClick();
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


    public void init(){
        isOnline();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mSettingsClient = LocationServices.getSettingsClient(getActivity());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

    }

    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }
    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void updateLocationUI() {
        if (mCurrentLocation != null) {
         //   Toast.makeText(getActivity(), "Lat: " + mCurrentLocation.getLatitude() + ", " +
           //         "Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();

            fetchNearByFacilities();
            Toast.makeText(getActivity(), "Showing Results within " + dist_val1 + " K.M.", Toast.LENGTH_LONG).show();

        }

        toggleButtons();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }

    private void toggleButtons() {
        if (mRequestingLocationUpdates) {
            // btnStartUpdates.setEnabled(false);
            // btnStopUpdates.setEnabled(true);
        } else {
            // btnStartUpdates.setEnabled(true);
            // btnStopUpdates.setEnabled(false);
        }
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

//                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }


    public void startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }


                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void stopLocationButtonClick() {
        mRequestingLocationUpdates = false;
        stopLocationUpdates();
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      //  Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        toggleButtons();
                    }
                });
    }

    public void showLastKnownLocation() {
        if (mCurrentLocation != null) {
           // Toast.makeText(getApplicationContext(), "Lat: " + mCurrentLocation.getLatitude()
             //       + ", Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Last known location is not available!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        Toast.makeText(getContext(), "Please allow access to your Location to serve you better!", Toast.LENGTH_LONG).show();
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        updateLocationUI();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }
    }

    private void fetchNearByFacilities() {


        // showing refresh animation before making http call
    //    swipeRefreshLayout.setRefreshing(true);

        StringRequest facilityReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOAD_FACILITY_NEARBY, new Response.Listener<String>() {


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
                        for (int a = 0; a <= jObj.length(); a++) {
                            FitnessFacility facility = new FitnessFacility();
                            JSONObject obj = jObj.getJSONObject(String.valueOf(a));

                            //  int rank1 = obj.getInt("fitnessfacility_id");

                            //int rank1 = a;
                            JSONObject jsonArray = obj.getJSONObject("fitnessfacility");

                            // updating offset value to highest value

                            //   if (rank1 >= limit_val1)
                            //       limit_val1 = rank1;
                            //   limit_val = String.valueOf(limit_val1) ;

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
                            facility.setDistance(jsonArray.getString("distance"));
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
                //   params.put("limit_val", limit_val);
                params.put("lat", String.valueOf(latitude));
                params.put("lon", String.valueOf(longitude));
                params.put("range",dist_val1);

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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.in/sharer/sharer.php?u=http://www.fitnessfundoo.com/" + fac + "/" + facility.getUrl()));
        startActivity(browserIntent);
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private GymFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final GymFragment.ClickListener clickListener) {
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

