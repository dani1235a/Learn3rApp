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
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import group7.tcss450.uw.edu.uilearner.SignIn_Registration.ChooseRoleFragment;
import group7.tcss450.uw.edu.uilearner.SignIn_Registration.ForgotPasswordFragment;
import group7.tcss450.uw.edu.uilearner.SignIn_Registration.RegisterFragment;
import group7.tcss450.uw.edu.uilearner.SignIn_Registration.SignInFragment;

public class MainActivity extends AppCompatActivity implements SignInFragment.OnFragmentInteractionListener,
           RegisterFragment.OnRegisterFragmentInteractionListener,
            ChooseRoleFragment.OnFragmentInteractionListener,
            ForgotPasswordFragment.OnFragmentInteractionListener {

    public static final String TAG = "FIREBASE_TAG";

    private FirebaseAuth mAuth;
    private Activity thisActivity;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private User user;
    private Activity activityReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.activityReference = this;
        //Gets the current instance of FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Doesn't do much more than check if a user is already signed in.
                if (user != null) {
                    Log.d("FIREBASE", "Signed in user id: " + user.getUid());
                } else {
                    Log.d("FIREBASE", "User signed out");
                }
            }
        };


        if (savedInstanceState == null) {
            if (findViewById(R.id.main_container) != null) {
                loadFragment(new SignInFragment(), null);
            }
        }
        thisActivity = this;
    }



    /*
        A reusable method that simply replaces the current fragment attached to
        the main_container layout in activity_main with the new one given.
     */
    private void loadFragment(Fragment fragment, Bundle args) {
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.main_container, fragment);
        transaction.commit();
    }



    /*
        Creates a new user account with the given email and password
        Strings. If they are both valid (meaning the email contains a
        '@' and corresponding '.' after to specify the domain, and the
        password is at least 6 chars long) then it will create the account.

        If the email is in an invalid format (a separate check within
        createUserWithEmailAndPassword) or the email is already in use by
        another User, the creation will fail and a Toast will be made stating
        so. Otherwise, the user is created in the Firebase console and the
        FirebaseAuth instance's current user is set to the one just created
        (automatic sign in).

        Author: Connor Lundberg
     */
    public void createAccount (final String email, final String password) {

        new AsyncTask<Void, Void, Boolean>() {

            private ProgressDialog dialog;

            private CountDownLatch latch;

            @Override
            protected void onPreExecute() {
                latch = new CountDownLatch(1);
                dialog = new ProgressDialog(thisActivity);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Registering... Please wait");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... voids) {

                Log.d(TAG, "In here");

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(thisActivity, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
                                            Log.w(TAG, "createUserWithEmailAndPassword:failed", task.getException());
                                            AlertDialog.Builder alert = new AlertDialog.Builder(activityReference);
                                            alert.setTitle("Failed to register");
                                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    loadFragment(new RegisterFragment(), null);
                                                }
                                            });
                                            latch.countDown();
                                            alert.show();

                                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.i(TAG, "User creation completed and was successful");
                                            Toast.makeText(MainActivity.this, R.string.auth_passed,
                                                    Toast.LENGTH_SHORT).show();


                                            //If the user has been created and signed in, the Display Fragment
                                            //will be switched to.
                                            user.setUid(mAuth.getCurrentUser().getUid());
                                            Toast.makeText(getApplicationContext(), "Role has been set for this User!", Toast.LENGTH_LONG).show();

                                /*if (mAuth.getCurrentUser().isEmailVerified()) {
                                    Log.d(TAG, "changing activities");
                                    changeActivity();
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.verify_first,
                                            Toast.LENGTH_SHORT).show();
                                }*/
                                        }
                                    }
                                }
                        );
                try {
                    latch.await(10, TimeUnit.SECONDS);
                    try {
                        // http://learner-backend.herokuapp.com/teacher?uuid=someUid&name=someName
                        // or
                        // http://learner-backend.herokuapp.com/student?uuid=someUid&name=someName
                        Uri uri;
                        String response;
                        if (user.getRole().equals(ChooseRoleFragment.IS_TEACHER)) {
                            uri = new Uri.Builder()
                                    .scheme("http")
                                    .authority("learner-backend.herokuapp.com")
                                    .appendEncodedPath("teacher")
                                    .appendQueryParameter("uuid", user.getUid()) //pass uid here
                                    .appendQueryParameter("name", user.getEmail())
                                    .build();
                        } else {
                            uri = new Uri.Builder()
                                    .scheme("http")
                                    .authority("learner-backend.herokuapp.com")
                                    .appendEncodedPath("student")
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
                        while(s.hasNext()) sb.append(s.next());
                        response = sb.toString();
                        Log.d(TAG, "here");
                        Log.d(TAG, response);
                        return Boolean.getBoolean(response);

                    } catch (Exception e) {
                        Log.e(TAG, "error creating", e);
                        JSONObject obj = new JSONObject();
                        try {
                            obj.accumulate("error", e.getMessage());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean wasSuccessful) {
                dialog.dismiss();
                if(wasSuccessful) {
                    sendEmailVerification();
                    changeActivity();
                } else {
                    showOkDialog(activityReference, "Failed to create user in database");
                    mAuth.getCurrentUser().delete();
                }
            }

        }.execute();

}


    /*
        A helper method to switch to the AgendaActivity. If any arguments need to be passed
        to AgendaActivity, it will be passed here using agendaIntent.putExtra(KEY, VALUE).

        Author: Connor Lundberg
     */
    private void changeActivity () {
        Intent agendaIntent = new Intent(this, AgendaActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(TAG, user);
        agendaIntent.putExtra(TAG, args);
        startActivity(agendaIntent);
    }


    /*
        Sends a verification email to the one specified by the User on account creation.
        If the email fails to send, then a Toast will appear, alerting the user of it.

        Author: Connor Lundberg
     */
    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Verification email failed to send",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /*
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

        new AsyncTask<Void, Void, Boolean>() {

            private ProgressDialog dialog;

            private CountDownLatch latch;

            private static final String ASYNC_TAG = "ASYNC_TAG";

            @Override
            protected void onPreExecute() {
                Log.d(ASYNC_TAG, "onPreExecute()");
                latch = new CountDownLatch(1);
                dialog = new ProgressDialog(thisActivity);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Signing In... Please wait");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                final AtomicBoolean signedIn = new AtomicBoolean();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(thisActivity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("FIREBASE", "signInWithEmail:onComplete:" + task.isSuccessful());
                                latch.countDown();
                                signedIn.set(task.isSuccessful());
                            }
                        });
                try {
                    //When onCompleteListener finishes, then we can proceed to do our work.
                    latch.await(20, TimeUnit.SECONDS);
                    if(signedIn.get() && mAuth.getCurrentUser().isEmailVerified()) {
                        try {
                            String response;
                            // http://learner-backend.herokuapp.com/role?uuid=someUid
                            Uri uri = new Uri.Builder()
                                    .scheme("http")
                                    .authority("learner-backend.herokuapp.com")
                                    .appendEncodedPath("role")
                                    .appendQueryParameter("uuid", user.getUid()) //pass uid here
                                    .build();

                            Log.d(TAG, uri.toString());
                            HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                            connection.setRequestMethod("GET");
                            connection.connect();
                            Scanner s = new Scanner(connection.getInputStream());
                            StringBuilder sb = new StringBuilder();
                            while(s.hasNext()) sb.append(s.next());
                            //The response is the role
                            response = sb.toString();
                            user.setRole(response);
                            Log.d(TAG, "here");
                            Log.d(TAG, response);
                            user.setUid(mAuth.getCurrentUser().getUid());
                        } catch (Exception e) {
                            Log.e(ASYNC_TAG, "Failed to sign in", e);

                        }
                    }
                } catch (InterruptedException e) {
                    Log.e(ASYNC_TAG, e.getMessage());
                }
                return signedIn.get();
            }

            @Override
            protected void onPostExecute(Boolean correctCredentials) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user == null) {
                    showOkDialog(activityReference, "Something went wrong...");
                    return;
                }
                dialog.dismiss();
                if(correctCredentials && user.isEmailVerified()) {
                    changeActivity();
                } else if(correctCredentials) {
                    //If we're here, it means that they entered correct credentials, but they're not verified.
                    showOkDialog(activityReference, "You must verify your email before continuing");
                } else {
                    //They entered invalid credentials.
                    showOkDialog(activityReference, "Invalid email or password");
                }

            }

        }.execute();

    }


    /*
        Does a simple sign out of the current user and switches the view back to the original
        login screen.
     */
    public void signOut () {
        mAuth.signOut();
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(MainActivity.this, R.string.auth_failed,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, R.string.auth_passed,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void showOkDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message);
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
    @Override
    public void SignInFragmentInteraction(User user) {
        this.user = user;
        signIn(this.user.getEmail(), this.user.getPassword());
    }

    @Override
    public void SignInRegisterButtonInteraction() {
        loadFragment(new RegisterFragment(), null);
    }

    @Override
    public void SignInForgotPasswordInteraction() {
        loadFragment(new ForgotPasswordFragment(), null);
    }


    @Override
    public void onRegisterFragmentInteraction(User user) {
        this.user = user;
        loadFragment(new ChooseRoleFragment(), null);
    }

    @Override
    public void onRoleFragmentInteraction(String role) {
        user.setRole(role);

        createAccount(user.getEmail(), user.getPassword());
    }

    @Override
    public void onForgotPasswordInteraction(String username) {
        loadFragment(new SignInFragment(), null);
    }

}
