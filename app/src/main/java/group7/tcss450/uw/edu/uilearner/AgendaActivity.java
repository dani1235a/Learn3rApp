package group7.tcss450.uw.edu.uilearner;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import group7.tcss450.uw.edu.uilearner.auth.ChooseRoleFragment;

/**
 * This is the controller for the majority of the app. It gets everything ready for
 * the different fragments and swaps between the AgendaFragment, CalendarFragment, and
 * EventFragment (Teachers only).
 *
 * @author Connor
 */
public class AgendaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AgendaFragment.OnListFragmentInteractionListener{

    public static final String TAG = "AGENDA";

    private boolean mIsTeacher; // differentiates the user type.
    private String mEmail;
    private String mUid;
    private String mRole;
    private String mAddCode;



    /**
     * This is where the Activity kicks off. The first thing is does after getting the layout
     * ready is check to make sure the user is signed in and that they are verified. After,
     * it will get an Intent and check if it exists or not. This is to get information from the
     * MainActivity about user details. Things like role type, add code (for Teachers), and email.
     * From there we get the Floating Action Button set up, do some more work on the layout, and start
     * the first Fragment (AgendaFragment).
     *
     * @param savedInstanceState
     *
     * @author Connor
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_person_black_24dp);
        toolbar.setTitle(mUid);
        setSupportActionBar(toolbar);

        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fireUser != null && !fireUser.isEmailVerified()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("You will have to verify your email before you login again");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Don't really need to do anything here...
                }
            });
            builder.show();
        }


        Intent i = getIntent();
        if (i != null) {
            Bundle args = i.getExtras();
            if (args != null) {
                Log.d(TAG, "here");
                Bundle temp = (Bundle) args.get(MainActivity.TAG);
                User currUser = (User) temp.get(MainActivity.TAG);
                mEmail = currUser.getEmail();
                mUid = currUser.getUid();
                mIsTeacher = currUser.getRole().equals(ChooseRoleFragment.IS_TEACHER);
                mRole = currUser.getRole();
                if(mRole.equals(ChooseRoleFragment.IS_TEACHER)) {
                    mAddCode = currUser.getAddCode();
                }
                Log.d(TAG, mRole);
            } else {
                Log.d(TAG, "Bundle was null");
            }
        } else {
            Log.d(TAG, "Intent was null");
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putSerializable("uuid", mUid);
                loadFragment(new EventFragment(), args);
            }
        });

        if (!mIsTeacher) {
            Log.d(TAG, "in here");
            fab.setVisibility(View.INVISIBLE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        if (savedInstanceState == null) {
            if (findViewById(R.id.abc) != null) {
                Bundle args = new Bundle();
                AgendaFragment af = new AgendaFragment();
                args.putSerializable("uuid", mUid); //will need to set this "uuid" string to a constant value.
                args.putSerializable("role", mRole);
                af.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.abc, af)
                        .commit();
            }
        }
    }


    /**
     * Closes the drawer on back press if it is open.
     *
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /**
     * Inflates the options menu.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        TextView myDrawerEmail = (TextView) findViewById(R.id.email_display);
        myDrawerEmail.setText(mEmail);
        TextView myAddCode = (TextView) findViewById(R.id.addCode_display);
        myDrawerEmail.setTextColor(Color.BLACK);
        if(mRole.equals(ChooseRoleFragment.IS_TEACHER)) {
           myAddCode.setText("Add Code: " + mAddCode);
            myAddCode.setTextColor(Color.BLACK);
        }
        getMenuInflater().inflate(R.menu.agenda, menu);
        return true;
    }


    /**
     * This is for any of the options menu items when they are clicked.
     *
     * @param item
     * @return That a menu item was clicked.
     *
     * @author Connor
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            //there will need to be a check using a popup here
            Intent toMain = new Intent(this, MainActivity.class);
            toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toMain);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * This is for any of the navigation menu items when they are clicked.
     *
     * @param item
     * @return That a menu item was clicked
     *
     * @author Connor
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calendar) {
            Bundle args = new Bundle();
            CalendarFragment cf = new CalendarFragment();
            args.putSerializable("uuid", mUid); //will need to set this "uuid" string to a constant value.
            args.putSerializable("role", mRole);
            loadFragment(cf, args);
        } else if (id == R.id.nav_agenda) {
            Bundle args = new Bundle();
            AgendaFragment af = new AgendaFragment();
            args.putSerializable("uuid", mUid); //will need to set this "uuid" string to a constant value.
            args.putSerializable("role", mRole);
            loadFragment(af, args);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
         A reusable method that simply replaces the current fragment attached to
         the main_container layout in activity_main with the new one given.
      */
    private void loadFragment(Fragment fragment, Bundle args) {
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.abc, fragment)
                .addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(String item) {
    }
}
