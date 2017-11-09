package group7.tcss450.uw.edu.uilearner;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import group7.tcss450.uw.edu.uilearner.dummy.DummyContent;

/**
 * Created by Connor on 11/9/2017.
 */

public class StudentAdapter extends RecyclerView.Adapter {

    private ArrayList<String> mStudentNames;
    private ArrayList<String> mStudentUids;


    /**
     * Will create the StudentAdapter using a HashMap<UUID, Name>.
     *
     * @param students
     * @author Connor
     */
    public StudentAdapter(HashMap<String, String> students) {
        mStudentNames = new ArrayList<String>();
        mStudentUids = new ArrayList<String>();

        Set<String> uuids = students.keySet();
        mStudentUids.addAll(uuids);
        Iterator<String> itr = uuids.iterator();
        while (itr.hasNext()) {
            mStudentNames.add(students.get(itr.next()));
        }
    }


    /**
     * Will create the StudentAdapter using an ArrayList<Names> and an ArrayList<UUID>.
     *
     * @param studentNames
     * @param studentUids
     * @author Connor
     */
    public StudentAdapter(ArrayList<String> studentNames, ArrayList<String> studentUids) {
        mStudentNames = studentNames;
        mStudentUids = studentUids;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
