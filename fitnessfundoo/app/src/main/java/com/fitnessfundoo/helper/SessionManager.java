//package info.androidhive.loginandregistration.helper;

package com.fitnessfundoo.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.fitnessfundoo.model.User;
import com.fitnessfundoo.www.fitnessfundoo.LoginActivity;

import java.util.HashMap;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "FitnessfundooLogin";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    public static final String     KEY_USER_ID = "id";

    public static final String KEY_NAME = "name";

    public static final String KEY_EMAIL = "email";

    //public static final String KEY_GENDER = "gender";

    public static final String KEY_DP_URL = "dp_url";

    public static final String KEY_INTEREST = "interest";

    private static final String KEY_NOTIFICATIONS = "notifications";


  //  public static final String KEY_FAV_COUNT = "fav_count";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public User getUser() {
        if (pref.getString(KEY_USER_ID, null) != null) {
            String id, name, email;
            id = pref.getString(KEY_USER_ID, null);
            name = pref.getString(KEY_NAME, null);
            email = pref.getString(KEY_EMAIL, null);

            User user = new User(id, name, email);
            return user;
        }
        return null;
    }

    public void storeUser(User user) {
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.commit();

        Log.e(TAG, "User is stored in shared preferences. " + user.getName() + ", " + user.getEmail());
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void updateInterest(String interest){
        // Storing interest in pref
        editor.remove(KEY_INTEREST);
        Log.d("in session manager : ", interest);
        editor.putString(KEY_INTEREST, interest);
        Log.d("in session manager : ", interest);
        // commit changes
        editor.commit();
    }

/*    public void updateFavCount(String count){
        // Storing interest in pref
      //  editor.remove(KEY_FAV_COUNT);
        Log.d("in session manager : ", count);
        editor.putString(KEY_FAV_COUNT, count);
       // Log.d("in session manager : ", count);
        // commit changes
        editor.commit();
    }
*/

    public void updateName(String name){
        // Storing interest in pref
        editor.remove(KEY_NAME);
        // Storing name in pref
        editor.putString(KEY_NAME, name);
        // commit changes
        editor.commit();

    }

    public void setLogin(String name,String email,String dp_url,String interest) {

        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing dp_url in pref
        editor.putString(KEY_DP_URL, dp_url);

        // Storing interest in pref
        editor.putString(KEY_INTEREST, interest);

        Log.d("in session manager : ", interest);
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
        Log.d(KEY_NAME, name);
        Log.d(KEY_DP_URL, dp_url);
        Log.d(KEY_INTEREST, interest);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user dp_url
        user.put(KEY_DP_URL, pref.getString(KEY_DP_URL, null));

        // user interest
        user.put(KEY_INTEREST, pref.getString(KEY_INTEREST, null));

        // user fav_count
 //       user.put(KEY_FAV_COUNT, pref.getString(KEY_FAV_COUNT, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

}
