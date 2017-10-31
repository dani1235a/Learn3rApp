package group7.tcss450.uw.edu.uilearner;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentFragment extends Fragment {


    private TeacherFragment.OnFragmentInteractionListener mListener;

    public StudentFragment() {
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
                if(isValidPassword(v.findViewById(R.id.editTextPassword))
                        && isValidUser(v.findViewById(R.id.editTextEmail))) {
                    EditText emailText = (EditText) getActivity().findViewById(R.id.editTextEmail);
                    String email = emailText.getText().toString();

                    EditText passwordText = (EditText) getActivity().findViewById(R.id.editTextPassword);
                    String password = passwordText.getText().toString();
                    mListener.onFragmentInteraction(MainActivity.SIGN_IN, email, password);
                }
            }
        });

        b = (Button) v.findViewById(R.id.buttonRegister);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //For testing until a student register is made
                EditText emailText = (EditText) getActivity().findViewById(R.id.editTextEmail);
                String email = emailText.getText().toString();

                EditText passwordText = (EditText) getActivity().findViewById(R.id.editTextPassword);
                String password = passwordText.getText().toString();
                mListener.onFragmentInteraction(MainActivity.REGISTER, email, password);
                //TODO: new Register Fragment.
            }
        });

        TextView forgotPass = (TextView) v.findViewById(R.id.textViewForgotPassword);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // forgot Password is clicked.
                //TODO: New forgot password Fragment.
            }
        });

        ImageButton ib = (ImageButton) v.findViewById(R.id.sign_out);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFragmentInteraction(MainActivity.SIGN_OUT, null, null);
            }
        });


        return v;
    }


    /*
        Looks to see if the device is connected to the Internet. If not, disable all buttons
        and set the Internet access message to visible for the user to see. Otherwise, if the
        buttons are already disabled, then reenable them and set the message to GONE.

        Author: Connor Lundberg
     */
    @Override
    public void onResume() {
        super.onResume();

        if (!isNetworkAvailable()) {
            ((Button) getActivity().findViewById(R.id.buttonRegister)).setEnabled(false);
            ((Button) getActivity().findViewById(R.id.buttonSignIn)).setEnabled(false);
            Toast.makeText(getActivity(), getString(R.string.internet_not_connected),
                    Toast.LENGTH_SHORT);
        } else {
            ((Button) getActivity().findViewById(R.id.buttonRegister)).setEnabled(true);
            ((Button) getActivity().findViewById(R.id.buttonSignIn)).setEnabled(true);

        }
    }

    /*
            Checks if the device is connected to the Internet.

            Note: This requires the uses-permission, android.permission.ACCESS_NETWORK_STATE

            Author: Connor Lundberg
         */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /*
        Assigns the MainActivity as the OnFragmentInteractionListener for this Fragment.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TeacherFragment.OnFragmentInteractionListener) {
            mListener = (TeacherFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TeacherFragment.OnFragmentInteractionListener");
        }
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
}
