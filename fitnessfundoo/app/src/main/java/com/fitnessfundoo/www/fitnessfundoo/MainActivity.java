package com.fitnessfundoo.www.fitnessfundoo;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.fitnessfundoo.R;
import com.fitnessfundoo.SlidingMenuOptions.ChatRooms;
import com.fitnessfundoo.SlidingMenuOptions.EventsNearBy;
import com.fitnessfundoo.SlidingMenuOptions.FavoriteFacilities;
import com.fitnessfundoo.SlidingMenuOptions.MyEvents;
import com.fitnessfundoo.SlidingMenuOptions.MyProfile;
import com.fitnessfundoo.SlidingMenuOptions.SendFeedback;
import com.fitnessfundoo.SlidingMenuOptions.*;
import com.fitnessfundoo.adapter.NavDrawerListAdapter;
import com.fitnessfundoo.helper.CircleTransform;
import com.fitnessfundoo.helper.SQLiteHandler;
import com.fitnessfundoo.helper.SessionManager;
import com.fitnessfundoo.model.NavDrawerItem;
import com.fitnessfundoo.newActivity.CreateEvent;
import com.fitnessfundoo.newActivity.SettingEvent;


import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private SQLiteHandler db;
    private SessionManager session;
    private String count;
    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;
    private Drawable toolbarBackgroundDrawable,toolbarBackgroundDrawable1;
    private ProgressDialog pDialog;
    Menu menu;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter sAdapter;
    LinearLayout drawerll;
    public boolean home_flag ;
    private  HashMap<String, String> user;
    private String uid;
    private  Toolbar toolbar;
    private TabLayout tabLayout;

    //added for hard link of fb

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar =  findViewById(R.id.toolbar_main);
        toolbarBackgroundDrawable = toolbar.getBackground();
        setSupportActionBar(toolbar);
        this.setTrue();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        final ViewPager viewPager=(ViewPager)findViewById(R.id.pager);
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(),getApplicationContext());
        viewPager.setAdapter(pagerAdapter);
        tabLayout=findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
       //tab layout changes are finished

        //Sliding menu start
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        drawerll = (LinearLayout) findViewById(R.id.drawerll);

        // SqLite database handler
        db = new SQLiteHandler(this);
        // Fetching user details from SQLite
        HashMap<String, String> user1 = db.getUserDetails();
        uid = user1.get("uid");

        // session manager
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            session.logoutUser();
            db.deleteUsers();
        }
        // Fetching user details from SQLite
        user = session.getUserDetails();

        String uName = user.get(SessionManager.KEY_NAME);

        String dp_url = user.get(SessionManager.KEY_DP_URL);
        // email
        //String email = user.get(SessionManager.KEY_EMAIL);

        TextView txtName = (TextView) findViewById(R.id.uName);
        // Displaying the user details on the screen
        txtName.setText(uName);

        // Loader image - will be shown before loading image
//        int loader = R.drawable.user;
        // Imageview to show
        ImageView image = (ImageView) findViewById(R.id.user_image);

        // Loading profile image
        Glide.with(this).load(dp_url)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image);

        TextView view_profile = (TextView) findViewById(R.id.desc);
        view_profile.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View view) {
                                                Fragment fragment;
                                                fragment = new ViewProfile();
                                                if (fragment != null) {
                                                    FragmentManager fragmentManager = getFragmentManager();
                                                    fragmentManager.beginTransaction()
                                                            .replace(R.id.frame_container, fragment).commit();
                                                    // update selected item and title, then close the drawer
                                                    mDrawerList.setItemChecked(-1, true);
                                                    mDrawerList.setSelection(-1);
                                                    setTitle("View Profile");
                                                    mDrawerLayout.closeDrawer(drawerll);
                                                } else {
                                                    // error in creating fragment
                                                    Log.e("SlidingMenu", "Error in creating fragment");
                                                }
                                                //Toast.makeText(MainActivity.this, "You Clicked View Profile Button.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
        );

        navDrawerItems = new ArrayList<>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        // Communities, Will add a counter here

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));

        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));
        // What's hot, We  will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[10], navMenuIcons.getResourceId(10, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        sAdapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(sAdapter);



        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, //nav menu toggle icon
                R.string.drawer_open, // nav drawer open - description for accessibility
                R.string.drawer_close // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    public void onResume(){
        toolbar =  findViewById(R.id.toolbar_main);
        toolbarBackgroundDrawable = toolbar.getBackground();
        super.onResume();
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            // display view for selected nav drawer item
            displayView(position);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                //fragment = new Home();
                if(home_flag) {
                    Toast.makeText(MainActivity.this, "You are already at Home.", Toast.LENGTH_SHORT).show();
                    mDrawerLayout.closeDrawer(drawerll);
                }
                else {
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                    //    Toast.makeText(MainActivity.this, "Action Home button Clicked.", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                fragment = new MyProfile();
                //  Toast.makeText(MainActivity.this, "You have Clicked on My Profile Button.", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                fragment = new MyEvents();
                //  Toast.makeText(MainActivity.this, "You have clicked on My Events Button.", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                fragment = new SportsEventsFragment();
                //  Toast.makeText(MainActivity.this, "You have clicked on My Events Button.", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                fragment = new EventsNearBy();
                //  Toast.makeText(MainActivity.this, "You have clicked on My Events Button.", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                fragment = new SportsFacilitiesFragment();
                //  Toast.makeText(MainActivity.this, "You have clicked on My Events Button.", Toast.LENGTH_SHORT).show();
                break;

            case 6:
                Intent intent_setting = new Intent(this, ChatRooms.class);
                startActivity(intent_setting);

//                fragment = new ChatRooms();
                //  Toast.makeText(MainActivity.this, "You have clicked on All Events button.", Toast.LENGTH_SHORT).show();
                break;
            case 7:
                fragment = new FavoriteFacilities();
                //  Toast.makeText(MainActivity.this, "You have clicked Favorite Facilities.", Toast.LENGTH_SHORT).show();
                break;
            case 8:
                fragment = new FavoriteEvents();
                //  Toast.makeText(MainActivity.this, "You have clicked Favorite Facilities.", Toast.LENGTH_SHORT).show();
                break;
            case 9:
                fragment = new SendFeedback();
                //  Toast.makeText(MainActivity.this, "You have clicked on Feedback button.", Toast.LENGTH_SHORT).show();
                break;
            case 10:
                //fragment = new SignOut();
                // SqLite database handler
                db = new SQLiteHandler(getApplicationContext());

                // session manager
                session = new SessionManager(getApplicationContext());

                if (!session.isLoggedIn()) {
                    pDialog.setMessage("Invalid Login,Login Again ...");
                    showDialog();
                    session.logoutUser();
                    db.deleteUsers();
                    finish();
                }else {
                    // logoutUser();
                    pDialog.setMessage("Logging Out ...");
                    showDialog();
                    // LoginManager.getInstance().logOut();
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    if(accessToken != null){
                        LoginManager.getInstance().logOut();
                    }
                    session.logoutUser();
                    db.deleteUsers();
                    finish();
                }
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(drawerll);
        } else {
            // error in creating fragment
            Log.e("SlidingMenu", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
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
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_home:
                //setContentView(R.layout.activity_main);
                if(home_flag) {
                    Toast.makeText(MainActivity.this, "You are already at Home.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                    finish();
                    //     Toast.makeText(MainActivity.this, "Action Home button Clicked.", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_settings:
                // Toast.makeText(MainActivity.this, "Action Setting button Clicked.", Toast.LENGTH_SHORT).show();
                Intent intent_setting = new Intent(this, SettingEvent.class);
                startActivity(intent_setting);

                return true;
            case R.id.action_search:
               /* Intent search_intent = new Intent(this, SearchResultsActivity.class);
                startActivity(search_intent);
*/

          //      Toast.makeText(MainActivity.this, "Action Search button Clicked.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.create_event:
                String interest = user.get(SessionManager.KEY_INTEREST);
                Log.d("inerest",interest);
                if(interest != null) {
                    Intent intent = new Intent(this, CreateEvent.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, "Please add some favourite sports First.", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */

    public void setTrue(){
        this.home_flag = true;
    }
    public void setFalse(){
        this.home_flag = false;
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
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


}