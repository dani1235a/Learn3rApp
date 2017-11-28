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
    private String addCode;

    /**
     * Constructor that takes in a String email and a String Password to create a user.
     * @param email - email of user
     * @param password - password of user
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }


    /**
     * Set the name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the Unique ID
     * @param uid
     */
    public void setUid(String uid) { this.uid = uid; }

    /**
     * Set the role (Teacher or Student)
     * @param role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Get the email of the user
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the name of the user.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get the password of the user.
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the Uid
     * @return
     */
    public String getUid() { return uid; }

    /**
     * get the role.
     * @return
     */
    public String getRole(){ return role; }

    public void setAddCode(String addCode) {
        this.addCode = addCode;
    }

    public String getAddCode() {
        return addCode;
    }
}
