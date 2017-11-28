package group7.tcss450.uw.edu.uilearner;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import group7.tcss450.uw.edu.uilearner.util.DateUtil;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarFragment.OnCalendarInteractionListener} interface
 * to handle interaction events. It will display the date and any events
 * listed for the selected below Calendar.
 *
 * @author Connor
 */
public class CalendarFragment extends Fragment {

    private static final String TAG = "CALENDAR";


    private OnCalendarInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mUid;

    public CalendarFragment() {
        // Required empty public constructor
    }


    /**
     * Gets the Calendar set up in the layout and specifies what to happen on the date changed.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     *
     * @author Connor
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        CalendarView calendarView = (CalendarView) v.findViewById(R.id.calendarView);
        calendarView.setShowWeekNumber(false);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                final TextView tv = (TextView) getActivity().findViewById(R.id.date_display);
                tv.setText(java.lang.String.format("%02d", dayOfMonth) + "/" + java.lang.String.format("%02d", month) + "/" + year);
                getActivity().findViewById(R.id.date_info).setVisibility(View.VISIBLE);
                AgendaTask aTask = new AgendaTask();
                aTask.execute(year, month, dayOfMonth);
            }
        });

        mRecyclerView = (RecyclerView) v.findViewById(R.id.event_display);

        return v;
    }


    /**
     * This is used to get the uuid of the user and tell the Calendar to start with the current day
     * (which is also the first day chosen on the Calendar automatically).
     *
     * @author Connor
     */
    @Override
    public void onStart() {
        Bundle args = getArguments();
        if (args != null) {
            mUid = (String) args.get("uuid");

            AgendaTask agendaTask = new AgendaTask();

            // Gets today's date so the Calendar page Recycler View can populate with
            // events for that day from Google Calendar.
            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(Calendar.YEAR);
            int month = rightNow.get(Calendar.MONTH);
            month++;
            int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);
            Log.d(TAG, "today's date from Calendar: " + dayOfMonth + "/" + month + "/" + year);

            final TextView tv = (TextView) getActivity().findViewById(R.id.date_display);
            tv.setText(java.lang.String.format("%02d", dayOfMonth) + "/" + java.lang.String.format("%02d", month) + "/" + year);
            getActivity().findViewById(R.id.date_info).setVisibility(View.VISIBLE);

            agendaTask.execute(year, month, dayOfMonth);
        } else {
            Log.e(TAG, "Arguments were null");
        }
        super.onStart();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCalendarInteractionListener {
        void onCalendarInteraction(int year, int month, int dayOfMonth);
    }


    /**
     * This inner class will get the user's events for the current day and display them
     * on the CalendarFragment's RecyclerView.
     *
     * @author Connor
     */
    public class AgendaTask extends AsyncTask<Integer, Integer, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Integer... integers) {
            String response = "";
            try {

                String[] dates = DateUtil.getWholeDayStartEnd(integers[0], integers[1], integers[2]);

                String uid = mUid;
                // http://learner-backend.herokuapp.com/student/events?start=someTime&end=someTime&uuid=UUID
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority("learner-backend.herokuapp.com")
                        .appendEncodedPath("teacher") //this will need to have a check for user role.
                        .appendEncodedPath("events")
                        .appendQueryParameter("uuid", uid) //pass uid here
                        .appendQueryParameter("start", dates[0])
                        .appendQueryParameter("end", dates[1])
                        .build();


                Log.d(TAG, uri.toString());
                HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                Scanner s = new Scanner(connection.getInputStream());  //get the result from the query
                StringBuilder sb = new StringBuilder();
                while(s.hasNext()) sb.append(s.next());
                response = sb.toString();
                Log.d(TAG, response);
                JSONObject json = new JSONObject(response);  //turn result into parseable json object
                JSONArray events = (JSONArray) json.get("events");

                Log.d(TAG, events.toString());

                ArrayList<String> dataset = new ArrayList<String>();  //add json events array into the dataset
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
            TextView dateEmpty = (TextView) getActivity().findViewById(R.id.date_empty);
            if (!result.isEmpty()) {
                if (dateEmpty.getVisibility() == View.VISIBLE) {
                    dateEmpty.setVisibility(View.GONE);
                }
                mRecyclerView.setHasFixedSize(true); //change this to false if size doesn't look correct

                /*
                    This section will look through the result list given and only
                    add students with events lists that are not empty.
                 */
                ArrayList<String> finalResult = new ArrayList<String>();
                for (String str : result) {
                    try {
                        JSONObject events = new JSONObject(str);
                        JSONArray arr = events.getJSONArray("events");
                        if (arr.length() > 0) {
                            finalResult.add(str);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //this will need to have a check if the result is empty. If so, then display an empty message
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(layoutManager);
                RecyclerView.Adapter adapter;
                adapter = new CalendarAdapter(finalResult);
                mRecyclerView.setAdapter(adapter);
            } else {
                dateEmpty.setVisibility(View.VISIBLE);
            }
        }
    }
}
