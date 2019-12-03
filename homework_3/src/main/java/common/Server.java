package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The reference for the client to use for communication between the server and the client.
 */
public interface Server extends Remote {
    /**
     *  Registers a user on the given credentials
     * @param credentials (CREDENTIALS) the user information
     * @throws RemoteException if the user already exist or connection error
     */
    void register(Credentials credentials) throws RemoteException;

    /**
     *  Unregisters a user on the given credentials
     * @param credentials (CREDENTIALS) the user information
     * @throws RemoteException if the credentials are wrong or connection error
     */
    void unregister(Credentials credentials) throws RemoteException;

    /**
     *  Logs in a user to the server with the given credentials
     * @param client (CLIENT) The reference to the output for the client
     * @param credentials (CREDENTIALS) the user information
     * @return (USERDTO) the login state
     * @throws RemoteException if the credentials are wrong or connection error
     */
    UserDTO login(Client client, Credentials credentials) throws RemoteException;

    /**
     *  Logs the user out from the server
     * @param userDTO (USERDTO) the state of the user
     * @throws RemoteException if the user doesn't exist or connection error
     */
    void logout(UserDTO userDTO) throws RemoteException;

    /**
     *  Uploads a file to the server
     * @param userDTO (USERDTO) the owner of file
     * @param fileName (STRING) the file name
     * @param fileSize (INT) the size/length of file
     * @param writable (BOOLEAN) write permission
     * @param readable (BOOLEAN) read permission
     * @throws RemoteException if the file already exists or user doesn't exist or connection error
     */
    void uploadFile(UserDTO userDTO, String fileName, int fileSize, Boolean writable, Boolean readable) throws RemoteException;

    /**
     *  Deletes the file from the server
     * @param userDTO (USERDTO) the user who tries to remove file
     * @param fileName (STRING) the file name of file to be removed
     * @throws RemoteException
     */
    void removeFile(UserDTO userDTO, String fileName) throws RemoteException;

    /**
     *  downloads a file to the user
     * @param userDTO (USERDTO) the user who tries to download
     * @param fileName (STRING) the file name to be downloaded
     * @throws RemoteException
     */
    void downloadFile(UserDTO userDTO, String fileName) throws RemoteException;

    /**
     *  Updated a file from the server.
     * @param userDTO (USERDTO) the user who tries to update
     * @param fileName (STRING) the file to be updated
     * @param fileSize (INT) the new size of file
     * @throws RemoteException
     */
    void updateFile(UserDTO userDTO, String fileName, int fileSize) throws RemoteException;

    /**
     * lists all the file in the server for the client
     * @return (LIST) of FileDTO.
     * @throws RemoteException if something is wrong with the connection
     */
    List listFiles() throws RemoteException;
}
