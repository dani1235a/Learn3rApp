package group7.tcss450.uw.edu.uilearner.auth;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

import group7.tcss450.uw.edu.uilearner.R;


/**
 * A simple {@link Fragment} subclass. Fragment for "Forgot Password"
 */
public class ForgotPasswordFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    private static final String TAG = "FORGOT";

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    /**
     * Creates the view and sets up the Buttons/TextView fields.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        final EditText email = (EditText) v.findViewById(R.id.editTextForgotPass);
        Button resetPass = (Button) v.findViewById(R.id.buttonResetPass);
        mListener = (OnFragmentInteractionListener) getActivity();


        /**
         * AsyncTask that calls on Firebase to send an email for a password reset to a particular user.
         */
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RESET", "Reset Hit");
                if (!RegisterFragment.isValidEmail(email.getText().toString())) {
                    email.setError("Please enter a valid email");
                } else {
                    Log.d("RESET", email.getText().toString());

                    new AsyncTask<String, Void, Void>() {

                        private ProgressDialog dialog;

                        /**
                         * Create the progress bar.
                         */
                        @Override
                        protected void onPreExecute() {
                            Log.d("RESET", "onPreExecute()");
                            dialog = new ProgressDialog(getContext());
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setMessage("Sending email... Please wait");
                            dialog.setIndeterminate(true);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }

                        /**
                         * Method to actually send the email for a password reset.
                         * @param email
                         * @return
                         */
                        @Override
                        protected Void doInBackground(String... email) {

                            try {
                                Uri uri = new Uri.Builder()
                                        .scheme("http")
                                        .authority("learner-backend.herokuapp.com")
                                        .appendEncodedPath("forgot")
                                        .appendQueryParameter("email", email[0])
                                        .build();

                                Log.d(TAG, uri.toString());
                                HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                                connection.setRequestMethod("POST");
                                connection.connect();
                                Scanner s = new Scanner(connection.getInputStream());
                                StringBuilder sb = new StringBuilder();
                                while (s.hasNext()) sb.append(s.next()).append(" ");
                                return null;
                            } catch (IOException e) {
                                Log.e(TAG, "failed", e);
                                return null;
                            }
                        }

                        /**
                         * Remove the progressbar and proceed.
                         * @param v
                         */
                        @Override
                        protected void onPostExecute(Void v) {
                            Log.d("RESET", "onPostExecute()");
                            dialog.dismiss();

                            //Toast.makeText(getContext(), "Sent Email", Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(ForgotPasswordFragment.this.getContext())
                                    .setMessage("Please check your email for the password reset code")
                                    .setPositiveButton("OK", null)
                                    .show();
                            mListener.onForgotPasswordInteraction(email.getText().toString());
                        }
                    }
                    .execute(email.getText().toString());
                }
            }
        });


        return v;
    }

    /**
     * Interface that gets called when the "Reset" button gets pressed.
     */
    public interface OnFragmentInteractionListener {
        void onForgotPasswordInteraction(String username);
    }
}
