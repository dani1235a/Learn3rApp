package group7.tcss450.uw.edu.uilearner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        final EditText email = (EditText) v.findViewById(R.id.editTextForgotPass);
        Button resetPass = (Button) v.findViewById(R.id.buttonResetPass);

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegisterFragment.isValidEmail(email.getText().toString())) {
                    mListener.onForgotPasswordInteraction(email.getText().toString());
                } else {
                    email.setError("Please enter a valid email");
                }
            }
        });


        return v;
    }

    public interface OnFragmentInteractionListener {
        void onForgotPasswordInteraction(String username);
    }
}
