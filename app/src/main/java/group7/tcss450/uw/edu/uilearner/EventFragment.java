package group7.tcss450.uw.edu.uilearner;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import group7.tcss450.uw.edu.uilearner.util.DateUtil;


public class EventFragment extends Fragment implements StudentAdapter.OnStudentNameInteractionListener {


    private static final String TAG = "EVENT";

    private String mCurrentChosenStudentUid;
    private String mTeacherUid;

    private RadioButton mCurrentChosenRadioButton;
    private EventFragment mInstance;

    private EditText mEventName;
    private EditText mEventDate;
    private EditText mEventTime;
    private EditText mEventSummary;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event, container, false);
        mInstance = this;

        mEventName = (EditText) v.findViewById(R.id.event_name);
        mEventDate = (EditText) v.findViewById(R.id.event_date);
        mEventTime = (EditText) v.findViewById(R.id.event_time);
        mEventSummary = (EditText) v.findViewById(R.id.event_summary);

        Button confirm = (Button) v.findViewById(R.id.confirm_event);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidForm()) {
                    String name = mEventName.getText().toString();
                    String date = mEventDate.getText().toString();
                    String time = mEventTime.getText().toString();
                    String summary = mEventSummary.getText().toString();

                    EventTask eTask = new EventTask();
                    eTask.execute(name, date, time, summary);
                }
            }
        });


        return v;
    }


    /**
     * Looks to see if any of the fields are empty, if so, then return false and set
     * the error for that field.
     *
     * @return True for valid form, false otherwise.
     *
     * @author Connor
     */
    private boolean isValidForm () {
        boolean result = true;

        if (mEventName.getText().toString().equals("")) {
            mEventName.setError("Event Name can't be empty!");
            result = false;
        }

        if (mEventDate.getText().toString().equals("")) {
            mEventDate.setError("Event Date can't be empty!");
            result = false;
        }

        if (mEventTime.getText().toString().equals("")) {
            mEventTime.setError("Event Time can't be empty!");
            result = false;
        }

        if (mEventSummary.getText().toString().equals("")) {
            mEventSummary.setError("Event Summary can't be empty!");
            result = false;
        }

        if (mCurrentChosenRadioButton == null) {
            Toast.makeText(getActivity(), "Must choose a student!", Toast.LENGTH_SHORT);
            result = false;
        }

        return result;
    }


    @Override
    public void onStart() {
        if (getArguments() != null) {
            mTeacherUid = getArguments().getString("uuid");
            StudentTask sTask = new StudentTask();
            sTask.execute();
        }
        super.onStart();
    }


    @Override
    public void onStudentNameInteraction(String uuid, RadioButton chosenRadioButton) {
        mCurrentChosenStudentUid = uuid;

        // Uncheck current chosen radio button.
        if (mCurrentChosenRadioButton != null) {
            mCurrentChosenRadioButton.setChecked(false);
        }
        mCurrentChosenRadioButton = chosenRadioButton;
        Log.d(TAG, "new student chosen: " + mCurrentChosenStudentUid);
    }


    public class StudentTask extends AsyncTask<Void, Void, HashMap<String, String>> {
        @Override
        protected HashMap<String, String> doInBackground(Void... params) {
            String response = "";
            try {
                //TODO Get uid and pass it to web request.

                String uid = mTeacherUid;

                // http://learner-backend.herokuapp.com/teacher/events?uuid=someUid&start=someDate&end=someDate&summary=someSummary
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority("learner-backend.herokuapp.com")
                        .appendEncodedPath("teacher")
                        .appendEncodedPath("students")
                        .appendQueryParameter("uuid", uid) //pass uid here
                        .build();


                Log.d(TAG, uri.toString());
                HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                Scanner s = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();
                while (s.hasNext()) sb.append(s.next());
                response = sb.toString();
                Log.d(TAG, response);
                JSONObject json = new JSONObject(response);
                JSONArray students = (JSONArray) json.get("students");
                HashMap<String, String> allStudentsForTeacher = new HashMap<String, String>();

                for (int i = 0; i < students.length(); i++) {
                    JSONObject obj = (JSONObject) students.get(i);
                    Iterator<String> keyItr = obj.keys();
                    String key = keyItr.next();
                    allStudentsForTeacher.put(key, (String) obj.get(key));
                }

                Log.d(TAG, "Final HashMap: " + allStudentsForTeacher.toString());
                return allStudentsForTeacher;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, String> allStudentsForTeacher) {
            RecyclerView rv = (RecyclerView) getActivity().findViewById(R.id.selected_student_list);
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(getActivity()));

            StudentAdapter sAdapter = new StudentAdapter(allStudentsForTeacher, mInstance);
            rv.setAdapter(sAdapter);
        }
    }


    /**
     * This class is used to make the new Event on clicking the Confirm button. It takes the given
     * date, summary, chosen student uuid, and event name and sends it to the back end to be
     * added into the GoogleCalendar.
     *
     * @author Connor
     */
    public class EventTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String response = "";
            try {
                //Date dStart = new Date(params[1]);
                //dStart.setTime(Long.valueOf(params[2]));  //add the time to the date

                /*Calendar rightNow = Calendar.getInstance();
                int year = rightNow.get(Calendar.YEAR);
                int month = rightNow.get(Calendar.MONTH);
                int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);

                GregorianCalendar endDay = new GregorianCalendar(year, month, dayOfMonth);
                endDay.add(GregorianCalendar.DAY_OF_MONTH, 1);
                Date dEnd = endDay.getTime();*/
                //TODO Get uid and pass it to web request.

                String uid = mCurrentChosenStudentUid;

                String[] dates = params[1].split("/");
                int year = Integer.valueOf(dates[0]);
                int month = Integer.valueOf(dates[1]);
                int dayOfMonth = Integer.valueOf(dates[2]);

                dates = DateUtil.getWholeDayStartEnd(year, month, dayOfMonth);

                String dStart = dates[0];
                String dEnd = dates[1];

                Log.d(TAG, "dStart: " + dStart);
                Log.d(TAG, "dEnd: " + dEnd);

                // http://learner-backend.herokuapp.com/teacher/events?uuid=someUid&start=someDate&end=someDate&summary=someSummary&event_name=someName
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority("learner-backend.herokuapp.com")
                        .appendEncodedPath("teacher")
                        .appendEncodedPath("events")
                        .appendQueryParameter("uuid", uid) //pass uid here
                        .appendQueryParameter("start", dStart)
                        .appendQueryParameter("end", dEnd)
                        .appendQueryParameter("summary", params[3])
                        .appendQueryParameter("event_name", params[0]) //pass event name here once the back end code is changed to match
                        .build();


                Log.d(TAG, uri.toString());
                HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                connection.setRequestMethod("POST");
                connection.connect();
                Scanner s = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();
                while(s.hasNext()) sb.append(s.next());
                response = sb.toString();
                Log.d(TAG, "Was the Event creation successful? " + response);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            return null;
        }

    }
}
