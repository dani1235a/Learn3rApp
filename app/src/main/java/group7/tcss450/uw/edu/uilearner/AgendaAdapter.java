package group7.tcss450.uw.edu.uilearner;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group7.tcss450.uw.edu.uilearner.AgendaFragment.OnListFragmentInteractionListener;
import group7.tcss450.uw.edu.uilearner.util.DateUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static group7.tcss450.uw.edu.uilearner.EventFragment.SPACE;

/**
 * {@link RecyclerView.Adapter} that can display a {@link String} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}. This one specifically makes the cards
 * that will be displayed on the AgendaFragment.
 *
 * @author Connor
 */
public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.ViewHolder> {

    public static final String TAG = "AGENDA";
    public static final String EVENT_TITLE = "summary";
    public static final String STUDENT_NAME = "studentName";
    public static final String TASKS = "tasks";
    public static final String DESCRIPTION = "description";

    private final ArrayList<String> mValues;


    /**
     * Main constructor for AgendaAdapter.
     *
     * @param items The list of String values to put on the text views.
     * @param listener The listener to interact with.
     *
     * @author Connor
     */
    public AgendaAdapter(ArrayList<String> items, OnListFragmentInteractionListener listener) {
        mValues = items;
    }


    /**
     * This is called for the number of elements in the String List. It makes each ViewHolder
     * and inflates them.
     *
     * @param parent
     * @param viewType
     * @return The ViewHolder to display
     *
     * @author Connor
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        Log.d(TAG, parent.toString());

        Log.d(TAG, "Creating View Holder");
        return new ViewHolder(view);
    }


    /**
     * This binds the values to their respective holder.
     *
     * @param holder
     * @param position
     *
     * @author Connor
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "Binding View Holder");
        holder.mItem = mValues.get(position);
        try {
            Log.d(TAG, "getting " + mValues.get(position));
            JSONObject events = new JSONObject(mValues.get(position));
            Log.d(TAG, events.toString(2));
            holder.mIdView.setText(events.optString(STUDENT_NAME));

            String start = events.getJSONObject("start").getString("dateTime");
            String end = events.getJSONObject("end").getString("dateTime");
            String dateAndTime = DateUtil.getCardStartEnd(start, end);
            holder.mEventTime.setText(dateAndTime);
            String eventTitle = events.getString(EVENT_TITLE).replaceAll(SPACE, " ");
            String eventId = events.getString("id");
            holder.mEventTitle.setText(eventTitle);
            JSONObject desc = new JSONObject(events.getString(DESCRIPTION));
            JSONArray tasks = desc.getJSONArray(TASKS);
            String summary = desc.getString("summary").replaceAll(EventFragment.SPACE, " ");
            String gCalId = events.getJSONObject("organizer").getString("email"); //Email of the Google Calendar


            //This is to set up the task views
            boolean anyTaskExists = setTask(holder.mTask1, tasks.optJSONObject(0));
            anyTaskExists |= setTask(holder.mTask2, tasks.optJSONObject(1));
            anyTaskExists |= setTask(holder.mTask3, tasks.optJSONObject(2));
            //If there's no tasks, get rid of the label that says "Tasks" cause it would be pointless & ugly
            if(!anyTaskExists) {
                holder.mTaskLabel.setVisibility(View.GONE);
            }

            boolean isStudent = !events.has(STUDENT_NAME);
            holder.mTask1.setClickable(isStudent);
            holder.mTask2.setClickable(isStudent);
            holder.mTask3.setClickable(isStudent);
            if(isStudent) {
                CheckBoxListener listener = new CheckBoxListener(gCalId, eventId, eventTitle,
                        summary,  holder);
                holder.mTask1.setOnCheckedChangeListener(listener);
                holder.mTask2.setOnCheckedChangeListener(listener);
                holder.mTask3.setOnCheckedChangeListener(listener);
            }

            holder.mContentView.setText(summary);
        } catch (JSONException e) {
            holder.mIdView.setText(e.getMessage());

            Log.e(TAG, e.getMessage(), e);
        }
    }

    private boolean setTask(CheckBox box, JSONObject task) {
        Log.d("TASK", "Setting task: " + task);
        if(task == null || task.optString(DESCRIPTION).length() == 0) {
            box.setVisibility(View.GONE);
            return false;
        } else {
            box.setChecked(task.optBoolean("done"));
            box.setText(task.optString(DESCRIPTION).replaceAll(SPACE, " "));
            return true;
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "Values size: " + mValues.size());
        return mValues.size();
    }



    /**
     * Our implementation of the RecyclerView.ViewHolder. It holds the values and View objects
     * that are going to be displayed in the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mIdView;
        final TextView mContentView;
        final TextView mTaskLabel;
        String mItem;
        final TextView mEventTitle;
        final TextView mEventTime;
        final CheckBox mTask1;
        final CheckBox mTask2;
        final CheckBox mTask3;


        public ViewHolder(View view) {
            super(view);
            Log.d(TAG, "ViewHolder");
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.student);
            mContentView = (TextView) view.findViewById(R.id.summary);
            mEventTime = (TextView) view.findViewById(R.id.Time);
            mEventTitle = (TextView) view.findViewById(R.id.eventTitle);
            mTaskLabel = (TextView) view.findViewById(R.id.taskLabel);
            mTask1 = (CheckBox) view.findViewById(R.id.item_task1);
            mTask2 = (CheckBox) view.findViewById(R.id.item_task2);
            mTask3 = (CheckBox) view.findViewById(R.id.item_task3);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


    public class CheckBoxListener implements CompoundButton.OnCheckedChangeListener {

        private final String mEventId;

        private final String calId;

        private final String mEventTitle;

        private final String mEventSummary;

        private final ViewHolder holder;

        public CheckBoxListener(String calId, String eventId, String eventTitle, String eventSummary, ViewHolder holder) {
            this.calId = calId;
            this.holder = holder;
            this.mEventSummary = eventSummary;
            this.mEventTitle = eventTitle;
            this.mEventId = eventId;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                JSONObject desc = new JSONObject();
                JSONArray tasksArr = new JSONArray();
                tasksArr.put(getTaskFromBox(holder.mTask1));
                tasksArr.put(getTaskFromBox(holder.mTask2));
                tasksArr.put(getTaskFromBox(holder.mTask3));
                desc.put("tasks", tasksArr);
                desc.put("summary", mEventSummary);
                Log.d(TAG, "executing checked changed: " + desc.toString());
                new Task().execute(desc);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        private class Task extends  AsyncTask<JSONObject, Void, Boolean> {
            @Override
            protected Boolean doInBackground(JSONObject... params) {
                try {

                    Uri uri = new Uri.Builder()
                            .scheme("http")
                            .authority("learner-backend.herokuapp.com")
                            .appendEncodedPath("student")
                            .appendEncodedPath("events")
                            .appendQueryParameter("calId", calId)
                            .appendQueryParameter("event_name", mEventTitle)
                            .appendQueryParameter("event", mEventId)
                            .appendQueryParameter("description", params[0].toString().replaceAll(" ", SPACE))
                            .build();

                    Log.d(TAG, uri.toString());
                    HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                    connection.setRequestMethod("PUT");
                    connection.connect();
                    Scanner in = new Scanner(connection.getInputStream());
                    StringBuilder sb = new StringBuilder();
                    while(in.hasNext()) sb.append(in.next()).append(" ");
                    String response = sb.toString();
                    Log.d(TAG, response);
                    JSONObject obj = new JSONObject(response);
                    return obj.getBoolean("success");
                } catch (IOException | JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                    return false;
                }
            }

            public void onPostExecute(Boolean success) {
                if(!success) {
                    Toast.makeText(null, "Failed to modify event", Toast.LENGTH_LONG).show();
                }
            }

        }

        private JSONObject getTaskFromBox(CheckBox box) throws JSONException {
            JSONObject obj = new JSONObject();
            obj.put("description", box.getText().toString());
            obj.put("done", box.isChecked());
            return obj;
        }
    }
}
