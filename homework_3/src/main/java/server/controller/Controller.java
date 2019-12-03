package server.controller;

import common.Client;
import common.Credentials;
import common.Server;
import common.UserDTO;
import server.integration.FileDAO;
import server.integration.UserDAO;
import server.model.File;
import server.model.User;
import server.model.UserManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * The methods the client uses.
 */
public class Controller extends UnicastRemoteObject implements Server {
    private UserManager userManager;
    private UserDAO userDAO;
    private FileDAO fileDAO;

    public Controller() throws RemoteException {
        userManager = new UserManager();
        userDAO = new UserDAO();
        fileDAO = new FileDAO();
    }

    /**
     *  Registers a user on the given credentials
     * @param credentials (CREDENTIALS) the user information
     * @throws RemoteException if the user already exist or connection error
     */
    @Override
    public void register(Credentials credentials) throws RemoteException {
        System.out.printf("\nDoing register on \t %s;%s \n", credentials.getUsername(), credentials.getPassword());
        if (userDAO.userExists(credentials.getUsername()) != null){
            throw new RemoteException("That username already exists");
        }
        userDAO.register(new User(credentials.getUsername(), credentials.getPassword()));
    }

    /**
     *  Unregisters a user on the given credentials
     * @param credentials (CREDENTIALS) the user information
     * @throws RemoteException if the credentials are wrong or connection error
     */
    @Override
    public void unregister(Credentials credentials) {
        System.out.printf("\nDoing unregister on \t %s;%s \n", credentials.getUsername(), credentials.getPassword());
        userDAO.unregister(new User(credentials.getUsername(), credentials.getPassword()));
    }

    /**
     *  Logs in a user to the server with the given credentials
     * @param client (CLIENT) The reference to the output for the client
     * @param credentials (CREDENTIALS) the user information
     * @return (USERDTO) the login state
     * @throws RemoteException if the credentials are wrong or connection error
     */
    @Override
    public UserDTO login(Client client, Credentials credentials) throws RemoteException {
        System.out.printf("\nDoing login on \t %s;%s \n", credentials.getUsername(), credentials.getPassword());
        User user = userDAO.login(credentials);
        if (user == null) {
            throw new RemoteException("Invalid credentials");
        }
        if (!userManager.userIsLoggedIn(user)){
            userManager.addLoggedUser(user, client);
        }
        return user;
    }

    /**
     *  Logs the user out from the server
     * @param userDTO (USERDTO) the state of the user
     * @throws RemoteException if the user doesn't exist or connection error
     */
    @Override
    public void logout(UserDTO userDTO)  {
        System.out.printf("\nDoing logout\n");
        userManager.removeLoggedUser((User) userDTO);
    }

    /**
     *  Uploads a file to the server
     * @param userDTO (USERDTO) the owner of file
     * @param fileName (STRING) the file name
     * @param fileSize (INT) the size/length of file
     * @param writable (BOOLEAN) write permission
     * @param readable (BOOLEAN) read permission
     * @throws RemoteException if the file already exists or user doesn't exist or connection error
     */
    @Override
    public void uploadFile(UserDTO userDTO, String fileName, int fileSize, Boolean writable, Boolean readable) throws RemoteException {
        System.out.printf("\nUploading file \t %s;%d;%b;%b\n", fileName, fileSize, writable, readable);
        File file = fileDAO.getFile(fileName);
        if (file == null){
            fileDAO.storeFile(new File((User) userDTO, fileName, fileSize, writable, readable));
        } else {
            throw new RemoteException(fileName + " already exists");
        }

    }

    /**
     *  Deletes the file from the server
     * @param userDTO (USERDTO) the user who tries to remove file
     * @param fileName (STRING) the file name of file to be removed
     * @throws RemoteException
     */
    @Override
    public void removeFile(UserDTO userDTO, String fileName) throws RemoteException {
        System.out.printf("\nRemoving file \t %s\n", fileName);
        File file = fileDAO.getFile(fileName);
        if (file != null) {
            if (checkPermissions(userDTO, file)){
                fileDAO.removeFile(file);
                userManager.notifyUser((User) userDTO, file, "removed");
            } else {
                throw new RemoteException("No permissions for deleting file " + fileName);
            }
        } else {
            throw new RemoteException(fileName + " doesn't exist");
        }
    }

    /**
     *  downloads a file to the user
     * @param userDTO (USERDTO) the user who tries to download
     * @param fileName (STRING) the file name to be downloaded
     * @throws RemoteException
     */
    @Override
    public void downloadFile(UserDTO userDTO, String fileName) throws RemoteException {
        System.out.printf("\nDownloading file \t %s\n", fileName);
        File file = fileDAO.getFile(fileName);
        if (file != null){
            if (file.getOwner().getUserID() == userDTO.getUserID() || file.getReadPermission() == true){
                userManager.notifyUser((User) userDTO, file, "read");
            }
        } else {
            throw new RemoteException(fileName + " doesn't exist");
        }
    }

    /**
     *  Updated a file from the server.
     * @param userDTO (USERDTO) the user who tries to update
     * @param fileName (STRING) the file to be updated
     * @param fileSize (INT) the new size of file
     * @throws RemoteException
     */
    @Override
    public void updateFile(UserDTO userDTO, String fileName, int fileSize) throws RemoteException {
        System.out.printf("\nUpdating file \t %s;%d\n", fileName, fileSize);
        File file = fileDAO.getFile(fileName);
        if (file != null){
            if (checkPermissions(userDTO, file)) {
                fileDAO.updateFile(new File(file.getOwner(), fileName, fileSize, file.getWritePermission(), file.getReadPermission()));
                userManager.notifyUser((User) userDTO, file, "updated");
            } else {
                throw new RemoteException("No permissions for writing on file " + fileName);
            }
        } else {
            throw new RemoteException(fileName + " doesn't exist");
        }
    }

    /**
     *  Checks if the user who tries an operation is the same user.
     * @param file (FILE) The file to be operated on
     * @param user (USER) The user who tries an operation
     * @return
     */
    private boolean checkIfOwner(File file, UserDTO user){
        return file.getOwner().getUserID() == user.getUserID();
    }

    /**
     *  Checks if the user who tries an operation has write permission
     * @param user (USER) The user who tries an operation
     * @param file (FILE) The file to be operated on
     * @return
     */
    private boolean checkPermissions(UserDTO user, File file){
        return (file.getOwner().getUserID() == user.getUserID() || file.getWritePermission() == true);
    }

    /**
     * lists all the file in the server for the client
     * @return (LIST) of FileDTO.
     * @throws RemoteException if something is wrong with the connection
     */
    @Override
    public List listFiles() {
        System.out.printf("\nList files\n");
        List files = fileDAO.listFiles();
        return files;
    }
}
