package group7.tcss450.uw.edu.uilearner;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Connor on 11/2/2017.
 *
 * This Adapter will populate our blank CardViews for the events list
 * with data from the given data set.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList<String> mDataset;


    public MyAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }


    /**
     * Creates a new ViewHolder with a blank CardView from the
     * my_text_view layout.
     *
     * @param parent
     * @param viewType
     * @return Blank ViewHolder
     */
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset.get(position));
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    /**
     *  A helper class that will hold each CardView for our events
     *  list.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView mCardView;
        public TextView mTextView;

        public ViewHolder (View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.cv);
            mTextView = (TextView) v.findViewById(R.id.single_event_info);
        }
    }
}
