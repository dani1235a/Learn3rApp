package group7.tcss450.uw.edu.uilearner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
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
    public static final String SPACE = "\u26B3";

    private String mCurrentChosenStudentUid;
    private String mTeacherUid;

    private RadioButton mCurrentChosenRadioButton;
    private EventFragment mInstance;

    private EditText mEventName;
    private EditText mEventDate;
    private EditText mEventTimeStart;
    private EditText mEventTimeEnd;
    private EditText mEventSummary;
    private TextView mAddTask;
    private CheckedTextView mTask1;
    private CheckedTextView mTask2;
    private CheckedTextView mTask3;
    private int numTasks = 0;

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
        mEventTimeStart = (EditText) v.findViewById(R.id.event_timeStart);
        mEventTimeEnd = (EditText) v.findViewById(R.id.event_timeEnd);
        mEventSummary = (EditText) v.findViewById(R.id.event_summary);
        mAddTask = (TextView) v.findViewById(R.id.textViewAddEvent);
        mTask1 = (CheckedTextView) v.findViewById(R.id.checkedTextViewTask1);
        mTask2 = (CheckedTextView) v.findViewById(R.id.checkedTextViewTask2);
        mTask3 = (CheckedTextView) v.findViewById(R.id.checkedTextViewTask3);

        mAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numTasks == 2) {
                    mTask3.setVisibility(View.VISIBLE);
                    numTasks++;
//                    mAddTask.setTextColor(Color.GRAY);
                    mAddTask.setVisibility(View.GONE);
                }if(numTasks == 1) {
                    mTask2.setVisibility(View.VISIBLE);
                    numTasks++;
                }
                if(numTasks == 0) {
                    mTask1.setVisibility(View.VISIBLE);
                    numTasks++;
                }
            }
        });

        mTask1.setOnClickListener(new OnTaskClickListener(1));
        mTask2.setOnClickListener(new OnTaskClickListener(2));
        mTask3.setOnClickListener(new OnTaskClickListener(3));



        Button confirm = (Button) v.findViewById(R.id.confirm_event);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidForm()) {
                    String name = mEventName.getText().toString();
                    String date = mEventDate.getText().toString();
                    String timeStart = mEventTimeStart.getText().toString();
                    String timeEnd = mEventTimeEnd.getText().toString();
                    String summary = mEventSummary.getText().toString();

                    EventTask eTask = new EventTask();
                    eTask.execute(name, date, timeStart, timeEnd, summary);
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
        Log.d(TAG, "mEventDate == null? " + (mEventDate == null));
        mEventDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(MotionEvent.ACTION_DOWN == event.getAction()) {
                    Context ctx = getContext();
                    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            mEventDate.setText(month+ 1 + "/" + dayOfMonth + "/" + year);
                        }
                    };
                    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    int month = Calendar.getInstance().get(Calendar.MONTH);
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    DatePickerDialog dialog = new DatePickerDialog(ctx, 0, listener, year, month, day);
                    dialog.show();

                }
                return false;
            }
        });

        mEventDate.setFocusable(false);
        mEventTimeStart.setFocusable(false);
        mEventTimeStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_DOWN == event.getAction()) {
                    TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mEventTimeStart.setText(hourOfDay + ":" + minute);
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


        mEventTimeEnd.setFocusable(false);
        mEventTimeEnd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_DOWN == event.getAction()) {
                    TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mEventTimeEnd.setText(hourOfDay + ":" + minute);
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
            mEventDate.setError("Date can't be empty!");
            result = false;
        }

        if (mEventTimeStart.getText().toString().equals("")) {
            mEventTimeStart.setError("Start Time can't be empty!");
            result = false;
        }

        if (mEventTimeStart.getText().toString().equals("")) {
            mEventTimeEnd.setError("End Time can't be empty!");
            result = false;
        }

        if (mEventSummary.getText().toString().equals("")) {
            mEventSummary.setError("Summary can't be empty!");
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

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getContext());
            dialog.setIndeterminate(true);
            dialog.setMessage("Getting your student list...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

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

                Log.d(TAG, uri.toString());
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
            dialog.dismiss();
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
            dialog.setMessage("Adding event...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String response = "";
            boolean wasSuccessful;
            try { //params[0] = name, params[1] = date, params[2] = timeStart, params[3] = timeEnd, params[4] = summary
                String uid = mCurrentChosenStudentUid;

                Log.d(TAG, "Start time: " + params[2]);
                Log.d(TAG, "End time: " + params[3]);

                String[] dates = params[1].split("/");
                int month = Integer.valueOf(dates[0]);
                int dayOfMonth = Integer.valueOf(dates[1]);
                int year = Integer.valueOf(dates[2]);

                String[] timeStart = params[2].split(":");
                int startHour = Integer.valueOf(timeStart[0]);
                int startMinute = Integer.valueOf(timeStart[1]);

                String[] timeEnd = params[3].split(":");
                int endHour = Integer.valueOf(timeEnd[0]);
                int endMinute = Integer.valueOf(timeEnd[1]);

                dates = DateUtil.getStartAndEndDate(year, month, dayOfMonth, startHour, startMinute, endHour, endMinute);
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
                        .appendQueryParameter("description", params[4].replaceAll(" ", SPACE))
                        .appendQueryParameter("event_name", params[0].replaceAll(" ", SPACE)) //pass event name here once the back end code is changed to match
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

    private class OnTaskClickListener implements View.OnClickListener {
        CheckedTextView mTaskView;

        public OnTaskClickListener(int taskNum) {
            if(taskNum == 1) {
                mTaskView = mTask1;
            } else if (taskNum == 2){
                mTaskView = mTask2;
            } else {
                mTaskView = mTask3;
            }
        }

        @Override
        public void onClick(View v) {
            final EditText alterText = new EditText(v.getContext());

            alterText.setHint("Add a task for your student!");

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Task")
                    .setMessage("Add a task to give to your student")
                    .setView(alterText)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mTaskView.setText(alterText.getText().toString());
                            mTaskView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_checked, 0, 0, 0);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }
    }
}
