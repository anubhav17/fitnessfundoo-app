package com.fitnessfundoo.SlidingMenuOptions;

/**
 * Created by Anubhav on 15-01-2016.
 */


import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fitnessfundoo.R;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.helper.SessionManager;
import com.fitnessfundoo.www.fitnessfundoo.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendFeedback extends Fragment {

    public SendFeedback(){}
    private EditText  user_name;
    private EditText email;
    private EditText contact_number;
    private EditText message;
    private Button send_mail;
    private SessionManager session;
    private String iName,iEmail,iMessage,iContact,iUserID;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private Drawable toolbarBackgroundDrawable;
    private  Toolbar toolbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_send_feedback, container, false);
        super.getActivity();
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setFalse();
//        Toolbar toolbar =  (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbarBackgroundDrawable = toolbar.getBackground();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        // enabling action bar app icon and behaving it as toggle button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setIcon(R.drawable.ic_drawer);
        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // session manager
        session = new SessionManager(getActivity());
        if (!session.isLoggedIn()) {
            session.logoutUser();
            db.deleteUsers();
        }
        // Fetching user details from SQLite
        HashMap<String, String> user = session.getUserDetails();

        iName = user.get(SessionManager.KEY_NAME);
        iUserID = user.get(SessionManager.KEY_USER_ID);
        iEmail = user.get(SessionManager.KEY_EMAIL);

        user_name = (EditText) rootView.findViewById(R.id.user_name);
        user_name.setText(iName);

        email = (EditText) rootView.findViewById(R.id.user_email);
        email.setText(iEmail);
        contact_number = (EditText) rootView.findViewById(R.id.user_contact);
        message = (EditText) rootView.findViewById(R.id.user_message);

        send_mail = (Button) rootView.findViewById(R.id.submit_feedback);
        send_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iName = user_name.getText().toString().trim();
                iEmail = email.getText().toString().trim();
                iContact = contact_number.getText().toString().trim();
                iMessage = message.getText().toString().trim();
              /*  String to="befit@fitnessfundoo.com";
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
                email.putExtra(Intent.EXTRA_SUBJECT, iName);
                email.putExtra(Intent.EXTRA_TEXT, iMessage);

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));
*/
                if((iName.isEmpty()) || (iEmail.isEmpty()) || (iContact.isEmpty())) {
                    Toast.makeText(getActivity(), "Please fill all the required fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendFeddbackMail();

                }
            }
        });


        return rootView;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void sendFeddbackMail(){

       // Log.d("TAG :", "Login Response: " + eTitle);
        // Tag used to cancel the request
        String tag_string_req = "creat_event";

        pDialog.setMessage("Submitting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FEEDBACK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG :", "Create Event Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(getActivity(), "Thanks For Your Valuable Feedback.Your Message Sent Successfully.", Toast.LENGTH_SHORT).show();
                        //finish();
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
                Log.e("TAG", "Submit Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("name",iName);
                params.put("email",iEmail);
                params.put("message",iMessage);
                params.put("contact",iContact);
               // params.put("user_id",iUserID);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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


