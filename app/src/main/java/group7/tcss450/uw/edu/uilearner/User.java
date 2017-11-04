package group7.tcss450.uw.edu.uilearner;

import java.io.Serializable;

/**
 * Created by Daniel on 11/3/2017.
 * class designed to hold user objects.
 */

public class User implements Serializable {
    String email;
    String name;
    String password;
    String uid;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    void setEmail(String email) {
        this.email = email;
    }

    void setPassword(String password) {
        this.password = password;
    }

    void setUid(String uid) { this.uid = uid; }

    String getEmail() {
        return email;
    }

    String getName() {
        return name;
    }

    String getPassword() {
        return password;
    }

    String getUid() { return uid; }
}
