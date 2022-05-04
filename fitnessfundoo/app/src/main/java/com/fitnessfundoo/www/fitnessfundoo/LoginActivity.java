package com.fitnessfundoo.www.fitnessfundoo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.toolbox.ImageLoader;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fitnessfundoo.R;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.helper.SessionManager;
import com.fitnessfundoo.model.User;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.facebook.Profile;

import io.branch.referral.Branch;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    // private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    //Signin button
    private SignInButton signInButton;

    //Signing Options
    private GoogleSignInOptions gso;

    //google api client
    private GoogleApiClient mGoogleApiClient;

    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        //info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        //Initializing google signin option
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        //Initializing signinbutton
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());

        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        //Setting onclick listener to signing button
        signInButton.setOnClickListener(this);

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));

        callbackManager = CallbackManager.Factory.create();

        //  loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                System.out.println("onSuccess");
                pDialog.setMessage("Logging in ...");
                showDialog();
                //              String accessToken = loginResult.getAccessToken().getToken();
                Log.i("accessToken", "I am here");

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                        final Bundle bFacebookData = getFacebookData(object);
                        //bFacebookData.getString("email");
                        //  Log.d("email value",bFacebookData.getString("email"));
                        final String email,fname,lname,id,image ;
                        if(bFacebookData.getString("email")!=null) {
                            email= bFacebookData.getString("email");
                        }else {
                            email="";
                        }if(bFacebookData.getString("first_name") != null){
                            fname=bFacebookData.getString("first_name");
                        }else {
                            fname="";
                        }if(bFacebookData.getString("last_name") != null){
                            lname=bFacebookData.getString("last_name");
                        }else {
                            lname = "";
                        }if(bFacebookData.getString("idFacebook") !=null){
                            id =bFacebookData.getString("idFacebook");
                        }else {
                            id="";
                        }if(bFacebookData.getString("profile_pic") != null){
                            image=bFacebookData.getString("profile_pic");
                        }else {
                            image="";
                        }

                        isOnline();
                        String tag_string_req = "req_login";

                        StringRequest strReq = new StringRequest(Method.POST,
                                AppConfig.URL_FB_LOGIN, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "Login Response: " + response.toString());
                                hideDialog();

                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    boolean error = jObj.getBoolean("error");

                                    // Check for error node in json
                                    if (!error) {
                                        // user successfully logged in

                                        // Now store the user in SQLite
                                        String uid = jObj.getString("uid");

                                        JSONObject user = jObj.getJSONObject("user");
                                        String name = user.getString("name");
                                        String email = user.getString("email");
                                        String created_at = user
                                                .getString("date_created");
                                        String dp_url = user
                                                .getString("dp_url");

                                        String interest = user
                                                .getString("interest");

                                        Log.d("value of interest : ", interest);
                                        User user1 = new User(uid,
                                                name,
                                                email);
                                        // Create login session
                                        session.setLogin(name, email, dp_url, interest);
                                        // storing user in shared preferences
                                        AppController.getInstance().getPrefManager().storeUser(user1);

                                        // Inserting row in users table
                                        db.addUser(name, email, uid, created_at);

                                        db.addDistance(uid, "event", "500");
                                        db.addDistance(uid,"gym","500");
                                        db.addDistance(uid,"sports_club","500");
                                        db.addDistance(uid,"swimming_pool","500");
                                        // Launch main activity
                                        Intent intent = new Intent(LoginActivity.this,
                                                MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Error in login. Get the error message
                                        String errorMsg = jObj.getString("error_msg");
                                        Toast.makeText(getApplicationContext(),
                                                errorMsg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    // JSON error
                                    e.printStackTrace();
                                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    Toast.makeText(getApplicationContext(), "Something went wrong Please try Again! " , Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "Login Error: " + error.getMessage());
                                //   Toast.makeText(getApplicationContext(),
                                //         error.getMessage(), Toast.LENGTH_LONG).show();
                                hideDialog();
                            }
                        }) {

                            @Override
                            protected Map<String, String> getParams() {
                                // Posting parameters to login url
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("email",email);
                                params.put("name", fname + lname);
                                params.put("id",id);
                                params.put("image",image);
                                return params;
                            }

                        };

                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

                    }

                });
                Bundle parameters = new Bundle();
                // parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // ParĂ¡metros que pedimos a facebook
                parameters.putString("fields", "id, first_name, last_name, email"); // ParĂ¡metros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),
                        "Login attempt canceled.", Toast.LENGTH_LONG)
                        .show();

                //  info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(),
                        "Login attempt failed.", Toast.LENGTH_LONG)
                        .show();

                // info.setText("Login attempt failed.");
            }
        });
    }

    //This function will option signing intent
    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            final GoogleSignInAccount acct = result.getSignInAccount();
            final String photourl;
            if(acct.getPhotoUrl()!= null){
                photourl =  acct.getPhotoUrl().toString();
            }else {
                photourl="";
            }
            pDialog.setMessage("Logging in ...");
            showDialog();
            isOnline();
            String tag_string_req = "req_login";

            StringRequest strReq = new StringRequest(Method.POST,
                    AppConfig.URL_GOOGLE_LOGIN, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Login Response: " + response);
                    hideDialog();

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        // Check for error node in json
                        if (!error) {
                            // user successfully logged in

                            // Now store the user in SQLite
                            String uid = jObj.getString("uid");

                            JSONObject user = jObj.getJSONObject("user");
                            String name = user.getString("name");
                            String email = user.getString("email");
                            String created_at = user
                                    .getString("date_created");
                            String dp_url = user
                                    .getString("dp_url");

                            String interest = user
                                    .getString("interest");

                            Log.d("value of name : ",user.getString("name") );
                            Log.d("value of interest : ", interest);
                            User user1 = new User(uid,
                                    name,
                                    email);
                            // Create login session
                            session.setLogin(name, email, dp_url, interest);
                            // storing user in shared preferences
                            AppController.getInstance().getPrefManager().storeUser(user1);

                            // Inserting row in users table
                            db.addUser(name, email, uid, created_at);

                            db.addDistance(uid, "event", "100");
                            db.addDistance(uid,"gym","100");
                            db.addDistance(uid,"sports_club","100");
                            db.addDistance(uid,"swimming_pool","100");
                            // Launch main activity
                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Error in login. Get the error message
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Something went wrong Please try Again! " , Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Login Error: " + error.getMessage());
                    //   Toast.makeText(getApplicationContext(),
                    //         error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email",acct.getEmail());
                    params.put("name", acct.getDisplayName());
                    params.put("id",acct.getId());
                    params.put("image",photourl);
                    return params;
                }

            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

            Log.d("name",acct.getDisplayName());

         /*   //Displaying name and email
            textViewName.setText(acct.getDisplayName());
            textViewEmail.setText(acct.getEmail());

            //Initializing image loader
            imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                    .getImageLoader();

            imageLoader.get(acct.getPhotoUrl().toString(),
                    ImageLoader.getImageListener(profilePhoto,
                            R.drawable.ic_launcher,
                            R.drawable.ic_launcher));

            //Loading image
            profilePhoto.setImageUrl(acct.getPhotoUrl().toString(), imageLoader);
*/
        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == signInButton) {
            //Calling signin
            signIn();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //If signin
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
    }


    /**
     * function to verify login details in mysql db
     */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();
        isOnline();
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("date_created");
                        String dp_url = user
                                .getString("dp_url");

                        String interest = user
                                .getString("interest");

                        Log.d("value of interest : ", interest);
                        User user1 = new User(uid,
                                name,
                                email);
                        // Create login session
                        session.setLogin(name, email, dp_url, interest);
                        // storing user in shared preferences
                        AppController.getInstance().getPrefManager().storeUser(user1);

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at);

                        db.addDistance(uid, "event", "100");
                        db.addDistance(uid,"gym","100");
                        db.addDistance(uid,"sports_club","100");
                        db.addDistance(uid,"swimming_pool","100");
                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Something went wrong Please try Again! " , Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                //  Toast.makeText(getApplicationContext(),
                //        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

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


    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();;
        try {
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=300&height=300");
                Log.i("profile_pic", profile_pic + "");
                final String photourl;
                if(profile_pic!= null){
                    photourl =  profile_pic.toString();
                }else {
                    photourl="";
                }
                bundle.putString("profile_pic", photourl);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            //          Log.d("email",object.getString("email"));
            //if (object.has("gender"))
            //  bundle.putString("gender", object.getString("gender"));
            //if (object.has("birthday"))
            //  bundle.putString("birthday", object.getString("birthday"));
            //if (object.has("location"))
            //bundle.putString("location", object.getJSONObject("location").getString("name"));
//            Log.d("location",object.getJSONObject("location").getString("name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;
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