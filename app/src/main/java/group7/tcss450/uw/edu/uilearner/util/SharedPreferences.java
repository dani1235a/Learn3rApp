package group7.tcss450.uw.edu.uilearner.util;

import android.content.Context;
import android.preference.PreferenceManager;

import java.io.Serializable;

/**
 * Created by Daniel on 12/5/2017.
 * Class for keeping the user signed in when they open the app.
 */

public class SharedPreferences implements Serializable{
    static final String PREF_USER_PASSWORD = "password";
    static final String PREF_USER_NAME= "username";

    static android.content.SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static void setUserPassword(Context ctx, String pass) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_PASSWORD, pass);
        editor.commit();
    }

    public static String getUserPass(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_PASSWORD, "");
    }

    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static void clearData(Context ctx) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }

}
