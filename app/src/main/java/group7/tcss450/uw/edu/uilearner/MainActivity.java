package group7.tcss450.uw.edu.uilearner;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import group7.tcss450.uw.edu.uilearner.SignIn_Registration.ChooseRoleFragment;
import group7.tcss450.uw.edu.uilearner.SignIn_Registration.ForgotPasswordFragment;
import group7.tcss450.uw.edu.uilearner.SignIn_Registration.RegisterFragment;
import group7.tcss450.uw.edu.uilearner.SignIn_Registration.SignInFragment;

public class MainActivity extends AppCompatActivity implements SignInFragment.OnFragmentInteractionListener,
           RegisterFragment.OnRegisterFragmentInteractionListener,
            ChooseRoleFragment.OnFragmentInteractionListener {

    public static final String TAG = "FIREBASE_TAG";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }



    /*
        A reusable method that simply replaces the current fragment attached to
        the main_container layout in activity_main with the new one given.
     */
    private void loadFragment(Fragment fragment, Bundle args) {
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null);
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
    public void createAccount (String email, String password) {
        Log.e(TAG, "In here");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("FIREBASE", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "createUserWithEmailAndPassword:failed", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i(TAG, "User creation completed and was successful");
                            Toast.makeText(MainActivity.this, R.string.auth_passed,
                                    Toast.LENGTH_SHORT).show();


                                //If the user has been created and signed in, the Display Fragment
                                //will be switched to.
                                sendEmailVerification();
                                user.setUid(mAuth.getCurrentUser().getUid());
                                Toast.makeText(getApplicationContext(), "Role has been set for this User!", Toast.LENGTH_LONG).show();
                                RegisterTask rTask = new RegisterTask();
                                rTask.execute(user);
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
}


    /*
        A helper method to switch to the AgendaActivity. If any arguments need to be passed
        to AgendaActivity, it will be passed here using agendaIntent.putExtra(KEY, VALUE).

        Author: Connor Lundberg
     */
    private void changeActivity () {
        Intent agendaIntent = new Intent(this, AgendaActivity.class);
        Log.d(AgendaActivity.TAG, "Breaks here");
        Bundle args = new Bundle();
        Log.d(AgendaActivity.TAG, "Breaks here2");
        args.putSerializable(TAG, user);
        Log.d(AgendaActivity.TAG, "Breaks here3");
        agendaIntent.putExtra(TAG, args);
        Log.d(AgendaActivity.TAG, "Breaks here4");
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
    public void signIn (String email, String password) {
        if (RegisterFragment.isValidEmail(email) && RegisterFragment.isValidPassword(password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("FIREBASE", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("FIREBASE", "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.auth_passed,
                                    Toast.LENGTH_SHORT).show();

                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                //Before switching activities, the user field needs to have a role set for it.
                                //This AsyncTask will get that role.
                                user.setUid(mAuth.getCurrentUser().getUid());
                                SignInTask sTask = new SignInTask();
                                sTask.execute(user);
                            } else {
                                Toast.makeText(MainActivity.this, R.string.verify_first,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        }
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

    public static boolean isUserRegistered(String username) {
        //TODO: create method to check if this user is already registered.
        //boolean reg = true;
        return false;
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


    /**
     * This class allows a User to be registered in the Learn3r backend as the role designated rather
     * than just in the Firebase system.
     *
     * @author Connor
     */
    public class RegisterTask extends AsyncTask<User, Void, String> {
        @Override
        protected String doInBackground(User... params) {
            User currUser = params[0];
            Uri uri;
            String response = "";
            try {
                // http://learner-backend.herokuapp.com/teacher?uuid=someUid&name=someName
                // or
                // http://learner-backend.herokuapp.com/student?uuid=someUid&name=someName
                if (currUser.getRole().equals(ChooseRoleFragment.IS_TEACHER)) {
                    uri = new Uri.Builder()
                            .scheme("http")
                            .authority("learner-backend.herokuapp.com")
                            .appendEncodedPath("teacher")
                            .appendQueryParameter("uuid", currUser.getUid()) //pass uid here
                            .appendQueryParameter("name", currUser.getEmail())
                            .build();
                } else {
                    uri = new Uri.Builder()
                            .scheme("http")
                            .authority("learner-backend.herokuapp.com")
                            .appendEncodedPath("student")
                            .appendQueryParameter("uuid", currUser.getUid()) //pass uid here
                            .appendQueryParameter("name", currUser.getEmail())
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

                return response;

            } catch (Exception e) {
                return e.getMessage();
            }
        }
    }


    /**
     * This class will get the role for the given User. The User is guaranteed to be in the
     * database because this Task is executed on the successful sign in of the User on Firebase.
     * Because of that we know the User is already registered, which is functional with our
     * back end, meaning their is a Teacher or Student in our tables with the corresponding uuid.
     *
     * @author Connor
     */
    public class SignInTask extends AsyncTask<User, Void, String> {
        @Override
        protected String doInBackground(User... params) {
            User currUser = params[0];
            try {
                String response;
                // http://learner-backend.herokuapp.com/role?uuid=someUid
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority("learner-backend.herokuapp.com")
                        .appendEncodedPath("role")
                        .appendQueryParameter("uuid", currUser.getUid()) //pass uid here
                        .build();

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

                return response;

            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            user.setRole(s);
            Log.d(TAG, "User role is " + user.getRole());
            Log.d(TAG, "Changing activities on sign in");
            changeActivity();
        }
    }
}
