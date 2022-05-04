package com.fitnessfundoo.newActivity;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fitnessfundoo.R;
import com.fitnessfundoo.adapter.PlaceArrayAdapter;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.helper.SessionManager;
import com.fitnessfundoo.model.SportsEvent;
import com.fitnessfundoo.www.fitnessfundoo.MainActivity;
import com.fitnessfundoo.www.fitnessfundoo.SearchResultsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anubhav on 24-02-2016.
 */
public class EditEvent extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static EditText DateEdit;
    private static EditText sTimeEdit;
    private static EditText eTimeEdit;
    private static final String LOG_TAG = "Create Event";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private EditText mNameTextView;
    private EditText mAddressTextView;
    private EditText et_state;
    private EditText et_Country;
    private TextView pLatitude;
    private TextView pLongitude;
    private String uid;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private EditText et_Colony;
    private EditText event_title;
    private EditText cityName;
    private TextView mAttTextView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    //private Spinner dropdown;
    private String iNterest;
    private Spinner dropdown2;
    private String[] favorite_item;
    private Button popupBtn1;
    private Button popupBtn2;
    private Button popupBtn3;
    //private String[] items = new String[]{"My Favorite Sports", "All Categories"};
    private boolean home_flag;
    private Button subBtn;
    private EditText eMailid;
    private EditText ePin;
    private EditText contactNumber;
    private EditText eventDescription;
    private EditText streetNumber;
    //private ArrayList<String> container;
    private SessionManager session;
    private SportsEvent event;
    //private double latVal,LonVal;
    private static String eDate = "",eStime ="",eEtime = "";
    private String emailBool = "1" ,contactBool ;
    private String email,state,cntc_no_visible,email_visible,description,country,street,pin,colony,no_participants,date_created,start_time,end_time,host_id,event_date,category;;
    private String uName,uEmail,uContact,uMessage,hName,hEmail,hImageUrl,url,favourite_val,tname;
    private String imageUrl, title, address, lat, lon, city, contact;
    private String eventId,colonyName,sCityName,sTreet,sState,sPin,sCountry,sAddress,sName,latVal,lonVal,eTitle,sEmail,sContact,sCat,pVal,pVal1,pVal2,sDescription;
    private Toolbar toolbar;
    private Drawable toolbarBackgroundDrawable;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarBackgroundDrawable = toolbar.getBackground();
        setSupportActionBar(toolbar);
        MainActivity mainActivity = new MainActivity();
        mainActivity.setFalse();
        getSupportActionBar().setTitle("Edit Event");
        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(this);
        // Fetching user details from SQLite
        HashMap<String, String> user1 = db.getUserDetails();
        uid = user1.get("uid");
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            session.logoutUser();
            db.deleteUsers();
        }
        // Fetching user details from SQLite
        HashMap<String, String> user = session.getUserDetails();

        iNterest = user.get(SessionManager.KEY_INTEREST);


        String strArray[] = iNterest.split(",");
        Log.d("array string value :", iNterest);
        for (int i = 0; i < strArray.length; i++) {
            Log.d("array " + i + "  :", strArray[i]);
        }


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

        eEtime = end_time;
        eStime = start_time;
        eDate = event_date;
        pVal2 = no_participants;

        favorite_item = strArray;
        dropdown2 = (Spinner) findViewById(R.id.spinner2);
        String myString = category; //the value you want the position for
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(EditEvent.this, android.R.layout.simple_spinner_dropdown_item, favorite_item);
        int spinnerPosition = adapter1.getPosition(myString);
        dropdown2.setAdapter(adapter1);
        dropdown2.setSelection(spinnerPosition);
        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get select item
                int sid = dropdown2.getSelectedItemPosition();
                sCat = favorite_item[sid].toString().trim();
                Toast.makeText(getBaseContext(), "You have selected Event Category : " + favorite_item[sid] ,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DateEdit = (EditText) findViewById(R.id.editText1);
        DateEdit.setText(event_date);
        DateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showTruitonTimePickerDialog(v);
                showTruitonDatePickerDialog(v);
            }
        });

        sTimeEdit = (EditText) findViewById(R.id.stimeEdit);
        sTimeEdit.setText(start_time);
        sTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimePickerDialog(v);
                // showTruitonDatePickerDialog(v);
            }
        });
        eTimeEdit = (EditText) findViewById(R.id.etimeEdit);
        eTimeEdit.setText(end_time);
        eTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTimePickerDialog(v);
                // showTruitonDatePickerDialog(v);
            }
        });

        popupBtn1 = (Button) findViewById(R.id.contactButton);
        if(cntc_no_visible.equals("1")){
            popupBtn1.setText("Public");
            contactBool = "1";
            pVal = "Public";
        }else {
            pVal = "Private";
            contactBool = "0";
            popupBtn1.setText("Private");
        }
        popupBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(EditEvent.this, popupBtn1);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        popupBtn1.setText(item.getTitle());
                        pVal = popupBtn1.getText().toString().trim();
                        if(pVal.equals("Public"))
                            contactBool = "1";
                        else
                            contactBool = "0";
                        Toast.makeText(EditEvent.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        popupBtn2 = (Button) findViewById(R.id.emailButton);
        if(email_visible.equals("1")){
            popupBtn2.setText("Public");
            pVal1 = "Public";
            emailBool = "1";
        }else {
            pVal1 = "Private";
            emailBool = "0";
            popupBtn2.setText("Private");
        }
        popupBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(EditEvent.this, popupBtn2);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        popupBtn2.setText(item.getTitle());
                        pVal1 = popupBtn2.getText().toString().trim();
                        if(pVal1.equals("Public"))
                            emailBool = "1";
                        else
                            emailBool = "0";
                        Toast.makeText(EditEvent.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        popupBtn3 = (Button) findViewById(R.id.numberOfparticipant);
        popupBtn3.setText("Number Of Participant :" + no_participants);
        popupBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(EditEvent.this, popupBtn3);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.no_of_participant, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        popupBtn3.setText("Number Of Participant :" + item.getTitle());
                        pVal2 = item.getTitle().toString().trim();
                        Toast.makeText(EditEvent.this,"You Clicked : " + pVal2,Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        event_title = (EditText) findViewById(R.id.event_title);
        event_title.setText(title);

        et_Colony = (EditText) findViewById(R.id.colony);
        et_Colony.setText(colony);

        cityName = (EditText) findViewById(R.id.cityName);
        cityName.setText(city);

        et_state = (EditText) findViewById(R.id.state);
        et_state.setText(state);

        et_Country = (EditText) findViewById(R.id.country);
        et_Country.setText(country);

        eMailid = (EditText) findViewById(R.id.emailId);
        eMailid.setText(email);

        ePin = (EditText) findViewById(R.id.pin);
        ePin.setText(pin);

        streetNumber = (EditText) findViewById(R.id.street);
        streetNumber.setText(street);

        contactNumber = (EditText) findViewById(R.id.contactNo);
        contactNumber.setText(contact);

        eventDescription = (EditText) findViewById(R.id.event_desc1);
        eventDescription.setText(description);

        mGoogleApiClient = new GoogleApiClient.Builder(EditEvent.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);
        mNameTextView = (EditText) findViewById(R.id.name);

        mAddressTextView = (EditText) findViewById(R.id.address);
        mAddressTextView.setText(address);

        pLatitude = (TextView) findViewById(R.id.latitude);
        pLatitude.setText(lat);

        pLongitude = (TextView) findViewById(R.id.longitude);
        pLongitude.setText(lon);

        mAttTextView = (TextView) findViewById(R.id.att);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);



        subBtn = (Button) findViewById(R.id.submitEvent);
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                latVal =    pLatitude.getText().toString().trim();
                sName = mNameTextView.getText().toString().trim();
                sAddress = mAddressTextView.getText().toString().trim();
                lonVal =    pLongitude.getText().toString().trim();
                eTitle = event_title.getText().toString().trim();
                sCityName = cityName.getText().toString().trim();
                sState = et_state.getText().toString().trim();
                colonyName = et_Colony.getText().toString().trim();
                sDescription = eventDescription.getText().toString().trim();
                sContact = contactNumber.getText().toString().trim();
                sPin = eMailid.getText().toString().trim();
                sEmail = eMailid.getText().toString().trim();
                sCountry = et_Country.getText().toString().trim();
                sTreet = streetNumber.getText().toString().trim();
                if (eTitle.isEmpty() || sAddress.isEmpty() || eStime.isEmpty() || eEtime.isEmpty() || eDate.isEmpty() || sEmail.isEmpty()
                        || sContact.isEmpty() || emailBool.isEmpty() || pVal2.isEmpty() || contactBool.isEmpty() ) {
                    Toast.makeText(EditEvent.this, "Please Fill All Star Marked Field!!", Toast.LENGTH_SHORT).show();
                } else {
                    updateEventFunction();
                }
            }
        });

    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            mNameTextView.setText(Html.fromHtml(place.getName() + ""));
            mAddressTextView.setText(Html.fromHtml(place.getAddress() + ""));
            pLatitude.setText(Html.fromHtml(place.getLatLng().latitude + ""));
            //mIdTextView.setText(Html.fromHtml(place.getId() + ""));
            pLongitude.setText(Html.fromHtml(place.getLatLng().longitude + ""));
//            mWebTextView.setText(place.getLocale() + "");
            if (attributions != null) {
                mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(getApplicationContext(), SearchResultsActivity.class)));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_home:
                //setContentView(R.layout.activity_main);
                if(home_flag) {
                    Toast.makeText(EditEvent.this, "You are already at Home.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                    Toast.makeText(EditEvent.this, "Action Home button Clicked.", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_settings:
                Toast.makeText(EditEvent.this, "Action Setting button Clicked.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_search:
                Toast.makeText(EditEvent.this, "Action Search button Clicked.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.create_event:
                Intent intent = new Intent(this, EditEvent.class);
                startActivity(intent);
                Toast.makeText(EditEvent.this, "Action Create Event Clicked.", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
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

    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            //DateEdit.setText(day + "/" + (month + 1) + "/" + year);
            DateEdit.setText(year + "-" + (month + 1) + "-" + day);
            eDate = DateEdit.getText().toString().trim();
        }
    }

    public void startTimePickerDialog(View v) {
        DialogFragment newFragment = new sTimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void endTimePickerDialog(View v) {
        DialogFragment newFragment = new eTimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    public static class sTimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            // sTimeEdit.setText("") sTimeEdit.getText()+ "" +;
            sTimeEdit.setText( hourOfDay + ":" + minute);
            eStime = sTimeEdit.getText().toString().trim();


        }
    }

    public static class eTimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            // sTimeEdit.setText("") sTimeEdit.getText()+ "" +;
            eTimeEdit.setText( hourOfDay + ":" + minute);
            eEtime = eTimeEdit.getText().toString().trim();

        }
    }

    private void updateEventFunction(){

        Log.d("TAG :", "Login Response: " + eTitle);
        Log.d("TAG :", "Login Response: " + sDescription);
        Log.d("TAG :", "Login Response: " + sAddress);
        // Tag used to cancel the request
        String tag_string_req = "creat_event";

        pDialog.setMessage("Submitting ...");
        showDialog();
        isOnline();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EDIT_EVENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Edit Event Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(EditEvent.this, "Event Edited Successfully.", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(EditEvent.this, MainActivity.class);
                        startActivity(i);

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
                params.put("event_id",eventId);
                params.put("uid",uid);
                if(eTitle != null)params.put("name",eTitle);
                if(sAddress != null)params.put("address",sAddress);
                params.put("colony",colonyName);
                params.put("city",sCityName);
                params.put("state",sState);
                params.put("lat",latVal);
                params.put("lon",lonVal);
                params.put("email",sEmail);
                params.put("contact",sContact);
                params.put("cat",sCat);
                params.put("date",eDate);
                params.put("end_time",eEtime);
                params.put("start_time",eStime);
                params.put("email_bool",emailBool);
                params.put("contact_bool",contactBool);
                params.put("participant",pVal2);
                params.put("description",sDescription);
                params.put("pin",sPin);
                params.put("country",sCountry);
                params.put("street",sTreet);
                return params;

            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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

    public void onResume(){
        super.onResume();
        updateColor();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateColor() {
        toolbarBackgroundDrawable.setAlpha(255); // 0% of transparency
        toolbar.setBackground(toolbarBackgroundDrawable);
    }


}
