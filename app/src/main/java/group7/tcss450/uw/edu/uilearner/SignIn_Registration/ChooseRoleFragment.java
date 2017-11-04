package group7.tcss450.uw.edu.uilearner.SignIn_Registration;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import group7.tcss450.uw.edu.uilearner.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseRoleFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public ChooseRoleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_choose_role, container, false);
        Button teacher = (Button) v.findViewById(R.id.buttonRoleTeacher);
        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRoleFragmentInteraction("teacher");
            }
        });

        Button student = (Button) v.findViewById(R.id.buttonRoleStudent);
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRoleFragmentInteraction("student");
            }
        });
        return v;
    }

    public interface OnFragmentInteractionListener {
        void onRoleFragmentInteraction(String role);
    }

}
