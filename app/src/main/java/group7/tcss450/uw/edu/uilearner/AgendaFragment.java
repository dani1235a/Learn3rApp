package group7.tcss450.uw.edu.uilearner;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AgendaFragment extends Fragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private static final String TAG = "AGENDA";

    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private String mUid;
    private ArrayList<String> mValues;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AgendaFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.agenda_list);

        return view;
    }


    /**
     * Gets the Bundle arguments passed from AgendaActivity that holds the uuid
     * needed to get the User's Calendar events from our back end.
     *
     * @author Connor
     */
    @Override
    public void onStart() {
        Bundle args = getArguments();
        if (args != null) {
            mUid = (String) args.get("uuid");

            AgendaTask agendaTask = new AgendaTask();

            // Gets today's date so the Agenda page Recycler View can populate with
            // events for that day from Google Calendar.
            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(Calendar.YEAR);
            int month = rightNow.get(Calendar.MONTH);
            int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);
            Log.d(TAG, "today's date from Agenda: " + dayOfMonth + "/" + month + "/" + year);

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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(String item);
    }


    /**
     * This inner class will get all of the events for the current day and display it for the user
     * via the AgendaFragment's RecyclerView.
     *
     * @author Connor
     */
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
                        .appendEncodedPath("teacher") //this will need to have a role check for teacher or student
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


        /**
         * Checks if the result ArrayList<String> from doInBackground is empty,
         * if so, then set the "empty_agenda" TextView to Visible and let the user know.
         * Otherwise, set up the RecyclerView and populate it with values from result using
         * the AgendaAdapter.
         *
         * @param result An ArrayList<String> that will never be null, but can be empty.
         * @author Connor
         */
        @Override
        protected void onPostExecute(ArrayList<String> result) {
            TextView empty = (TextView) getActivity().findViewById(R.id.empty_agenda);
            if (!result.isEmpty()) {
                if (empty.getVisibility() != View.GONE) { //in case "empty_agenda" is already visible, make it gone.
                    empty.setVisibility(View.GONE);
                }
                mRecyclerView.setHasFixedSize(true); //change this to false if size doesn't look correct

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(layoutManager);
                RecyclerView.Adapter adapter;
                adapter = new AgendaAdapter(result,
                        (AgendaFragment.OnListFragmentInteractionListener) getActivity());
                mRecyclerView.setAdapter(adapter);
            } else {
                empty.setVisibility(View.VISIBLE);
            }
        }
    }
}
