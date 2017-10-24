package group7.tcss450.uw.edu.uilearner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherFragment extends Fragment {

    public OnFragmentInteractionListener mOnFragmentInteractionListener;

    public TeacherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_student, container, false);
        Button b = (Button) v.findViewById(R.id.buttonSignIn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Something that happens when when Sign in is Clicked.
                if(isValidPassword(v.findViewById(R.id.editTextPassword))
                        && isValidUser(v.findViewById(R.id.editTextUserName))) {
                    // Sign in Here
                }
            }
        });

        b = (Button) v.findViewById(R.id.buttonRegister);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Something that happens when Register button clicked

            }
        });

        TextView forgotPass = (TextView) v.findViewById(R.id.textViewForgotPassword);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // forgot Password is clicked.
            }
        });
        return v;
    }

    private boolean isValidPassword(View viewById) {
        boolean valid = true;
        // TODO: Password logic and fetch from back end.
        // TODO: limit number of password attempts

        return valid;
    }

    private boolean isValidUser(View viewById) {
        // TODO: User name logic and back end reference.
        boolean valid = true;
        return valid;
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction();
    }

}
