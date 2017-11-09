package group7.tcss450.uw.edu.uilearner;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import group7.tcss450.uw.edu.uilearner.SignIn_Registration.ChooseRoleFragment;

public class AgendaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "CALENDAR";

    private boolean mIsTeacher; // differentiates the user type.
    private String mEmail;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fireUser != null && !fireUser.isEmailVerified()) {
            Log.d(TAG, "Email not verified");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("You will have to verify your email before you login again");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Don't really need to do anything here...
                }
            });
            builder.show();
        } else if(fireUser != null) Log.d(TAG, "Email " + fireUser.getEmail() + "is verified");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new EventFragment(), null);
            }
        });


        Intent i = getIntent();
        if (i != null) {
            Bundle args = i.getExtras();
            if (args != null) {
                Bundle temp = (Bundle) args.get(MainActivity.TAG);
                User currUser = (User) temp.get(MainActivity.TAG);
                mEmail = currUser.getEmail();
                mUid = currUser.getUid();
                mIsTeacher = currUser.getRole().equals(ChooseRoleFragment.IS_TEACHER);
            } else {
                Log.d(TAG, "Bundle was null");
            }
        } else {
            Log.d(TAG, "Intent was null");
        }

        /*if (!mIsTeacher) {
            Log.d(TAG, "in here");
            fab.setVisibility(View.INVISIBLE);
        }*/

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
                af.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.abc, af)
                        .commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.agenda, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calendar) {
            Bundle args = new Bundle();
            CalendarFragment cf = new CalendarFragment();
            args.putSerializable("uuid", mUid); //will need to set this "uuid" string to a constant value.
            loadFragment(cf, args);
        } else if (id == R.id.nav_agenda) {
            Bundle args = new Bundle();
            AgendaFragment af = new AgendaFragment();
            args.putSerializable("uuid", mUid); //will need to set this "uuid" string to a constant value.
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
}
