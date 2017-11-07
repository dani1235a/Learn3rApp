package group7.tcss450.uw.edu.uilearner.SignIn_Registration;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import group7.tcss450.uw.edu.uilearner.AgendaActivity;
import group7.tcss450.uw.edu.uilearner.MainActivity;
import group7.tcss450.uw.edu.uilearner.R;


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
        mListener = (OnFragmentInteractionListener) getActivity();


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

                        @Override
                        protected Void doInBackground(String... email) {
                            //This latch allows us to figure out when a job is done.
                            // 1 in constructor means 1 job to await.
                            final CountDownLatch latch = new CountDownLatch(1);
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email[0])
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //We're complete, countdown the latch.
                                            latch.countDown();
                                        }
                                    });
                            try {
                                //Wait five seconds for job to finish.
                                Log.d("RESET", "Awaiting latch");
                                latch.await(5000, TimeUnit.MILLISECONDS);
                                Log.d("RESET", "Done with latch");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void v) {
                            Log.d("RESET", "onPostExecute()");
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Sent Email", Toast.LENGTH_SHORT).show();
                            mListener.onForgotPasswordInteraction(email.getText().toString());
                        }
                    }
                    .execute(email.getText().toString());
                }
            }
        });


        return v;
    }

    public interface OnFragmentInteractionListener {
        void onForgotPasswordInteraction(String username);
    }
}
