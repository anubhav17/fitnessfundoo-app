package com.fitnessfundoo.SlidingMenuOptions;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fitnessfundoo.R;
import com.fitnessfundoo.adapter.InterestLevelAdapter;
import com.fitnessfundoo.adapter.InterestLevelAdapterEditable;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.helper.CircleTransform;
import com.fitnessfundoo.helper.ProfileImageLoader;
import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.helper.SessionManager;
import com.fitnessfundoo.model.InterestLevel;
import com.fitnessfundoo.www.fitnessfundoo.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Anubhav on 03-02-2016.
 */
public class EditProfile extends Fragment {
    private List<InterestLevel> interestLevelList = new ArrayList<InterestLevel>();
    // private ListView listView;
    private InterestLevelAdapterEditable adapter;
    public EditProfile(){}
    private ProgressDialog pDialog;
    private Bitmap bitmap;
    private EditText user_name;
    private TextView email;
    private EditText contact_number;
    private EditText city,state,country,bio;
    private Button send_mail;
    private SQLiteHandler db;
    private SessionManager session;
    private ImageView image_new;
    private String imageUpload;
    private InterestLevel interestLevel;
    private String userId,interest_level="";
    private int PICK_IMAGE_REQUEST = 1;
    private String nameUpdate,contactUpdate,cityUpdate,stateUpdate,countryUpdate,bioUpdate;
    private static  String interest_string= "";
    private Drawable toolbarBackgroundDrawable;
    private  Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        super.getActivity();
        ((MainActivity) getActivity()).setFalse();
      //  Toolbar toolbar =  (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbarBackgroundDrawable = toolbar.getBackground();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        // enabling action bar app icon and behaving it as toggle button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Edit Profile");


        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        // SqLite database handler
        db = new SQLiteHandler(rootView.getContext());
        // Fetching user details from SQLite
        HashMap<String, String> user_sqlite = db.getUserDetails();
        userId = user_sqlite.get("uid");

        getDataForUpdate();


        user_name = (EditText) rootView.findViewById(R.id.name);
        email = (TextView) rootView.findViewById(R.id.uEmail);
        contact_number = (EditText) rootView.findViewById(R.id.uContact);
        country = (EditText) rootView.findViewById(R.id.country);
        city = (EditText) rootView.findViewById(R.id.city);
        state = (EditText) rootView.findViewById(R.id.state);
        bio = (EditText) rootView.findViewById(R.id.bio);



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

        user_name.setText(name);
        email.setText(uEmail);


        // Loader image - will be shown before loading image
        int loader = R.drawable.user;
        // Imageview to show
        ImageView image = (ImageView) rootView.findViewById(R.id.user_image);

        // Loading profile image
        Glide.with(this).load(dp_url)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image);

        Button edtBtn = (Button) rootView.findViewById(R.id.updateBtn);
        edtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameUpdate = user_name.getText().toString().trim();
                session.updateName(nameUpdate);
                contactUpdate = contact_number.getText().toString().trim();
                cityUpdate = city.getText().toString().trim();
                stateUpdate = state.getText().toString().trim();
                countryUpdate = country.getText().toString().trim();
                bioUpdate = bio.getText().toString().trim();
                if(!interest_level.isEmpty())
                interest_string =  adapter.displayString();
                updateData();

            }
        });


        Button buttonLoadImage = (Button) rootView.findViewById(R.id.changePicture);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showFileChooser();
                //uploadImage();
            }
        });

        return rootView;
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        //Log.d("image :",encodedImage);
        return encodedImage;
    }

    private void uploadImage(){
        // Tag used to cancel the request
        String tag_string_req = "profile_update";

        pDialog.setMessage("Updating ...");
        showDialog();
        isOnline();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PROFILE_UPDATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Profile Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        //   finish();
                        Toast.makeText(getActivity(),
                                "Image uploaded Successfully.", Toast.LENGTH_LONG).show();

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
                Log.e("TAG", "Uploading  Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                imageUpload = getStringImage(bitmap);

                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                //Adding parameters
                params.put("image", imageUpload);
                params.put("id", userId);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Boolean success = false;
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
              //  ByteArrayOutputStream baos = new ByteArrayOutputStream();
             //   bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                //Setting the Bitmap to ImageView
                image_new = (ImageView) getActivity().findViewById(R.id.user_image);
                image_new.setImageBitmap(bitmap);
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(success){
                uploadImage();
            }
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
                params.put("country",countryUpdate);
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



    private void getDataForUpdate (){
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

                    // Check for error node in json
                    if (!error) {

                            city.setText( jObj.getString("city"));
                            state.setText(jObj.getString("state"));
                            country.setText( jObj.getString("country"));
                            bio.setText( jObj.getString("bio"));
                            contact_number.setText( jObj.getString("contact"));

                        interest_level = jObj.getString("interest_level");
                        if(!interest_level.isEmpty()){
                        String strArray[] = interest_level.split(",");
                        Log.d("array string value :", interest_level);
                        for (int i = 0; i < strArray.length; i++) {
                            Log.d("array " + i + "  :", strArray[i]);
                             interestLevel = new InterestLevel();
                            String strArray1[] = strArray[i].split(":");
                            for(int j =0 ; j< strArray1.length;j++){
                                Log.d("array1 " + j + "  :", strArray1[j]);
                     //           interestLevel.setSportsPosition(strArray1[0],i);
                                interestLevel.setSports_name(strArray1[0]);
                                interestLevel.setLevel(strArray1[1]);

                            }
                            interestLevelList.add(0,interestLevel);
                        }}else {
                            interest_level = "";
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
        //listView = (ListView) findViewById(R.id.list);
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
