package com.ulima.carpool.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ulima.carpool.LoginActivity;

import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 * Created by Julian on 23/08/2016.
 */
public class SessionManager {

    SharedPreferences pref;
    SharedPreferences pref1;
    SharedPreferences pref2;

    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor1;
    SharedPreferences.Editor editor2;

    Context _context;

    int PRIVATE_MODE=0;

    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_EMAIL="email";
    public static final String KEY_PSW="psw";
    public static final String KEY_PESOS="pesos";

    public SessionManager(Context context){
        this._context = context;

        pref = _context.getSharedPreferences(KEY_EMAIL, PRIVATE_MODE);
        pref1 = _context.getSharedPreferences(KEY_PESOS, PRIVATE_MODE);
        pref2=_context.getSharedPreferences(KEY_PSW,PRIVATE_MODE);
        editor = pref.edit();
        editor1=pref1.edit();
        editor2=pref2.edit();
    }

    public void createLoginSession(String email){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);


        // commit changes
        editor.commit();

    }
    
    public void createPesos(String pesos){

        editor1.putString(KEY_PESOS,pesos);

        editor1.commit();
    }

    public void createPsw(String psw){
        editor2.putString(KEY_PSW,psw);
        editor2.commit();
    }

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

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_PESOS, pref1.getString(KEY_PESOS, null));
        user.put(KEY_PSW,pref2.getString(KEY_PSW,null));

        // return user
        return user;
    }

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

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
