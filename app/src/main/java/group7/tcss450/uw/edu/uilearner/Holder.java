package group7.tcss450.uw.edu.uilearner;

import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;

/**
 * Created by Connor on 11/3/2017.
 *
 * This is a simple holder class that contains the user's email and uuid on
 * sign in. It is used to pass user information between activities.
 */

public class Holder implements Serializable {

    private String mEmail;
    private String mUuid;

    public Holder(String email, String uuid) {
        mEmail = email;
        mUuid = uuid;
    }

    public String getEmail () {
        return mEmail;
    }

    public String getUid () {
        return mUuid;
    }
}
