package common;

import java.io.Serializable;

/**
 * A DTO for handling credentials
 */
public class Credentials implements Serializable {
    String username;
    String password;

    public Credentials(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    @Override
    public String toString() {
        return username + " " + password;
    }
}
