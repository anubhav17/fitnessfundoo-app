package com.fitnessfundoo.newActivity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.fitnessfundoo.R;
import com.fitnessfundoo.adapter.InterestLevelAdapter;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
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

/**
 * Created by Anubhav on 18-04-2016.
 */
public class SettingEvent extends AppCompatActivity {
    private boolean home_flag = false;
    private String uid;
    private ProgressDialog pDialog;
    private Toolbar mToolbar;
    private SQLiteHandler db;
    private SeekBar seekBar_e,seekBar_g,seekBar_s,seekBar_sc;
    private TextView textView_e,textView_g,textView_s,textView_sc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_event);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(this);

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

        HashMap<String, String> dist_event = db.getDistVal("event");
        String event_val = dist_event.get("dist_val");


        HashMap<String, String> dist_gym = db.getDistVal("gym");
        String gym_val = dist_gym.get("dist_val");


        HashMap<String, String> dist_pool = db.getDistVal("swimming_pool");
        String pool_val = dist_pool.get("dist_val");


        HashMap<String, String> dist_club = db.getDistVal("sports_club");
        String club_val = dist_club.get("dist_val");


        //  db.deleteDists();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        MainActivity mainActivity = new MainActivity();
        mainActivity.setFalse();

        getSupportActionBar().setTitle("Settings");


        seekBar_e = (SeekBar) findViewById(R.id.seekBar1);
        textView_e = (TextView) findViewById(R.id.textView1);
        seekBar_e.setProgress(Integer.valueOf(event_val));

        seekBar_g = (SeekBar) findViewById(R.id.seekBar2);
        textView_g = (TextView) findViewById(R.id.textView2);
        seekBar_g.setProgress(Integer.valueOf(gym_val));

        seekBar_s = (SeekBar) findViewById(R.id.seekBar3);
        textView_s = (TextView) findViewById(R.id.textView3);
        seekBar_s.setProgress(Integer.valueOf(pool_val));

        seekBar_sc = (SeekBar) findViewById(R.id.seekBar4);
        textView_sc = (TextView) findViewById(R.id.textView4);
        seekBar_sc.setProgress(Integer.valueOf(club_val));

        // Initialize the textview with '0'
        textView_e.setText(seekBar_e.getProgress() + "/" + seekBar_e.getMax());
        seekBar_e.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        progress = progresValue;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here,
                        //if you want to do anything at the start of
                        // touching the seekbar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        textView_e.setText(progress + "/" + seekBar.getMax());
                        db.addDistance(uid,"event",String.valueOf(progress));
                    }
                });


        textView_g.setText(seekBar_g.getProgress() + "/" + seekBar_g.getMax());
        seekBar_g.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        progress = progresValue;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here,
                        //if you want to do anything at the start of
                        // touching the seekbar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        textView_g.setText(progress + "/" + seekBar.getMax());
                        db.addDistance(uid, "gym", String.valueOf(progress));
                    }
                });


        textView_s.setText(seekBar_s.getProgress() + "/" + seekBar_s.getMax());
        seekBar_s.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        progress = progresValue;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here,
                        //if you want to do anything at the start of
                        // touching the seekbar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        textView_s.setText(progress + "/" + seekBar.getMax());
                        db.addDistance(uid, "swimming_pool", String.valueOf(progress));
                    }
                });


        textView_sc.setText(seekBar_sc.getProgress() + "/" + seekBar_sc.getMax());
        seekBar_sc.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        progress = progresValue;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here,
                        //if you want to do anything at the start of
                        // touching the seekbar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        textView_sc.setText(progress + "/" + seekBar.getMax());
                        db.addDistance(uid, "sports_club", String.valueOf(progress));
                    }
                });


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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_home:
                //setContentView(R.layout.activity_main);
                if (home_flag) {
                    Toast.makeText(SettingEvent.this, "You are already at Home.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                    Toast.makeText(SettingEvent.this, "Action Home button Clicked.", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_settings:
                Intent intent_setting = new Intent(this, SettingEvent.class);
                startActivity(intent_setting);

                Toast.makeText(SettingEvent.this, "Action Setting button Clicked.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_search:

                Toast.makeText(SettingEvent.this, "Action Search button Clicked.", Toast.LENGTH_SHORT).show();
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

}

