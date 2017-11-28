package group7.tcss450.uw.edu.uilearner.auth;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import group7.tcss450.uw.edu.uilearner.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class EnterPinFragment extends Fragment implements TextWatcher, View.OnClickListener {

    private static final String TAG = "PIN";

    private OnResetListener mListener;

    private EditText reset;
    private EditText resetConfirm;
    private Collection<View> invisibles;
    private String newPassword;
    private String pin;

    private String email;

    public EnterPinFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if(args != null) {
            email = args.getString("email");
            Log.d(TAG, email);
        } else {
            throw new RuntimeException("no email supplied");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_enter_pin, container, false);
        EditText digits = (EditText) v.findViewById(R.id.digits);
        reset = (EditText) v.findViewById(R.id.resetPass);
        resetConfirm = (EditText) v.findViewById(R.id.resetPassConfirm);
        Button submit = (Button) v.findViewById(R.id.pinSubmit);
        submit.setOnClickListener(this);
        invisibles = Arrays.asList(resetConfirm
                , reset
                , v.findViewById(R.id.resetPassConfirmContainer)
                , v.findViewById(R.id.resetPassContainer)
                , submit);
        digits.addTextChangedListener(this);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnResetListener) context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d(TAG, Integer.toString(count));
        if(s.length() == 6) {
            for(View v : invisibles) v.setVisibility(View.VISIBLE);
            pin = s.toString();
        } else {
            for(View v: invisibles) v.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        if(!RegisterFragment.isValidPassword(reset.getText().toString())) {
            reset.setError("Invalid password");
        }
        if(!RegisterFragment.isValidPassword(resetConfirm.getText().toString())) {
            resetConfirm.setError("Invalid password");
            return;
        }
        if(reset.getText().toString().equals(reset.getText().toString())) {
            newPassword = reset.getText().toString();
            new ResetTask().execute();
        }
    }

    /**
     * Resets password for account.
     */
    private class ResetTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority("learner-backend.herokuapp.com")
                        .appendEncodedPath("reset")
                        .appendQueryParameter("email", email)
                        .appendQueryParameter("pass", newPassword)
                        .appendQueryParameter("pin", pin)
                        .build();

                Log.d(TAG, uri.toString());
                HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                connection.setRequestMethod("POST");
                connection.connect();
                Scanner s = new Scanner(connection.getInputStream());
                StringBuilder sb = new StringBuilder();
                while (s.hasNext()) sb.append(s.next()).append(" ");
                return new JSONObject(sb.toString());
            } catch (IOException | JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                return new JSONObject();
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if(result.optBoolean("success")) mListener.onReset(true, email, newPassword);
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(result.optString("error"));
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing... We just want them to read the dialog
                    }
                });
                builder.show();
            }
        }

    }

    public interface OnResetListener {
        void onReset(boolean success, String email, String newPass);
    }


}
