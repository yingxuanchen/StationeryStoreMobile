package com.yingxuan.stationerystore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    //Sharedpref mode
    int PRIVATE_MODE = 0;

    //shared pref file name
    private static final String PREF_NAME = "StatStorePref";

    //all shared pref keys
    private static final String LOGGED_IN = "IsLoggedIn";

    //public access variables (accessible from outside/global)
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";

    //Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    //create Login session
    public void createLoginSession(String name, String email) {
        //login value true
        editor.putBoolean(LOGGED_IN, true);
        //store name n email in pref
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);

        //commit changes
        editor.commit();
    }

    //To get stored session data
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        //user's name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        return user;
    }

    //Clear session details
    public void logOutUser() {

        //Clear all data from sharedprefs
        editor.clear();
        editor.commit();

        //redirect user to login activity after logging out
        Intent i = new Intent(_context, MainActivity.class);
        //close all activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //start new activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //start login activity
        _context.startActivity(i);

    }

    //Check: get login state
    public boolean isLoggedIn() {
        return pref.getBoolean(LOGGED_IN, false);
    }

}
