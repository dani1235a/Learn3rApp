package group7.tcss450.uw.edu.uilearner.util;

import android.content.Context;
import android.preference.PreferenceManager;

import java.io.Serializable;

/**
 * Created by Daniel on 12/5/2017.
 * Class for keeping the user signed in when they open the app.
 * @author Daniel
 */

public class SharedPreferences implements Serializable{
    static final String PREF_USER_PASSWORD = "password";
    static final String PREF_USER_NAME= "username";

    /**
     * Returns the SharedPreferences.
     * @param ctx
     * @return
     * @author Daniel
     */
    static android.content.SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    /**
     * Sets the username field of the SharedPreferences to the one provided.
     * @param ctx
     * @param userName
     * @author Daniel
     */
    public static void setUserName(Context ctx, String userName) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    /**
     * Sets the username field of the SharedPreferences to the one provided.
     * @param ctx
     * @param pass
     * @author Daniel
     */
    public static void setUserPassword(Context ctx, String pass) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_PASSWORD, pass);
        editor.commit();
    }


    /**
     * Retrieves the user's password if set in the SharedPreferences. This is used for
     * automatic sign in.
     * @param ctx
     * @return
     * @author Daniel
     */
    public static String getUserPass(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_PASSWORD, "");
    }


    /**
     * Retrieves the user's username if set in the Shared Preferences. This is used for
     * automatic sign in.
     * @param ctx
     * @return
     */
    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    /**
     * Clears the information stored in the SharedPreferences for this app. This happens on
     * sign out so that the next time the app is opened (if the user hasn't already signed back
     * in) it will take them to the sign in screen first.
     * @param ctx
     * @author Daniel
     */
    public static void clearData(Context ctx) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }

}
