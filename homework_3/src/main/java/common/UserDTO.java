package common;

import java.io.Serializable;

/**
 * DTO for the user information for logged in state and handling operations in database.
 */
public interface UserDTO extends Serializable {

    String getUserName();

    String getPassword();

    int getUserID();
}
