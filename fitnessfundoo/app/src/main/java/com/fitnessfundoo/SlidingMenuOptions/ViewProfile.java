package com.fitnessfundoo.SlidingMenuOptions;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
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
import com.fitnessfundoo.R;
import com.fitnessfundoo.adapter.InterestLevelAdapter;
import com.fitnessfundoo.adapter.InterestLevelAdapterEditable;
import com.fitnessfundoo.adapter.JoinedUserListAdapter;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.helper.CircleTransform;
import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.helper.SessionManager;
import com.fitnessfundoo.model.InterestLevel;
import com.fitnessfundoo.model.JoinedUser;
import com.fitnessfundoo.www.fitnessfundoo.MainActivity;
import com.fitnessfundoo.helper.ProfileImageLoader;
import com.fitnessfundoo.www.fitnessfundoo.SearchResultsActivity;
//import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViewProfile extends Fragment   {
    private List<InterestLevel> interestLevelList = new ArrayList<InterestLevel>();
    // private ListView listView;
    private InterestLevelAdapterEditable adapter;
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;
    private String interest_level,userId;
    private TextView followers,following,txtName,event_count;
    public ViewProfile(){}
    private InterestLevel interestLevel;
    private EditText bio,country,contact,city,state;
    private Drawable toolbarBackgroundDrawable;
    private  Toolbar toolbar;
    private static  String interest_string= "";
    private String nameUpdate,contactUpdate,cityUpdate,stateUpdate,countryUpdate,bioUpdate;
    //private TypedArray navMenuIcons;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_profile, container, false);
        super.getActivity();
        setHasOptionsMenu(true);
        // Progress dialog
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setCancelable(false);

        ((MainActivity) getActivity()).setFalse();
        Toolbar toolbar =  (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbarBackgroundDrawable = toolbar.getBackground();


         txtName = (TextView) rootView.findViewById(R.id.name);
        TextView txtEmail = (TextView) rootView.findViewById(R.id.uEmail);
                 city     = (EditText) rootView.findViewById(R.id.city);
        //city.setText( "Not Set");
                 state    = (EditText) rootView.findViewById(R.id.state);
       // state.setText( "Not Set");
                 country  = (EditText) rootView.findViewById(R.id.country);
       // country.setText( "Not Set");
                 bio      = (EditText) rootView.findViewById(R.id.bio);
       // bio.setText( "Not Set");
                 contact  = (EditText) rootView.findViewById(R.id.uContact);
      //  contact.setText( "Not Set");
                 followers = (TextView) rootView.findViewById(R.id.followers);

        following = (TextView) rootView.findViewById(R.id.following);

        event_count = (TextView) rootView.findViewById(R.id.event_count);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // enabling action bar app icon and behaving it as toggle button
        if(((AppCompatActivity) getActivity()).getSupportActionBar() !=null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        }

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                fragment = new MyFollowers();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();
                } else {
                    // error in creating fragment
                    Log.e("View Profile", "Error in creating fragment");
                }
               // Toast.makeText(getActivity(), "You Clicked Edit Profile Button.", Toast.LENGTH_SHORT).show();
            }
        });

        event_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                fragment = new MyEvents();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();
                } else {
                    // error in creating fragment
                    Log.e("View Profile", "Error in creating fragment");
                }
                // Toast.makeText(getActivity(), "You Clicked Edit Profile Button.", Toast.LENGTH_SHORT).show();
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                fragment = new MyFollowings();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();
                } else {
                    // error in creating fragment
                    Log.e("View Profile", "Error in creating fragment");
                }
              //  Toast.makeText(getActivity(), "You Clicked Edit Profile Button.", Toast.LENGTH_SHORT).show();
            }
        });


        // ImageView user_image = (ImageView) rootView.findViewById(R.id.user_image);
        // user_image.getDisplay();
        // SqLite database handler
        db = new SQLiteHandler(rootView.getContext());
        // Fetching user details from SQLite
        HashMap<String, String> user_sqlite = db.getUserDetails();
        userId = user_sqlite.get("uid");

        getInterestLevel();
      //  getUserData();

        // session manager
        session = new SessionManager(rootView.getContext());

        if (!session.isLoggedIn()) {
            session.logoutUser();
            db.deleteUsers();
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManager.KEY_NAME);
        String dp_url = user.get(SessionManager.KEY_DP_URL);
        String uEmail = user.get(SessionManager.KEY_EMAIL);
        //String iNterest = user.get(SessionManager.KEY_INTEREST);

        // email

        // Displaying the user details on the screen
          txtName.setText(name);
          txtEmail.setText(uEmail);
         // arrInterest.setText(iNterest);

        Button edtBtn = (Button) rootView.findViewById(R.id.editBtn);
        edtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameUpdate = txtName.getText().toString().trim();
                session.updateName(nameUpdate);
                contactUpdate = contact.getText().toString().trim();
                cityUpdate = city.getText().toString().trim();
                stateUpdate = state.getText().toString().trim();
                countryUpdate = country.getText().toString().trim();
                bioUpdate = bio.getText().toString().trim();
                if(!interest_level.isEmpty())
                    interest_string =  adapter.displayString();
                updateData();
                Toast.makeText(getActivity(), "Your data will be updated.", Toast.LENGTH_SHORT).show();
            }
        });

        // Imageview to show
        ImageView image = (ImageView) rootView.findViewById(R.id.user_image);

        // Loading profile image
        Glide.with(this).load(dp_url)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image);


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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void getInterestLevel (){

        isOnline();

        // Tag used to cancel the request
        String tag_string_req = "interest_level";

        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INTEREST_LEVEL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "interest_level Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("TAG :",jObj.toString());
                    // Check for error node in json
                    if (!error) {


                        if(jObj.getString("user_event_count") != null && !(jObj.getString("user_event_count").isEmpty())  ){
                            event_count.setText(jObj.getString("user_event_count") );
                          }else {
                            event_count.setText( "0");
                        }

                        if(jObj.getString("user_followers_count") != null && !(jObj.getString("user_followers_count").isEmpty())  ){
                           followers.setText(jObj.getString("user_followers_count") );
                        }else {
                            followers.setText( "0");
                        }

                        if(jObj.getString("user_following_count") != null && !(jObj.getString("user_following_count").isEmpty())  ){
                            following.setText(jObj.getString("user_following_count") );
                            }else {
                            following.setText( "0");
                        }

                        if(jObj.getString("city") != null && !(jObj.getString("city").isEmpty())  ){
                            city.setText( jObj.getString("city"));
                        }
                       if(jObj.getString("state") != null && !(jObj.getString("state").isEmpty())){
                           state.setText(jObj.getString("state"));
                       }
                        if(jObj.getString("country") != null && !(jObj.getString("country").isEmpty())){
                            country.setText( jObj.getString("country"));
                        }
                        if(jObj.getString("bio") != null && !(jObj.getString("bio").isEmpty())){
                            bio.setText( jObj.getString("bio"));
                        }
                        if(jObj.getString("contact") != null && !(jObj.getString("contact").isEmpty())){
                            contact.setText( jObj.getString("contact"));
                        }
                        interest_level = jObj.getString("interest_level");
                        if(!interest_level.isEmpty()) {
                            String strArray[] = interest_level.split(",");
                            Log.d("array string value :", interest_level);
                            for (int i = 0; i < strArray.length; i++) {
                                Log.d("array " + i + "  :", strArray[i]);
                                InterestLevel interestLevel = new InterestLevel();
                                String strArray1[] = strArray[i].split(":");
                                for (int j = 0; j < strArray1.length; j++) {
                                    Log.d("array1 " + j + "  :", strArray1[j]);
                                    interestLevel.setSports_name(strArray1[0]);
                                    interestLevel.setLevel(strArray1[1]);

                                }
                                interestLevelList.add(0, interestLevel);
                            }
                        }
                         loadList();
                        //   finish();

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
                Log.e("TAG", "Getting count Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("user_id",userId);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    public void loadList(){
        adapter = new InterestLevelAdapterEditable(getActivity(), interestLevelList);
        //listView.setAdapter(adapter);
        //listView.setScrollContainer(false);
        ListView listViewReplacement = (ListView) getActivity().findViewById(R.id.list);
        adapter = new InterestLevelAdapterEditable(getActivity(), interestLevelList);
        //  NamesRowItemAdapter adapter = new NamesRowItemAdapter(this, namesInList);
        /*for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.getView(i, null, listViewReplacement);
            listViewReplacement.addView(view);
        } */

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, adapter.getCount() * 61);

        p.addRule(RelativeLayout.BELOW, R.id.rellayout1);

        listViewReplacement.setLayoutParams(p);
//        listViewReplacement.setLayoutParams(new RelativeLayout.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, adapter.getCount()*40));
        listViewReplacement.setAdapter(adapter);
    }

    public void onResume(){
        super.onResume();
    }

    private void updateData (){
        isOnline();
        // Tag used to cancel the request
        String tag_string_req = "interest_level";

        pDialog.setMessage("Loding ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_DATA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "interest_level Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Log.d("string", interest_string);

                        Toast.makeText(getActivity(), "Data updated Successfully.", Toast.LENGTH_SHORT).show();


                        //   finish();

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
                Log.e("TAG", "Getting count Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("id",userId);
                params.put("name",nameUpdate);
                params.put("city",cityUpdate);
                params.put("state",stateUpdate);
                params.put("contact",contactUpdate);
                params.put("bio",bioUpdate);
                if(!interest_string.isEmpty()){
                    params.put("interestLevel",interest_string);
                }else {
                    params.put("interestLevel","");
                }
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
