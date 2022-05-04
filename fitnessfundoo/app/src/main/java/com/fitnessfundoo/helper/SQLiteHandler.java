/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
//package info.androidhive.loginandregistration.helper;
package com.fitnessfundoo.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "fitnessfundoo";

    // Login table name
    private static final String TABLE_USER = "user";

    //Dist_pref table
    private static final String TABLE_DIST_PREF = "dist_pref";

    //Dist_pref table columns names
    private static final String KEY_DIST_ID = "_id";
    private static final String KEY_INDC = "_indc";
    private static final String KEY_DIST_VAL = "dist_val";
    private static final String KEY_USER_ID = "user_id";


    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "date_created";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_DIST_PREF = "CREATE TABLE " + TABLE_DIST_PREF + "("
                + KEY_DIST_ID + " INTEGER PRIMARY KEY," + KEY_INDC + " TEXT,"
                + KEY_DIST_VAL + " TEXT," + KEY_USER_ID + " TEXT )";
        db.execSQL(CREATE_DIST_PREF);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIST_PREF);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String uid, String date_created) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, date_created); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("date_created", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public void deleteDists() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_DIST_PREF, null, null);
        db.close();

        Log.d(TAG, "Deleted all distance info from sqlite");
    }


    public void addDistance(String user_id, String indc_val, String disc_val) {
        SQLiteDatabase db = this.getWritableDatabase();
        String val = "'" + indc_val + "'";
        String selectQuery = "SELECT  dist_val FROM " + TABLE_DIST_PREF + " WHERE " +  KEY_INDC + "=" + val;
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put(KEY_DIST_VAL, disc_val);
            db.update(TABLE_DIST_PREF, values,  KEY_INDC + " = " + val, null);
            Log.d(TAG, "New distance updated into sqlite: " +disc_val );
        }else {
            ContentValues values = new ContentValues();
            values.put(KEY_INDC, indc_val); // Name
            values.put(KEY_DIST_VAL, disc_val); // Email
            values.put(KEY_USER_ID, user_id); // Email

            // Inserting Row
            long id = db.insert(TABLE_DIST_PREF, null, values);
            Log.d(TAG, "New distance inserted into sqlite: " + id);
        }
        cursor.close();
        db.close(); // Closing database connection


    }

    public HashMap<String, String> getDistVal(String indc_val) {
        HashMap<String, String> dist = new HashMap<String, String>();
        String val = "'" + indc_val + "'";
        String selectQuery = "SELECT  dist_val FROM " + TABLE_DIST_PREF + " WHERE " +  KEY_INDC + " = "+ val ;
        Log.d("select query " ,selectQuery );
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
           Log.d("cursor",cursor.toString());
            dist.put("dist_val", cursor.getString(0));
//            dist.put("abc", cursor.getString(2));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + dist.toString());

        return dist;
    }


}
