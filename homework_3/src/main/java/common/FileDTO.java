package common;

import server.model.User;

import java.io.Serializable;

/**
 * A DTO for the file structure.
 */
public interface FileDTO extends Serializable {

    Boolean getReadPermission();

    Boolean getWritePermission();

    String getFileName();

    User getOwner();

    int getSize();
}
