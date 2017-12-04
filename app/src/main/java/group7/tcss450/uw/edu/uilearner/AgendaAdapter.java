package group7.tcss450.uw.edu.uilearner;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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

    public OnEditButtonInteractionListener mListener;

    private final ArrayList<String> mValues;


    /**
     * Main constructor for AgendaAdapter.
     *
     * @param items The list of String values to put on the text views.
     * @param listener The listener to interact with.
     *
     * @author Connor
     */
    public AgendaAdapter(ArrayList<String> items, OnEditButtonInteractionListener listener) {
        mValues = items;
        mListener = listener;
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


            String start = events.getJSONObject("start").getString("dateTime");
            String end = events.getJSONObject("end").getString("dateTime");
            String dateAndTime = DateUtil.getCardStartEnd(start, end); //Returns MM/DD/YYYY @ HH:MM AM - HH:MM PM
            holder.mEventTime.setText(dateAndTime);

            String eventTitle = events.getString(EVENT_TITLE).replaceAll(SPACE, " ");

            JSONObject desc = new JSONObject(events.getString(DESCRIPTION));
            JSONArray tasks = desc.getJSONArray(TASKS);

            String summary = desc.getString("summary").replaceAll(SPACE, " ");


            //This is to set up the task views
            boolean anyTaskExists = setTask(holder.mTask1, tasks.optJSONObject(0));
            anyTaskExists |= setTask(holder.mTask2, tasks.optJSONObject(1));
            anyTaskExists |= setTask(holder.mTask3, tasks.optJSONObject(2));
            //If there's no tasks, get rid of the label that says "Tasks" cause it would be pointless & ugly
            if(!anyTaskExists) {
                holder.mTaskLabel.setVisibility(View.GONE);
            }

            final boolean taskExists = anyTaskExists;

            //This is a pretty hacky way to figure out whether we're a student or not, but since
            //the server only includes the 'studentName' param when we're requests via /teacher/events,
            //we know if its missing we're a student.
            boolean isStudent = !events.has(STUDENT_NAME);
            final String gCalId = events.getJSONObject("organizer").getString("email"); //Email of the Google Calendar
            final String eventId = events.getString("id");
            holder.mContentView.setText(summary);
            if(isStudent) {
                holder.mEventTitle.setText(eventTitle);
                //The summary here is pointless since the big text usually for email is now
                //the title since student's don't care about their emails.
                holder.mIdView.setVisibility(View.GONE);

                CheckBoxListener listener = new CheckBoxListener(gCalId, eventId, eventTitle,
                        summary,  holder);
                holder.mTask1.setOnCheckedChangeListener(listener);
                holder.mTask2.setOnCheckedChangeListener(listener);
                holder.mTask3.setOnCheckedChangeListener(listener);
                holder.deleteButton.setVisibility(View.GONE);
                holder.editButton.setVisibility(View.GONE);
            } else {
                holder.mIdView.setText(events.optString(STUDENT_NAME));
                holder.mEventTitle.setText(eventTitle);

                holder.mTask1.setClickable(false);
                holder.mTask2.setClickable(false);
                holder.mTask3.setClickable(false);
                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
                        adb.setMessage("Are you sure you want to delete this event?");
                        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                new AsyncTask<Void, Void, Boolean>() {

                                    @Override
                                    protected Boolean doInBackground(Void... params) {
                                        try {
                                            Uri uri = new Uri.Builder()
                                                    .scheme("http")
                                                    .authority("learner-backend.herokuapp.com")
                                                    .appendEncodedPath("teacher")
                                                    .appendEncodedPath("events")
                                                    .appendEncodedPath("delete")
                                                    .appendQueryParameter("calId", gCalId)
                                                    .appendQueryParameter("event", eventId)
                                                    .build();
                                            HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                                            connection.setRequestMethod("POST");
                                            connection.connect();
                                            Scanner in = new Scanner(connection.getInputStream());
                                            StringBuilder sb = new StringBuilder();
                                            while (in.hasNext()) sb.append(in.next()).append(" ");
                                            String response = sb.toString();
                                            Log.d(TAG, response);
                                            JSONObject obj = new JSONObject(response);
                                            return obj.getBoolean("success");
                                        } catch (Exception e) {
                                            Log.e(TAG, "failed to request: ", e);
                                            return false;
                                        }
                                    }


                                    @Override
                                    protected void onPostExecute(Boolean success) {
                                        triggerRefresh(holder.getAdapterPosition());
                                        dialog.dismiss();
                                    }
                                }.execute();
                            }
                        });
                        adb.setCancelable(true);
                        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        adb.show();
                    }
                });

                holder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
                        adb.setMessage("Are you sure you want to edit this event?" +
                                " The state of your student's tasks will be cleared");
                        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                String id = holder.mIdView.getText().toString();
                                String title = holder.mEventTitle.getText().toString();

                                String tmp = holder.mEventTime.getText().toString();
                                String[] helper = tmp.split("@");
                                String date = helper[0];

                                String[] times = helper[1].split("-");
                                String startTime = times[0];
                                String endTime = times[1];

                                String summary = holder.mContentView.getText().toString();

                                String[] tasks = new String[3];
                                if (taskExists) {
                                    if (!holder.mTask1.getText().toString().equals("")) {
                                        tasks[0] = holder.mTask1.getText().toString();
                                    }

                                    if (!holder.mTask2.getText().toString().equals("")) {
                                        tasks[1] = holder.mTask2.getText().toString();
                                    }

                                    if (!holder.mTask3.getText().toString().equals("")) {
                                        tasks[2] = holder.mTask3.getText().toString();
                                    }
                                }

                                mListener.onEditButtonInteraction(id, title, date, gCalId, eventId,
                                        startTime, endTime, summary, tasks);
                            }
                        });
                        adb.setCancelable(true);
                        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        adb.show();
                    }
                });
            }

        } catch (JSONException e) {
            holder.mIdView.setText(e.getMessage());

            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void triggerRefresh(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
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
        final ImageButton deleteButton;
        final ImageButton editButton;


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
            deleteButton = (ImageButton) view.findViewById(R.id.deleteEvent);
            editButton = (ImageButton) view.findViewById(R.id.editEvent);
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

        private JSONObject getTaskFromBox(CheckBox box) throws JSONException {
            JSONObject obj = new JSONObject();
            obj.put("description", box.getText().toString());
            obj.put("done", box.isChecked());
            return obj;
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
    }


    public interface OnEditButtonInteractionListener {
        public void onEditButtonInteraction (String studentId, String title, String date,
                                             String gCalid,String eventId,String startTime,
                                             String endTime, String summary, String[] tasks);
    }
}
