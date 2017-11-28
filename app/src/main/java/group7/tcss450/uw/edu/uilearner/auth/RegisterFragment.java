package group7.tcss450.uw.edu.uilearner.auth;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import group7.tcss450.uw.edu.uilearner.AgendaActivity;
import group7.tcss450.uw.edu.uilearner.R;
import group7.tcss450.uw.edu.uilearner.User;


/**
 * A simple {@link Fragment} subclass.
 * @author Daniel
 */
public class RegisterFragment extends Fragment {

    private OnRegisterFragmentInteractionListener mListener;
    private User user;

    public RegisterFragment() {
        // Required empty public constructor
    }


    /**
     * Redid this method to load the register Fragment correctly.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_register, container, false);
        //The registration email
        final EditText registerEmail = (EditText) v.findViewById(R.id.editTextEmailRegister);

        //First attempt at entering password
        final EditText pass1 = (EditText) v.findViewById(R.id.editTextRegisterPassword1);
        final CheckBox showPass1 = (CheckBox) v.findViewById(R.id.checkBoxShowPassword1);

        showPass1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pass1.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    pass1.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else {
                    pass1.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                }
                pass1.setSelection(pass1.getText().length());
            }
        });


        final EditText pass2 = (EditText) v.findViewById(R.id.editTextReEnterPassword);
        final CheckBox showPass2 = (CheckBox) v.findViewById(R.id.checkBoxShowPass2);

        showPass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pass2.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    pass2.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else {
                    pass2.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                }
                pass2.setSelection(pass2.getText().length());
            }
        });


        final Button registerButton = (Button) v.findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cont = true;

                cont = (isValidEmail(registerEmail.getText().toString()));
                if(!cont) {
                    registerEmail.setError("Email must contain a \"@\" and \".\"");
                }

                if(!pass1.getText().toString().equals(pass2.getText().toString()) && cont) {
                    pass1.setError("Passwords must match!");
                    pass2.setError("Passwords must match!");
                    cont = false;
                } else if(!isValidPassword(pass1.getText().toString()) & cont) {
                    pass1.setError("Password must contain least 6 characters,1 capital, & 1 number");
                    cont = false;
                }

                if(cont) {
                    if (emailAlreadyInUse(registerEmail.getText().toString())) {
                        new android.app.AlertDialog.Builder(RegisterFragment.this.getContext())
                                .setMessage("The email: " + registerEmail.getText().toString()
                                        + "\nis already in use. Please enter a different email.")
                                .setPositiveButton("OK", null)
                                .show();
                    } else {
                        String email = registerEmail.getText().toString();
                        String password = pass1.getText().toString();
                        user = new User(email, password);
                        mListener.onRegisterFragmentInteraction(user);
                    }

                } else {
                    new AlertDialog.Builder(RegisterFragment.this.getContext())
                            .setMessage("Invalid Email + Password combo!")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });

        return v;
    }

    /**
     * Checks if the email is already in the system.
     * @param email - email getting checked
     * @return - true if the email is in use.
     */
    private boolean emailAlreadyInUse(String email) {
        boolean userFound = false;

        return userFound;
        //return !checkEmail(email);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterFragmentInteractionListener) {
            mListener = (OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    /**
     * Checks the email to make sure its a valid email.
     * @param s - the email
     * @return - true if valid email.
     */
    public static boolean isValidEmail(String s) {
        Log.d(AgendaActivity.TAG, "s in isValidEmail is null: " + (s == null));
        //Complicated Regex statement
        return s.matches("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)" +
                "|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
    }

    /**
     * Method that checks the password.
     * @param s - input string (password)
     * @return - true if password is valid.
     */
    public static boolean isValidPassword(String s) {
        Log.d(AgendaActivity.TAG, "s in isValidPassword is null: " + (s == null));
        return ((s.length() >= 6) && (!s.equals(s.toLowerCase())) && (s.matches(".*\\d.*")));
    }


    /**
     * interface that gets called when "Next" button is pressed.
     */
    public interface OnRegisterFragmentInteractionListener {
        void onRegisterFragmentInteraction(User user);
    }
}
