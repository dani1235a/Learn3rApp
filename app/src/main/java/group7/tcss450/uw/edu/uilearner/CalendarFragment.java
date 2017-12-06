package group7.tcss450.uw.edu.uilearner;

import android.app.ProgressDialog;
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

import group7.tcss450.uw.edu.uilearner.auth.ChooseRoleFragment;
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
public class CalendarFragment extends Fragment implements AgendaAdapter.OnEditButtonInteractionListener {

    private static final String TAG = "CALENDAR";


    private AgendaFragment.OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mUid;
    private String mRole;

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
                tv.setText(java.lang.String.format("%02d", month) + "/" +  java.lang.String.format("%02d", dayOfMonth) + "/" + year);
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
            mRole = (String) args.get("role");

            AgendaTask agendaTask = new AgendaTask();

            // Gets today's date so the Calendar page Recycler View can populate with
            // events for that day from Google Calendar.
            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(Calendar.YEAR);
            int month = rightNow.get(Calendar.MONTH);
            int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);
            Log.d(TAG, "today's date from Calendar: " + dayOfMonth + "/" + month + "/" + year);

            final TextView tv = (TextView) getActivity().findViewById(R.id.date_display);
            tv.setText(java.lang.String.format("%02d", month) + "/" +  java.lang.String.format("%02d", dayOfMonth) + "/" + year);
            getActivity().findViewById(R.id.date_info).setVisibility(View.VISIBLE);

            agendaTask.execute(year, month, dayOfMonth);
        } else {
            Log.e(TAG, "Arguments were null");
        }
        super.onStart();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (AgendaFragment.OnListFragmentInteractionListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onEditButtonInteraction(String studentId, String title, String date, String gCalid, String eventId, String startTime, String endTime, String summary, String[] tasks) {
        mListener.onListFragmentInteraction(studentId, title, date, gCalid, eventId, startTime, endTime, summary, tasks);
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

        ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getContext());
            dialog.setIndeterminate(true);
            dialog.setMessage("Getting the day's events...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(Integer... integers) {
            String response = "";
            try {

                String[] dates = DateUtil.getWholeDayStartEnd(integers[0], integers[1], integers[2]);
                Log.d("CALENDAR", "Sending in CalendarFragment start date: " + dates[0]);
                Log.d("CALENDAR", "Sending in CalendarFragment end date: " + dates[1]);

                String uid = mUid;
                // http://learner-backend.herokuapp.com/student/events?start=someTime&end=someTime&uuid=UUID

                Uri uri;
                if (mRole.equals(ChooseRoleFragment.IS_TEACHER)) {
                    uri = new Uri.Builder()
                            .scheme("http")
                            .authority("learner-backend.herokuapp.com")
                            .appendEncodedPath("teacher") //this will need to have a check for user role.
                            .appendEncodedPath("events")
                            .appendQueryParameter("uuid", uid) //pass uid here
                            .appendQueryParameter("start", dates[0])
                            .appendQueryParameter("end", dates[1])
                            .build();
                } else {
                    uri = new Uri.Builder()
                            .scheme("http")
                            .authority("learner-backend.herokuapp.com")
                            .appendEncodedPath("student")
                            .appendEncodedPath("events")
                            .appendQueryParameter("uuid", uid)
                            .appendQueryParameter("start", dates[0])
                            .appendQueryParameter("end", dates[1])
                            .build();
                }


                Log.d(TAG, uri.toString());
                HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                Scanner s = new Scanner(connection.getInputStream());  //get the result from the query
                StringBuilder sb = new StringBuilder();
                while(s.hasNext()) sb.append(s.next());
                response = sb.toString();
                Log.d(TAG, response);
                JSONArray events;
                try {
                    events = new JSONObject(response)
                            .getJSONArray("events");
                } catch (Exception e) {
                    events = new JSONArray(response);
                }

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
                String msg = "Result wasn't empty";
                if (dateEmpty.getVisibility() == View.VISIBLE) {
                    dateEmpty.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    msg += ", setting the empty text to gone and setting the recycler view to visible";
                }
                Log.d(TAG, msg);
                mRecyclerView.setHasFixedSize(true); //change this to false if size doesn't look correct

                //this will need to have a check if the result is empty. If so, then display an empty message
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(layoutManager);
                RecyclerView.Adapter adapter;
                adapter = new AgendaAdapter(result, getFragment());
                mRecyclerView.setAdapter(adapter);
                dialog.dismiss();
            } else {
                Log.d(TAG, "Result was empty, setting the empty text to visible and setting recycler view to gone");
                dateEmpty.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                dialog.dismiss();
            }
        }
    }


    public CalendarFragment getFragment() {
        return this;
    }
}
