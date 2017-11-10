package group7.tcss450.uw.edu.uilearner.SignIn_Registration;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

import group7.tcss450.uw.edu.uilearner.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseRoleFragment extends Fragment {

    public static String IS_TEACHER = "teacher";
    public static String IS_STUDENT = "student";
    private static String TAG = "CHOOSE_ROLE";

    private OnFragmentInteractionListener mListener;

    public ChooseRoleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_choose_role, container, false);

        final EditText addCode = (EditText) v.findViewById(R.id.addCode);



        addCode.setVisibility(View.INVISIBLE);


        final Button register = (Button) v.findViewById(R.id.registerSubmit);
        register.setEnabled(false);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onRoleFragmentInteraction(IS_STUDENT, addCode.getText().toString());
            }
        });

        addCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Don't do anything
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                register.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(addCode.getText().length() > 0) {
                    final String code = addCode.getText().toString();
                    new AsyncTask<Void, Void, Boolean>() {

                        @Override
                        protected Boolean doInBackground(Void... params) {
                            Uri uri = new Uri.Builder()
                                    .scheme("http")
                                    .authority("learner-backend.herokuapp.com")
                                    .appendEncodedPath("teacher")
                                    .appendEncodedPath("code")
                                    .appendQueryParameter("add_code", code)
                                    .build();

                            Log.d(TAG, uri.toString());
                            try {
                                HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                                connection.setRequestMethod("GET");
                                connection.connect();
                                Scanner s = new Scanner(connection.getInputStream());
                                StringBuilder sb = new StringBuilder();
                                while(s.hasNext()) sb.append(s.next());
                                String response = sb.toString();
                                Log.d(TAG, response);
                                return "true".equalsIgnoreCase(response);
                            } catch (IOException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }

                        @Override
                        protected void onPostExecute(Boolean isValid) {
                            if(!isValid) {
                                addCode.setError("Invalid add code");
                                register.setEnabled(false);
                            } else {
                                register.setEnabled(true);
                                addCode.setError(null);

                            }
                        }

                    }.execute();
                }
            }
        });


        RadioGroup group = (RadioGroup) v.findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId == R.id.teacher) {
                    Log.d(TAG, "teacher selected");
                    register.setEnabled(true);
                } else if(checkedId == R.id.student) {
                    Log.d(TAG, "student selected");
                    addCode.setVisibility(View.VISIBLE);
                }
            }
        });




        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }


    public interface OnFragmentInteractionListener {
        void onRoleFragmentInteraction(String role, String addCode);
    }

}
