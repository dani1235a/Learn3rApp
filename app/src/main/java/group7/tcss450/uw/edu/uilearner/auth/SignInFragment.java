package group7.tcss450.uw.edu.uilearner.auth;


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
 * A simple {@link Fragment} subclass. This handles our sign in screen, which is also
 * the default home screen if the user isn't already signed in.
 *
 * @author Myles, Connor, Daniel
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
     *
     * @author Connor, Myles, Daniel
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

                if (RegisterFragment.isValidEmail(email.getText().toString())
                        && RegisterFragment.isValidPassword(pass.getText().toString())) {

                    user = new User(email.getText().toString(), pass.getText().toString());
                    mListener.SignInFragmentInteraction(user);
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
