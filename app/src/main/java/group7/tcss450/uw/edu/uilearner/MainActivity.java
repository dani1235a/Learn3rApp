package group7.tcss450.uw.edu.uilearner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import group7.tcss450.uw.edu.uilearner.auth.ChooseRoleFragment;
import group7.tcss450.uw.edu.uilearner.auth.ForgotPasswordFragment;
import group7.tcss450.uw.edu.uilearner.auth.RegisterFragment;
import group7.tcss450.uw.edu.uilearner.auth.SignInFragment;

/**
 * This class is the main activity. It first opens when the app runs. The sign in page
 */
public class MainActivity extends AppCompatActivity implements SignInFragment.OnFragmentInteractionListener,
           RegisterFragment.OnRegisterFragmentInteractionListener,
            ChooseRoleFragment.OnFragmentInteractionListener,
            ForgotPasswordFragment.OnFragmentInteractionListener {

    public static final String TAG = "FIREBASE_TAG";
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private Activity thisActivity;
    private User user;
    private Activity activityReference;

    /**
     * On create method. Pretty self explanatory.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.activityReference = this;

        //If the Intent is not null, that means we got here from the sign out button
        //in AgendaActivity, so sign out of the current FirebaseUser account before
        //doing anything else.
        if (getIntent() != null) {
            signOut();
        }
        if (savedInstanceState == null) {
            if (findViewById(R.id.main_container) != null) {
                loadFragment(new SignInFragment(), null);
            }
        }
        thisActivity = this;
    }



    /**
        A reusable method that simply replaces the current fragment attached to
        the main_container layout in activity_main with the new one given.
     */
    private void loadFragment(Fragment fragment, Bundle args) {
        // dont want to add the first fragment to the backstack.
        if (fragment instanceof SignInFragment) {
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment);
            transaction.commit();
        } else {
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.main_container, fragment);
            transaction.commit();
        }

    }

    /**
     * At a high level, this method creates a new user in FireBase, then once that user is successfully
     * created and signed in, is then 'posted' to the backend database of students and teacher
     * if the user is a student, the @param addCode is used to link the teacher to the new student via
     * a database bridge.
     * If the user is a teacher, then the user is added to the teacher table on the backend, and an
     * addCode is generated for them in the server side code.
     * authors: Myles Haynes, Connor Lundberg
     */
    public void createAccount(final String addCode) {

        new AsyncTask<Void, Void, JSONObject>() {

            private ProgressDialog dialog;

            /**
             * Sets the the progress bar during the calls to the back end to create the account.
             */
            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(thisActivity);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Registering... Please wait");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }

            /**
             * Creates the account in our system.
             * @param voids
             * @return
             */
            @Override
            protected @NonNull JSONObject doInBackground(Void... voids) {

            Log.d(TAG, "In here");

                try {
                    // http://learner-backend.herokuapp.com/teacher?uuid=someUid&name=someName
                    // or
                    // http://learner-backend.herokuapp.com/student?uuid=someUid&name=someName
                    Uri uri;

                    if (user.getRole().equals(ChooseRoleFragment.IS_TEACHER)) {
                        uri = new Uri.Builder()
                                .scheme("http")
                                .authority("learner-backend.herokuapp.com")
                                .appendEncodedPath("teacher")
                                .appendQueryParameter("pass", user.getPassword()) //pass uid here
                                .appendQueryParameter("name", user.getEmail())
                                .build();
                    } else {
                        uri = new Uri.Builder()
                                .scheme("http")
                                .authority("learner-backend.herokuapp.com")
                                .appendEncodedPath("student")
                                .appendQueryParameter("add_code", addCode)
                                .appendQueryParameter("uuid", user.getUid()) //pass uid here
                                .appendQueryParameter("name", user.getEmail())
                                .build();
                    }

                    Log.d(TAG, uri.toString());
                    HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                    connection.setRequestMethod("POST");
                    connection.connect();
                    Scanner s = new Scanner(connection.getInputStream());
                    StringBuilder sb = new StringBuilder();
                    while (s.hasNext()) sb.append(s.next());
                    return new JSONObject(sb.toString());


                } catch (Exception e) {
                    Log.e(TAG, "error creating", e);
                    return new JSONObject();
                }
            }



            /**
             * Removes the Progress bar and changes the activity to the next activity.
             */
            @Override
            protected void onPostExecute(JSONObject res) {
                dialog.dismiss();
                if(res.optBoolean("success")) {
                    user.setUid(res.optString("uid"));
                    user.setRole(res.optString("role"));
                    if(user.getRole().equals("teacher")) {
                        user.setAddCode(res.optString("add_code"));
                    }
                    sendEmailVerification();
                    changeActivity();
                } else {
                    String msg = (res.has("error")) ? res.optString("error") : "Something went wrong...";
                    showOkDialog(msg);
                }
            }

        }.execute();

    }


    /**
        A helper method to switch to the AgendaActivity. If any arguments need to be passed
        to AgendaActivity, it will be passed here using agendaIntent.putExtra(KEY, VALUE).

        Author: Connor Lundberg
     */
    private void changeActivity () {
        Intent agendaIntent = new Intent(this, AgendaActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(TAG, user);
        agendaIntent.putExtra(TAG, args);
        //This clears the back stack.
        agendaIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(agendaIntent);
        finish();
    }


    /**
        Sends a verification email to the one specified by the User on account creation.
        If the email fails to send, then a Toast will appear, alerting the user of it.

        Author: Connor Lundberg
     */
    private void sendEmailVerification() {
        //TODO: Send Email Verification
    }


    /**
        Attempts to sign in a user using the given email and password Strings.
        It first attempts to check if the email and password are valid (using
        isValidEmail and isValidPassword. If they then it will try to sign in.
        If the email is not attached to a user in the user database, or if the
        password is incorrect, it will make a Toast saying it failed.

        If the email and password strings match a user in the user database however,
        it will sign them in as the FirebaseAuth instance's current user, make a
        Toast saying they were successful, and move them onto the next fragment.

        Author: Connor Lundberg
     */
    public void signIn (final String email, final String password) {

        new AsyncTask<Void, Void, JSONObject>() {

            private ProgressDialog dialog;

            private CountDownLatch latch;

            private static final String ASYNC_TAG = "ASYNC_TAG";

            /**
             * Creates the progress bar and shows it.
             */
            @Override
            protected void onPreExecute() {
                latch = new CountDownLatch(1);
                dialog = new ProgressDialog(thisActivity);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Signing In... Please wait");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }

            /**
             * Make the backend calls to see if the password and usename were entered correctly.
             * @param voids
             * @return
             */
            @Override
            protected JSONObject doInBackground(Void... voids) {

                try {
                    String respStr;
                    // http://learner-backend.herokuapp.com/role?uuid=someUid
                    Uri uri = new Uri.Builder()
                            .scheme("http")
                            .authority("learner-backend.herokuapp.com")
                            .appendEncodedPath("login")
                            .appendQueryParameter("pass", user.getPassword())
                            .appendQueryParameter("email", user.getEmail())
                            .build();

                    Log.d(TAG, uri.toString());
                    HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    Scanner s = new Scanner(connection.getInputStream());
                    StringBuilder sb = new StringBuilder();
                    while (s.hasNext()) sb.append(s.next());
                    respStr = sb.toString();
                    return new JSONObject(respStr);

                } catch (Exception e) {
                    Log.e(ASYNC_TAG, "Failed to sign in", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject response) {

                boolean ok = false;
                try {
                    if (response == null) {
                        showOkDialog("Something went wrong...");
                    } else if (response.getBoolean("success")) {
                        Log.d(TAG, response.toString());
                        if(!response.getBoolean("verified")) {
                            showOkDialog("You must verify your email before continuing");
                            ok = false;
                        } else {
                            Log.d(TAG, "Successful: + " + response.toString());
                            String uuid = response.getString("uuid");
                            String role = response.getString("role");
                            user.setRole(role);
                            user.setUid(uuid);
                            ok = true;
                        }
                    } else {
                        Log.d(TAG, response.toString());
                        showOkDialog("Incorrect User & Password");
                    }
                } catch (JSONException json) {
                    Log.e(TAG, json.getMessage(), json);
                    Log.e(TAG, response.toString());
                }
                dialog.dismiss();
                if(ok)
                    changeActivity();
            }

        }.execute();

    }


    /**
        Does a simple sign out of the current user and switches the view back to the original
        login screen.
     */
    public void signOut () {
        //TODO Implement sign out. Probably going to clear an sqllite database stored on device.
    }

    /**
     * Shows a simple AlertDialog.
     * @param message - the message to be displayed.
     */
    public void showOkDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing... We just want them to read the dialog
            }
        });
        builder.show();
    }


    /*
        Because this can be coming from a Teacher sign-in/register or a
        Student sign-in/register, this needs an accountState to differentiate
        the type of operation to do with the account.
     */

    /**
     * Fragment to display to sign the user in.
     * Once the user presses "sign in" this creates the user and
     * attempts to sign them in.
     * @param user
     */
    @Override
    public void SignInFragmentInteraction(User user) {
        this.user = user;
        signIn(this.user.getEmail(), this.user.getPassword());
    }

    /**
     * This is the fragment that is loaded when the user presses the "Register" button
     * on the first screen.
     */
    @Override
    public void SignInRegisterButtonInteraction() {
        loadFragment(new RegisterFragment(), null);
    }

    /**
     * This is for the "Forgot password" fragment to load when the user selects
     * the "Forgot password" text on the first screen.
     */
    @Override
    public void SignInForgotPasswordInteraction() {
        loadFragment(new ForgotPasswordFragment(), null);
    }


    /**
     * Fragment that takes the information in from the registration screen,
     * creates the user, and then opens the ChooseRoleFragment.
     * @param user
     */
    @Override
    public void onRegisterFragmentInteraction(User user) {
        this.user = user;
        loadFragment(new ChooseRoleFragment(), null);
    }

    /**
     * This is the fragment WHere the user selects their role, and it then
     * creates the account after a successful registration.
     * @param role
     * @param addCode
     */
    @Override
    public void onRoleFragmentInteraction(String role, String addCode) {
        user.setRole(role);

        createAccount(addCode);
    }

    /**
     * After the user enters their email, it goes back to the sign in page.
     * @param username
     */
    @Override
    public void onForgotPasswordInteraction(String username) {
        loadFragment(new SignInFragment(), null);
    }


    /**
     * Simple method that returns this activity.
     * @return
     */
    private Activity returnActivity() {
        return this;
    }

}
