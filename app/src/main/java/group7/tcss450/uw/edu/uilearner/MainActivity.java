package group7.tcss450.uw.edu.uilearner;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements TeacherFragment.OnFragmentInteractionListener {

    public static final String TAG = "FIREBASE_TAG";
    public static final String SIGN_IN = "SIGN_IN";
    public static final String REGISTER = "REGISTER";
    public static final String SIGN_OUT = "SIGN_OUT";

    private static final String AT_SYMBOL = "@";
    private static final String DOT_SYMBOL = ".";

    private static final int MIN_PASSWORD_LENGTH = 6;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

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
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_container, new OpenFragment())
                        .commit();
            }
        }
    }

    /**
     * Method for button press.
     * @param view
     */
    public void onButtonPress(View view) {

        switch(view.getId()) {
            case R.id.buttonStudent:
                loadFragment(new StudentFragment(), null);
                break;
            case R.id.buttonTeacher:
                loadFragment(new TeacherFragment(), null);
                break;
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
        Returns true if the String email given to it contains a
        '@' followed by a '.', otherwise false.

        Author: Connor Lundberg
     */
    private boolean isValidEmail(String email) {
        boolean isValid = false;

        if (email.contains(AT_SYMBOL)) {
            int delimiter = email.indexOf(AT_SYMBOL);
            String suffix = email.substring(delimiter);
            if (suffix.contains(DOT_SYMBOL)) {
                String prefix = email.substring(0, delimiter);
                if (!prefix.equals("")) {
                    isValid = true;
                }
            }
        }

        return isValid;
    }


    /*
        Returns true if the String password given to it is
        at least MIN_PASSWORD_LENGTH long, otherwise false.

        Author: Connor Lundberg
     */
    private boolean isValidPassword(String password) {
        boolean isValid = false;

        if (password.length() >= MIN_PASSWORD_LENGTH) {
            isValid = true;
        }

        return isValid;
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
        if (isValidEmail(email) && isValidPassword(password)) {
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
                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                    DisplayFragment displayFragment = new DisplayFragment();
                                    Bundle args = new Bundle();
                                    loadFragment(displayFragment, args);
                                } else {

                                }
                            }
                        }
                    });
        }
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
        if (isValidEmail(email) && isValidPassword(password)) {
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

                                DisplayFragment displayFragment = new DisplayFragment();
                                Bundle args = new Bundle();
                                loadFragment(displayFragment, args);
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


    /*
        Because this can be coming from a Teacher sign-in/register or a
        Student sign-in/register, this needs an accountState to differentiate
        the type of operation to do with the account.
     */
    @Override
    public void onFragmentInteraction(String accountState, String email, String password) {
        switch(accountState) {
            case SIGN_IN:
                signIn(email, password);
                break;
            case REGISTER:
                createAccount(email, password);
                break;
            case SIGN_OUT:
                signOut();
                break;
            default:
                Log.e(TAG, "Invalid accountState: " + accountState);
                break;
        }
    }
}
