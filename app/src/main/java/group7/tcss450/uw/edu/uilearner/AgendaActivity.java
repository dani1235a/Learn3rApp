package group7.tcss450.uw.edu.uilearner;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.webkit.URLUtil;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

public class AgendaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CalendarFragment.OnCalendarInteractionListener{

    public static final String TAG = "CALENDAR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*if (savedInstanceState == null) {
            if (findViewById(R.id.agendaContainer) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.agendaContainer, new CalendarFragment())
                        .commit();
            }
        }*/
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
            loadFragment(new CalendarFragment(), null);
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
        tv.setText(String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month) + "/" + year);

        findViewById(R.id.date_info).setVisibility(View.VISIBLE);

        new AsyncTask<Integer, Integer, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Integer... integers) {
                try {
                    Date dStart = new GregorianCalendar(integers[0], integers[1], integers[2]).getTime();
                    GregorianCalendar endDay = new GregorianCalendar(integers[0], integers[1], integers[2]);
                    endDay.add(GregorianCalendar.DAY_OF_MONTH, 1);
                    Date dEnd = endDay.getTime();
                    //TODO Get uuid and pass it to web request.
                    // http://learner-backend.herokuapp.com/student/events?start=someTime&end=someTime&uuid=UUID
                    Uri uri = new Uri.Builder()
                            .scheme("http")
                            .authority("learner-backend.herokuapp.com")
                            .appendEncodedPath("student")
                            .appendEncodedPath("events")
                            .appendQueryParameter("uuid", "test")
                            .appendQueryParameter("start", dStart.toString())
                            .appendQueryParameter("end", dEnd.toString())
                            .build();



                    HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    Scanner s = new Scanner(connection.getInputStream());
                    StringBuilder sb = new StringBuilder();
                    while(s.hasNext()) sb.append(s.next());
                    String response = sb.toString();
                    return new JSONObject(response);

                } catch (Exception e) {
                    Log.e("ASYNC_TASK", "Failed...", e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                try {
                    JSONArray arr = result.getJSONArray("events");
                    String str = "";
                    for(int i = 0; i < arr.length(); i++) {
                        str += arr.getJSONObject(i).getString("summary") + "\n";
                        Log.d("event", arr.getJSONObject(i).toString());
                    }
                    tv.setText(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }.execute(year, month, dayOfMonth);


    }



}
