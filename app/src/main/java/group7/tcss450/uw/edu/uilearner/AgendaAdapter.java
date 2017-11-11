package group7.tcss450.uw.edu.uilearner;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group7.tcss450.uw.edu.uilearner.AgendaFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link String} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}. This one specifically makes the cards
 * that will be displayed on the AgendaFragment.
 *
 * @author Connor
 */
public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.ViewHolder> {

    public static final String TAG = "AGENDA";

    private final ArrayList<String> mValues;
    private final OnListFragmentInteractionListener mListener;


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
            JSONObject events = new JSONObject(mValues.get(position));
            holder.mIdView.setText(events.getString("studentId"));
            JSONArray arr = events.getJSONArray("events");
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < arr.length(); i++) {
                sb.append(arr.getJSONObject(i).getString("summary"))
                        .append("\n");
            }
            holder.mContentView.setText(sb.toString());


        } catch (JSONException e) {
            holder.mIdView.setText("Something went wrong with network request");
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
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            Log.d(TAG, "ViewHolder");
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.summary);
            mContentView = (TextView) view.findViewById(R.id.student);
        }

        @Override
        public java.lang.String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
