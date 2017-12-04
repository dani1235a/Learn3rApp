package group7.tcss450.uw.edu.uilearner;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Connor on 11/2/2017.
 *
 * This Adapter will populate our blank CardViews for the events list
 * with data from the given data set.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private ArrayList<String> mDataset;


    public CalendarAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }


    /**
     * Creates a new ViewHolder with a blank CardView from the
     * my_text_view layout.
     *
     * @param parent
     * @param viewType
     * @return Blank ViewHolder
     *
     * @author Connor
     */
    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        // set the view's size, margins, paddings, and layout params

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    /**
     * Sets the text of the TextView in the passed ViewHolder to the
     * position in the data set.
     *
     * @param holder
     * @param position
     *
     * @author Connor
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!mDataset.isEmpty()) {
            String currJson = mDataset.get(position);
            String name = "";
            JSONArray summary = null;
            try {
                JSONObject json = new JSONObject(currJson);
                name = json.getString("studentName");
                summary = json.getJSONArray("events");

            } catch (JSONException e) {
                Log.e("Error", "e.printStackTrace()");
            }


            String s = "";
            for (int i = 0; i < summary.length(); i++) {
                try {
                    JSONObject curr = summary.getJSONObject(i);
                    JSONObject desc = new JSONObject(curr.getString("description"));
                    s += ("\n" + desc.getString("summary"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            holder.mCalendarSum.setText(s);
            holder.mTextView.setText(name);
        } else {
            holder.mTextView.setText(R.string.empty_agenda);
        }
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    /**
     *  A helper class that will hold each CardView for our events
     *  list.
     *
     *  @author Connor
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView mCardView;
        public TextView mTextView;
        public TextView mCalendarSum;
//        public final TextView mEventTitle;
//        public final TextView mEventTime;

        public ViewHolder (View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.cv);
            mTextView = (TextView) v.findViewById(R.id.single_event_info);
            mCalendarSum = (TextView) v.findViewById(R.id.calendarSummary);

        }
    }
}
