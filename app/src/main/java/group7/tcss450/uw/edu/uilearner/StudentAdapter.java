package group7.tcss450.uw.edu.uilearner;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Connor on 11/9/2017.
 *
 * This holds the information of each student radio button.
 */

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    public static final String TAG = "EVENT";

    private ArrayList<String> mStudentNames;
    private ArrayList<String> mStudentUids;
    private OnStudentNameInteractionListener mListener;
    private int mHead;


    /**
     * Will create the StudentAdapter using a HashMap<UUID, Name>.
     *
     * @param students
     * @author Connor
     */
    public StudentAdapter(HashMap<String, String> students, OnStudentNameInteractionListener listener) {
        mStudentNames = new ArrayList<String>();
        mStudentUids = new ArrayList<String>();

        Set<String> uuids = students.keySet();
        mStudentUids.addAll(uuids);
        Iterator<String> itr = uuids.iterator();
        while (itr.hasNext()) {
            mStudentNames.add(students.get(itr.next()));
        }

        mListener = listener;
        mHead = 0;
    }


    /**
     * Will create the StudentAdapter using an ArrayList<Names> and an ArrayList<UUID>.
     *
     * @param studentNames
     * @param studentUids
     * @author Connor
     */
    public StudentAdapter(ArrayList<String> studentNames, ArrayList<String> studentUids,
                          OnStudentNameInteractionListener listener) {
        mStudentNames = studentNames;
        mStudentUids = studentUids;
        mListener = listener;
        mHead = 0;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_view, parent, false);
        // set the view's size, margins, paddings, and layout params

        final ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    /**
     * Sets the text of each radio button to the email position given and sets the
     * on click listener for the radio button to be a call to listener's
     * onStudentNameInteraction.
     *
     * @param holder
     * @param position
     *
     * @author Connor
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mRadioButton.setText(mStudentNames.get(position));

        // get the uuid that this radio button will be passing to EventFragment
        // as the current chosen student.
        final String uuid = mStudentUids.get(mHead);
        mHead++;
        holder.mRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onStudentNameInteraction(uuid, holder.mRadioButton);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mStudentNames.size();
    }


    /**
     * This class will hold the RadioButton item which tells the currently selected
     * student name from the list.
     *
     * @author Connor
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public RadioButton mRadioButton;

        public ViewHolder (View v) {
            super(v);
            mRadioButton = (RadioButton) v.findViewById(R.id.choose_student_name);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnStudentNameInteractionListener {
        void onStudentNameInteraction(String uuid, RadioButton chosenRadioButton);
    }
}
