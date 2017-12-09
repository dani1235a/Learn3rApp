package group7.tcss450.uw.edu.uilearner.auth;


import android.app.AlertDialog;
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
import android.widget.TextView;

import group7.tcss450.uw.edu.uilearner.R;
import group7.tcss450.uw.edu.uilearner.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private User user;

    public SignInFragment() {
        // Required empty public constructor
    }


    /**
     * Sets up the fragment, and links to all the buttons/TextViews.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return - view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);
        Button b = (Button) v.findViewById(R.id.buttonSignIn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pass = (EditText) getActivity().findViewById(R.id.editTextPassword);
                EditText email = (EditText) getActivity().findViewById(R.id.editTextEmail);

                if (RegisterFragment.isValidEmail(email.getText().toString())) {
                    user = new User(email.getText().toString(), pass.getText().toString());
                    mListener.SignInFragmentInteraction(user);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Please enter a valid email address");
                    builder.setPositiveButton("OK", null);
                    builder.show();

                }
            }
        });

        b = (Button) v.findViewById(R.id.buttonRegisterSignIn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.SignInRegisterButtonInteraction();
            }
        });

        TextView forgotPass = (TextView) v.findViewById(R.id.textViewForgotPassword);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // forgot Password is clicked.
                mListener.SignInForgotPasswordInteraction();
            }
        });
        return v;
    }


    /**
        Looks to see if the device is connected to the Internet. If not, disable all buttons
        and set the Internet access message to visible for the user to see. Otherwise, if the
        buttons are already disabled, then reenable them and set the message to GONE.

        Author: Connor Lundberg
     */
    @Override
    public void onResume() {
        super.onResume();

        if (!isNetworkAvailable()) {
//            ((Button) getActivity().findViewById(R.id.buttonRegister)).setEnabled(false);
//            ((Button) getActivity().findViewById(R.id.buttonSignIn)).setEnabled(false);
//            Toast.makeText(getActivity(), getString(R.string.internet_not_connected),
//                    Toast.LENGTH_SHORT);
        } else {
//            ((Button) getActivity().findViewById(R.id.buttonRegister)).setEnabled(true);
//            ((Button) getActivity().findViewById(R.id.buttonSignIn)).setEnabled(true);

        }
    }

    /**
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


    /**
        Assigns the MainActivity as the OnFragmentInteractionListener for this Fragment.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (OnFragmentInteractionListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    /**
     * Listeners for when a button it pressed on this fragment.
     */
    public interface OnFragmentInteractionListener {

        void SignInFragmentInteraction(User user);

        void SignInRegisterButtonInteraction();

        void SignInForgotPasswordInteraction();
    }
}
