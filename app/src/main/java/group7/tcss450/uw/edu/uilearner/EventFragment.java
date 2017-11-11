package group7.tcss450.uw.edu.uilearner;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
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


/**
 * This is where the Teacher creates a new event for a student. It has fields for event name, date,
 * time, summary, and the student it's for.
 *
 * @author Connor, Myles
 */
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


    /**
     * This gets the Elements to use in the AsyncTasks.
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
        View v = inflater.inflate(R.layout.fragment_event, container, false);
        mInstance = this;

        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
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
        setUpDateAndTimeListeners();


        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
    }

    /**
     * Gets the date and time listeners ready for the popups once the Teacher clicks
     * on one of the fields.
     *
     * @author Myles
     */
    private void setUpDateAndTimeListeners() {
        mEventDate.setOnTouchListener(new View.OnTouchListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(MotionEvent.ACTION_DOWN == event.getAction()) {
                    final DatePickerDialog dialog = new DatePickerDialog(getContext());

                    dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            mEventDate.setText(month+ 1 + "/" + dayOfMonth + "/" + year);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
                return false;
            }
        });

        mEventDate.setFocusable(false);
        mEventTime.setFocusable(false);
        mEventTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_DOWN == event.getAction()) {
                    TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mEventTime.setText(hourOfDay + ":" + minute);
                        }
                    };
                    int nowMinute = Calendar.getInstance().get(Calendar.MINUTE);
                    int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    TimePickerDialog dialog = new TimePickerDialog(getContext(), listener, nowHour, nowMinute, false);
                    dialog.show();
                }
                return false;
            }
        });





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
//            Toast.makeText(getActivity(), "Must choose a student!", Toast.LENGTH_SHORT);
            new AlertDialog.Builder(this.getContext())
                    .setMessage("Must choose a student!")
                    .setPositiveButton("OK", null)
                    .show();
            result = false;
        }

        return result;
    }


    /**
     * Gets the Teacher's uuid from the Activity and starts the Student AsyncTask in order to make
     * the list of students attached to the Teacher.
     *
     * @author Connor
     */
    @Override
    public void onStart() {
        if (getArguments() != null) {
            mTeacherUid = getArguments().getString("uuid");
            StudentTask sTask = new StudentTask();
            sTask.execute();
        }
        super.onStart();
    }


    /**
     * Once a student name is chosen, the current chosen student uuid is set and any
     * already selected radio button is deselected.
     *
     * @param uuid
     * @param chosenRadioButton
     *
     * @author Connor
     */
    @Override
    public void onStudentNameInteraction(String uuid, RadioButton chosenRadioButton) {
        mCurrentChosenStudentUid = uuid;

        // Uncheck current chosen radio button.
        if (mCurrentChosenRadioButton != null) {
            mCurrentChosenRadioButton.setChecked(false);
        }
        mCurrentChosenRadioButton = chosenRadioButton;
    }


    /**
     * This AsyncTask will get the list of Student emails and uuids and put them onto the
     * RecyclerView that will hold them.
     *
     * @author Connor, Myles
     */
    public class StudentTask extends AsyncTask<Void, Void, HashMap<String, String>> {
        @Override
        protected HashMap<String, String> doInBackground(Void... params) {
            String response = "";
            try {
                String uid = mTeacherUid;

                // http://learner-backend.herokuapp.com/teacher/events?uuid=someUid&start=someDate&end=someDate&summary=someSummary
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority("learner-backend.herokuapp.com")
                        .appendEncodedPath("teacher")
                        .appendEncodedPath("students")
                        .appendQueryParameter("uuid", uid) //pass uid here
                        .build();


                HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                Scanner s = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();
                while (s.hasNext()) sb.append(s.next());
                response = sb.toString();

                JSONObject json = new JSONObject(response);
                JSONArray students = (JSONArray) json.get("students");
                HashMap<String, String> allStudentsForTeacher = new HashMap<String, String>();

                for (int i = 0; i < students.length(); i++) {
                    JSONObject obj = students.getJSONObject(i);
                    Iterator<String> keyItr = obj.keys();
                    String key = keyItr.next();
                    allStudentsForTeacher.put(key, (String) obj.get(key));
                }

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
     * @author Connor, Myles
     */
    public class EventTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getContext());
            dialog.setIndeterminate(true);
            dialog.setMessage("Adding message");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            String response = "";
            boolean wasSuccessful;
            try {
                String uid = mCurrentChosenStudentUid;

                String[] dates = params[1].split("/");
                int month = Integer.valueOf(dates[0]);
                int dayOfMonth = Integer.valueOf(dates[1]);
                int year = Integer.valueOf(dates[2]);

                dates = DateUtil.getWholeDayStartEnd(year, month, dayOfMonth);
                Log.d(TAG, dates[0]);
                Log.d(TAG, dates[1]);

                String dStart = dates[0];
                String dEnd = dates[1];

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


                HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                connection.setRequestMethod("POST");
                connection.connect();
                Scanner s = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();
                while(s.hasNext()) sb.append(s.next());
                response = sb.toString();
                wasSuccessful = true;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                wasSuccessful = false;
            }

            return wasSuccessful;
        }


        @Override
        protected void onPostExecute(Boolean wasSuccessful) {
            if (wasSuccessful) {
                Toast.makeText(getActivity(), "Created a new event successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getActivity(), "Event creation failed!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }
    }
}
