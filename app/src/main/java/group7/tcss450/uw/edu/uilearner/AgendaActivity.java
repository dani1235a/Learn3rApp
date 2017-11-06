package group7.tcss450.uw.edu.uilearner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import group7.tcss450.uw.edu.uilearner.dummy.DummyContent;

public class AgendaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CalendarFragment.OnCalendarInteractionListener,
        AgendaFragment.OnListFragmentInteractionListener {

    public static final String TAG = "CALENDAR";

    private RecyclerView mRecyclerView;
    private boolean mIsCalendarView; // differentiates which adapter will be set to the recycler view.
    private boolean mIsTeacher; // differentiates the user type.
    private String mEmail;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "made it in here");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "made it in here1");
        setContentView(R.layout.activity_agenda);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                mEmail = ((User) temp.get(MainActivity.TAG)).getEmail();
                mUid = ((User) temp.get(MainActivity.TAG)).getUid();
                Log.d(TAG, "email is: " + mEmail);
                Log.d(TAG, "uid is: " + mUid);
            } else {
                Log.d(TAG, "Bundle was null");
            }
        } else {
            Log.d(TAG, "Intent was null");
        }
        Log.d(TAG, "made it in here2");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Log.d(TAG, "made it in here3");
        if (savedInstanceState == null) {
            if (findViewById(R.id.agendaContainer) != null) {
                /*mIsCalendarView = false;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.agendaContainer, new AgendaFragment())
                        .commit();
                mRecyclerView = (RecyclerView) findViewById(R.id.agenda_list);

                AgendaTask agendaTask = new AgendaTask();

                // Gets today's date so the Agenda page Recycler View can populate with
                // events for that day from Google Calendar.
                Calendar rightNow = Calendar.getInstance();
                int year = rightNow.get(Calendar.YEAR) - 1900;
                int month = rightNow.get(Calendar.MONTH);
                int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);
                Log.d(TAG, "today's date from Agenda: " + dayOfMonth + "/" + month + "/" + year);

                agendaTask.execute(year, month, dayOfMonth);*/
            }
        }
        Log.d(TAG, "made it in here4");
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
        Log.d(TAG, "an item was selected");

        if (id == R.id.nav_calendar) {
            Log.d(TAG, "opening Calendar");
            mIsCalendarView = true;
            loadFragment(new CalendarFragment(), null);
            mRecyclerView = (RecyclerView) findViewById(R.id.event_display);
        } else if (id == R.id.nav_agenda) {
            Log.d(TAG, "opening Agenda");
            mIsCalendarView = false;
            loadFragment(new AgendaFragment(), null);
            mRecyclerView = (RecyclerView) findViewById(R.id.agenda_list);
            AgendaTask agendaTask = new AgendaTask();

            // Gets today's date so the Agenda page Recycler View can populate with
            // events for that day from Google Calendar.
            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(Calendar.YEAR) - 1900;
            int month = rightNow.get(Calendar.MONTH);
            int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);
            Log.d(TAG, "today's date from Agenda: " + dayOfMonth + "/" + month + "/" + year);

            agendaTask.execute(year, month, dayOfMonth);

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
                .replace(R.id.agendaContainer, fragment)
                .addToBackStack(null);
        transaction.commit();
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void onCalendarInteraction(int year, int month, int dayOfMonth) {

        final TextView tv = (TextView) findViewById(R.id.date_display);
        tv.setText(java.lang.String.format("%02d", dayOfMonth) + "/" + java.lang.String.format("%02d", month) + "/" + year);

        findViewById(R.id.date_info).setVisibility(View.VISIBLE);

        Log.d(TAG, "today's date from Calendar: " + dayOfMonth + "/" + month + "/" + year);
        AgendaTask aTask = new AgendaTask();
        aTask.execute(year, month, dayOfMonth);
    }


    @Override
    public void onListFragmentInteraction(String item) {

    }



    public Activity returnActivity () {
        return this;
    }




    public class AgendaTask extends AsyncTask<Integer, Integer, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Integer... integers) {
            String response = "";
            try {
                Date dStart = new GregorianCalendar(integers[0], integers[1], integers[2]).getTime();
                GregorianCalendar endDay = new GregorianCalendar(integers[0], integers[1], integers[2]);
                endDay.add(GregorianCalendar.DAY_OF_MONTH, 1);
                Date dEnd = endDay.getTime();
                //TODO Get uid and pass it to web request.

                String uid = mUid;
                // http://learner-backend.herokuapp.com/student/events?start=someTime&end=someTime&uuid=UUID
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority("learner-backend.herokuapp.com")
                        .appendEncodedPath("teacher")
                        .appendEncodedPath("events")
                        .appendQueryParameter("uuid", uid) //pass uid here
                        .appendQueryParameter("start", dStart.toString())
                        .appendQueryParameter("end", dEnd.toString())
                        .build();


                Log.d(TAG, uri.toString());
                HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                Scanner s = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();
                while(s.hasNext()) sb.append(s.next());
                response = sb.toString();
                Log.d(TAG, "here");
                Log.d(TAG, response);
                JSONObject json = new JSONObject(response);
                Log.d(TAG, "here2");
                JSONArray events = (JSONArray) json.get("events");

                Log.d(TAG, events.toString());

                ArrayList<String> dataset = new ArrayList<String>();
                for (int i = 0; i < events.length(); i++) {
                    dataset.add(events.getString(i));
                }

                return dataset;

            } catch (Exception e) {
                ArrayList<String> msg = new ArrayList<String>();
                msg.add(e.getMessage());
                return msg;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            mRecyclerView.setHasFixedSize(true); //change this to false if size doesn't look correct

            LinearLayoutManager layoutManager = new LinearLayoutManager(returnActivity());
            mRecyclerView.setLayoutManager(layoutManager);
            RecyclerView.Adapter adapter;
            if (mIsCalendarView) {
                adapter = new CalendarAdapter(result); // will probably need to update CalendarAdapter constructor to take the InteractionListener like below.
            } else {
                adapter = new AgendaAdapter(result,
                        (AgendaFragment.OnListFragmentInteractionListener) returnActivity());
            }
            mRecyclerView.setAdapter(adapter);
        }


    }
}
