package com.fitnessfundoo.SlidingMenuOptions;

/**
 * Created by Anubhav on 15-01-2016.
 */

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fitnessfundoo.app.AppConfig;
import com.fitnessfundoo.app.AppController;
import com.fitnessfundoo.helper.ExpandableListAdapterFrag;

import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.model.Sports;

import com.fitnessfundoo.R;
import com.fitnessfundoo.www.fitnessfundoo.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fitnessfundoo.helper.SessionManager;
public class MyProfile extends Fragment {

    public MyProfile(){}
    ExpandableListAdapterFrag listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Sports>> listDataChild;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private SessionManager sessionManager ;
    String uid;
    private Drawable toolbarBackgroundDrawable;
    private  Toolbar toolbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        super.getActivity();
        setHasOptionsMenu(true);
        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // Session manager
        sessionManager = new SessionManager(getActivity());

        // SQLite database handler
        db = new SQLiteHandler(getActivity());

        ((MainActivity) getActivity()).setFalse();
        //Toolbar toolbar =  (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbarBackgroundDrawable = toolbar.getBackground();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        // enabling action bar app icon and behaving it as toggle button
        if(((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            //   getSupportActionBar().setHomeButtonEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

        final Button subButton = (Button) rootView.findViewById(R.id.submit_button);
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!listAdapter.isEmpty()){
               // listAdapter.displayString();
                pDialog.setMessage("Updating Please Wait");
                showDialog();

                // Tag used to cancel the request
                String tag_string_req = "update_req";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_UPDATE_INTEREST, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("Update Response", "Update Response: " + response.toString());
                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            Log.d("TAG", jObj.toString());
                            boolean error = jObj.getBoolean("error");
                            // Check for error node in json
                            if (!error) {
                                Toast.makeText(getActivity(), "Updated successfully.", Toast.LENGTH_LONG).show();
                                Log.d("value to update", listAdapter.displayString());
                                String interest = listAdapter.displayString().replace("[","");
                                interest = interest.replace("]","");
                                Log.d("value to update", interest);
                                sessionManager.updateInterest(interest);

                                subButton.setText("Added");
                            }
                        }catch (JSONException e){
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                },new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Update error", "Update Error: " + error.getMessage());
                        Toast.makeText(getActivity(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("interest", listAdapter.displayString());
                        params.put("id", uid);

                        return params;
                    }
                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

            //    Toast.makeText(getActivity(), listAdapter.displayString() + " for : " + uid , Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getActivity(), "Nothing Selected." , Toast.LENGTH_SHORT).show();
            }
            }
        });

        // get the listview
        expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapterFrag(getActivity().getApplicationContext(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            //    Toast.makeText(getActivity().getApplicationContext(),
              //          listDataHeader.get(groupPosition) + " Expanded",
               //         Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
            //    Toast.makeText(getActivity().getApplicationContext(),
              //          listDataHeader.get(groupPosition) + " Collapsed",
                //        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition)._title, Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
        return rootView;
    }

    /*
 * Preparing the list data
 */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Sports>>();

        // Adding child data
        listDataHeader.add("Baseball");
        listDataHeader.add("Basket");
        listDataHeader.add("Cricket");
        listDataHeader.add("Combat");
        listDataHeader.add("Cycle");
        listDataHeader.add("Dance");
        listDataHeader.add("Football");
        listDataHeader.add("Golf");
        listDataHeader.add("Gym");
        listDataHeader.add("Handball");
        listDataHeader.add("Hockey");
        listDataHeader.add("Indoors");
        listDataHeader.add("Kabaddi");
        listDataHeader.add("Motor");
        listDataHeader.add("Others");
        listDataHeader.add("Racket");
        listDataHeader.add("Rugby");
        listDataHeader.add("Run");
        listDataHeader.add("Shooting");
        listDataHeader.add("Volleyball");
        listDataHeader.add("Water");
        listDataHeader.add("Winter");

        // Adding child data
        List<Sports> Baseball = new ArrayList<Sports>();
        Baseball.add(new Sports("Baseball",R.drawable.baseball));

        List<Sports> Basket = new ArrayList<Sports>();
        Basket.add(new Sports("Basketball",R.drawable.basketball));
        Basket.add(new Sports("Korfball",R.drawable.basketball));

        List<Sports> Cricket = new ArrayList<Sports>();
        Cricket.add(new Sports("Cricket",R.drawable.cricket));
        Cricket.add(new Sports("Gulley Cricket",R.drawable.cricket));

        List<Sports> Combat = new ArrayList<Sports>();
        Combat.add(new Sports("Judo",R.drawable.back_muscles));
        Combat.add(new Sports("Kung Fu",R.drawable.championship_belt));
        Combat.add(new Sports("Boxing",R.drawable.boxing));
        Combat.add(new Sports("Karate",R.drawable.shunt));
        Combat.add(new Sports("MMA(Mixed Martial Arts)",R.drawable.back_muscles));
        Combat.add(new Sports("Kickboxing",R.drawable.leg));
        Combat.add(new Sports("Taekwondo",R.drawable.helmel));
        Combat.add(new Sports("Fencing",R.drawable.helmel));

        List<Sports> Cycle = new ArrayList<Sports>();
        Cycle.add(new Sports("Cycling",R.drawable.biking));
        Cycle.add(new Sports("MTB(BTT)",R.drawable.time_trial_biking));
        Cycle.add(new Sports("Cyclo-Tourism",R.drawable.mountain_biking));

        List<Sports> Dance = new ArrayList<Sports>();
        Dance.add(new Sports("Salsa",R.drawable.dancing));
        Dance.add(new Sports("Zumba",R.drawable.dancing));

        List<Sports> Football = new ArrayList<Sports>();
        Football.add(new Sports("Football 11",R.drawable.ball));
        Football.add(new Sports("Football 7",R.drawable.football_32));
        Football.add(new Sports("Fustal",R.drawable.football2_32));
        Football.add(new Sports("Football 5",R.drawable.ball));
        Football.add(new Sports("Footvolley",R.drawable.football_32));


        List<Sports> Golf = new ArrayList<Sports>();
        Golf.add(new Sports("Golf",R.drawable.golf));


        List<Sports> Gym = new ArrayList<Sports>();
        Gym.add(new Sports("Aerobics",R.drawable.acrobatics));
        Gym.add(new Sports("Fitness",R.drawable.back_muscles));
        Gym.add(new Sports("Pilates",R.drawable.weightlift));
        Gym.add(new Sports("BodyBuilder",R.drawable.biceps));
        Gym.add(new Sports("Yoga",R.drawable.yoga));
        Gym.add(new Sports("Crossfit",R.drawable.energy_absorber));

        List<Sports> Handball = new ArrayList<Sports>();
        Handball.add(new Sports("Handball",R.drawable.handball));

        List<Sports> Hockey = new ArrayList<Sports>();
        Hockey.add(new Sports("Hockey",R.drawable.carabiner));

        List<Sports> Indoors = new ArrayList<Sports>();
        Indoors.add(new Sports("Chess",R.drawable.crafting));
        Indoors.add(new Sports("Bowling",R.drawable.bowling));
        Indoors.add(new Sports("Darts",R.drawable.crafting));
        Indoors.add(new Sports("Snooker",R.drawable.crafting));
        Indoors.add(new Sports("Cards",R.drawable.cards));
        Indoors.add(new Sports("Poker",R.drawable.cards));

        List<Sports> Kabaddi = new ArrayList<Sports>();
        Kabaddi.add(new Sports("Kabaddi",R.drawable.leg));

        List<Sports> Motor = new ArrayList<Sports>();
        Motor.add(new Sports("Kart",R.drawable.car));
        Motor.add(new Sports("MotorCycling",R.drawable.time_trial_biking));
        Motor.add(new Sports("Motoring",R.drawable.mountain_biking));

        List<Sports> Others = new ArrayList<Sports>();
        Others.add(new Sports("Horseback",R.drawable.horse));
        Others.add(new Sports("Polo",R.drawable.shoulders));
        Others.add(new Sports("Rock Climbing",R.drawable.climbing));

        List<Sports> Racket = new ArrayList<Sports>();
        Racket.add(new Sports("Badminton",R.drawable.tennis));
        Racket.add(new Sports("Squash",R.drawable.tennis));
        Racket.add(new Sports("Tennis",R.drawable.tennis));
        Racket.add(new Sports("Table Tennis",R.drawable.table_tennis));


        List<Sports> Rugby = new ArrayList<Sports>();
        Rugby.add(new Sports("Rugby",R.drawable.rugby));

        List<Sports> Run = new ArrayList<Sports>();
        Run.add(new Sports("Athletics",R.drawable.aethletics));
        Run.add(new Sports("Running",R.drawable.aethletics));
        Run.add(new Sports("Walking",R.drawable.walking));
        Run.add(new Sports("Jogging",R.drawable.walking));

        List<Sports> Shooting = new ArrayList<Sports>();
        Shooting.add(new Sports("Airsoft",R.drawable.ball));
        Shooting.add(new Sports("Paintball",R.drawable.ball));
        Shooting.add(new Sports("Precision Shooting",R.drawable.shoulders));
        Shooting.add(new Sports("Archery",R.drawable.archery));

        List<Sports> Volleyball = new ArrayList<Sports>();
        Volleyball.add(new Sports("Beach Volleyball",R.drawable.volleyball));
        Volleyball.add(new Sports("Volleyball",R.drawable.volleyball));

        List<Sports> Water = new ArrayList<Sports>();
        Water.add(new Sports("BodyBoard",R.drawable.swimming));
        Water.add(new Sports("Sailing",R.drawable.swimming));
        Water.add(new Sports("Surf",R.drawable.swimming));
        Water.add(new Sports("Canoeing",R.drawable.swimming));
        Water.add(new Sports("Scuba Diving",R.drawable.swimming));
        Water.add(new Sports("Swimming",R.drawable.swimming));
        Water.add(new Sports("Water Polo",R.drawable.swimming));

        List<Sports> Winter = new ArrayList<Sports>();
        Winter.add(new Sports("Ski",R.drawable.skiing));
        Winter.add(new Sports("SnowBoard",R.drawable.shunt));

        listDataChild.put(listDataHeader.get(0), Baseball); // Header, Child data
        listDataChild.put(listDataHeader.get(1), Basket);
        listDataChild.put(listDataHeader.get(2), Cricket);
        listDataChild.put(listDataHeader.get(3), Combat);
        listDataChild.put(listDataHeader.get(4), Cycle);
        listDataChild.put(listDataHeader.get(5), Dance);
        listDataChild.put(listDataHeader.get(6), Football);
        listDataChild.put(listDataHeader.get(7), Golf);
        listDataChild.put(listDataHeader.get(8), Gym);
        listDataChild.put(listDataHeader.get(9), Handball);
        listDataChild.put(listDataHeader.get(10), Hockey);
        listDataChild.put(listDataHeader.get(11), Indoors);
        listDataChild.put(listDataHeader.get(12), Kabaddi);
        listDataChild.put(listDataHeader.get(13), Motor);
        listDataChild.put(listDataHeader.get(14), Others);
        listDataChild.put(listDataHeader.get(15), Racket);
        listDataChild.put(listDataHeader.get(16), Rugby);
        listDataChild.put(listDataHeader.get(17), Run);
        listDataChild.put(listDataHeader.get(18), Shooting);
        listDataChild.put(listDataHeader.get(19), Volleyball);
        listDataChild.put(listDataHeader.get(20), Water);
        listDataChild.put(listDataHeader.get(21), Winter);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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
