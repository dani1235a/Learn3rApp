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


public class EventFragment extends Fragment implements StudentAdapter.OnStudentNameInteractionListener {


    private static final String TAG = "EVENT";

    private String mCurrentChosenStudentUid;
    private String mTeacherUid;
    private RadioButton mCurrentChosenRadioButton;
    private EventFragment mInstance;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event, container, false);
        mInstance = this;

        Button confirm = (Button) v.findViewById(R.id.confirm_event);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar rightNow = Calendar.getInstance();
                int year = rightNow.get(Calendar.YEAR);
                int month = rightNow.get(Calendar.MONTH);
                int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);

                EventTask eTask = new EventTask();
                eTask.execute(year, month, dayOfMonth);
            }
        });


        return v;
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

                Log.d(TAG, students.toString());
                Log.d(TAG, "Final HashMap: " + allStudentsForTeacher.toString());
                return allStudentsForTeacher;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, String> allStudentsForTeacher) {
            Log.d(TAG, "Starting onPostExecute");
            RecyclerView rv = (RecyclerView) getActivity().findViewById(R.id.selected_student_list);
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(getActivity()));

            StudentAdapter sAdapter = new StudentAdapter(allStudentsForTeacher, mInstance);
            rv.setAdapter(sAdapter);
            Log.d(TAG, "Setting adapter");
        }
    }

    /**
     * This class is used to make the new Event on clicking the Confirm button. It takes the given
     * date, summary, chosen student uuid, and event name and sends it to the back end to be
     * added into the GoogleCalendar.
     *
     * @author Connor
     */
    public class EventTask extends AsyncTask<Integer, Void, Void> {
        private String summary;

        @Override
        protected void onPreExecute() {
            EditText summ = (EditText) getActivity().findViewById(R.id.event_summary);
            summary = summ.getText().toString();
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            String response = "";
            try {
                Date dStart = new GregorianCalendar(integers[0], integers[1], integers[2]).getTime();
                GregorianCalendar endDay = new GregorianCalendar(integers[0], integers[1], integers[2]);
                endDay.add(GregorianCalendar.DAY_OF_MONTH, 1);
                Date dEnd = endDay.getTime();
                //TODO Get uid and pass it to web request.

                String uid = mCurrentChosenStudentUid;

                // http://learner-backend.herokuapp.com/teacher/events?uuid=someUid&start=someDate&end=someDate&summary=someSummary
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority("learner-backend.herokuapp.com")
                        .appendEncodedPath("teacher")
                        .appendEncodedPath("events")
                        .appendQueryParameter("uuid", uid) //pass uid here
                        .appendQueryParameter("start", dStart.toString())
                        .appendQueryParameter("end", dEnd.toString())
                        .appendQueryParameter("summary", summary)
                        //.appendQueryParameter("event_name", eventName) //pass event name here once the back end code is changed to match
                        .build();


                Log.d(TAG, uri.toString());
                HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                connection.setRequestMethod("POST");
                connection.connect();
                Scanner s = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();
                while(s.hasNext()) sb.append(s.next());
                response = sb.toString();
                Log.d(TAG, response);
                JSONObject json = new JSONObject(response);
                JSONArray events = (JSONArray) json.get("events");

                Log.d(TAG, events.toString());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            return null;
        }

    }
}
