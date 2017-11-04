package group7.tcss450.uw.edu.uilearner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * @author Daniel
 */
public class RegisterFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
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


        Button registerButton = (Button) v.findViewById(R.id.buttonRegister);
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
                    String email = registerEmail.getText().toString();
                    String password = pass1.getText().toString();
                    user = new User(email, password);
                    mListener.onRegisterFragmentInteraction(user);

                    //TODO: Check to see if the email already exists in database
                    Toast.makeText(v.getContext(), "Valid password + Email Combo!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(v.getContext(), "Wrong password + email Combo!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

    /**
     * Checks the email to make sure its a valid email.
     * @param s - the email
     * @return - true if valid email.
     */
    protected static boolean isValidEmail(String s) {
        return s.matches("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)" +
                "|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
    }

    /**
     * Method that checks the password.
     * @param s - input string (password)
     * @return - true if password is valid.
     */
    protected static boolean isValidPassword(String s) {
        return ((s.length() >= 6)&&(!s.equals(s.toLowerCase()))&&(s.matches(".*\\d.*")));
    }



    public interface OnFragmentInteractionListener {
        void onRegisterFragmentInteraction(User user);
    }
}
