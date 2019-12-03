package server.model;

import common.UserDTO;
import org.hibernate.annotations.*;
import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * The user class that is used for handling the state of the user and doing operations for the database
 */
@Entity(name = "Users")
public class User implements UserDTO, Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @NaturalId
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User(){

    }

    @Override
    public String getUserName() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public int getUserID() {
        return id;
    }
}
