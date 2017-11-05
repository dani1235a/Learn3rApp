package group7.tcss450.uw.edu.uilearner;

import java.io.Serializable;

/**
 * Created by Daniel on 11/3/2017.
 * class designed to hold user objects.
 */

public class User implements Serializable {
    private String email;
    private String name;
    private String password;
    private String role;
    private String uid;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUid(String uid) { this.uid = uid; }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getUid() { return uid; }

    public String getRole(){ return role; }

}
