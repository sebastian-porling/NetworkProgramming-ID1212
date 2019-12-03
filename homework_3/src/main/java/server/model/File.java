package server.model;

import common.FileDTO;
import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * The file class that is used for handling the state of the file and doing operations for the database
 */
@Entity(name = "Files")
public class File implements FileDTO, Serializable {

    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private User owner;

    @Id
    @Column(name = "filename", unique = true, nullable = false)
    private String filename;

    @Column(name = "size", nullable = false)
    private int size;

    @Column(name = "read_permission", nullable = false)
    private boolean read_permission;

    @Column(name = "write_permission", nullable = false)
    private boolean writePermission;

    public File(User owner, String filename, int size, boolean readPermission, boolean writePermission){
        this.owner = owner;
        this.filename = filename;
        this.size = size;
        this.read_permission = readPermission;
        this.writePermission = writePermission;
    }

    public File() {
    }

    @Override
    public Boolean getReadPermission() {
        return read_permission;
    }

    @Override
    public Boolean getWritePermission() {
        return writePermission;
    }

    @Override
    public String getFileName() {
        return filename;
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public int getSize() {
        return size;
    }

    public String toString(){
        return filename + "\t" + owner.getUserName() + "\t\t" + size + "\t\t" + writePermission + "\t" + read_permission;
    }
}
