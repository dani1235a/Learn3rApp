package group7.tcss450.uw.edu.uilearner;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayFragment extends Fragment {


    public DisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_display, container, false);


        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currUser != null) {
            TextView email = ((TextView) getActivity().findViewById(R.id.email_address));
            String userEmail = currUser.getEmail();
            Log.e(MainActivity.TAG, "userEmail == null: " + (userEmail == null));
            Log.e(MainActivity.TAG, "email textview == null: " + (email == null));
            email.setText(userEmail);
            ((TextView) getActivity().findViewById(R.id.is_verified)).setText("Is Verified? " + currUser.isEmailVerified());
        } else {
            Log.e(MainActivity.TAG, "Current User was null!");
        }
    }
}
