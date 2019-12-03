package server.model;

import common.Client;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 *  The class for handling all the logged in users.
 */
public class UserManager {
    HashMap<Integer, Client> loggedUsers = new HashMap();

    /**
     *  Checks if the given user is logged in to the server
     * @param user (USER) the user to check if logged in
     * @return True if logged in
     */
    public boolean userIsLoggedIn(User user){
        if(loggedUsers.containsKey(user.getUserID())) return true;
        return false;
    }

    /**
     *  Adds the user to the logged in state
     * @param user (USER) the user to be logged in
     * @param client (CLIENT) the observer for the user
     */
    public void addLoggedUser(User user, Client client){
        if (userIsLoggedIn(user)){
            removeLoggedUser(user);
        }
        loggedUsers.put(user.getUserID(), client);
    }

    /**
     *  Removes/loggs out the given user
     * @param user (USER) the user to be removed/logged out
     */
    public void removeLoggedUser(User user){
        loggedUsers.remove(user.getUserID());
    }

    /**
     *  Notifies the owner of file that an operation has been done to the owners file
     * @param user (USER) the user that made the operation
     * @param file (FILE) the file that has been changed
     * @param operation (STRING) the operation made by the given user
     * @throws RemoteException if something is wrong with conenction
     */
    public void notifyUser(User user, File file, String operation) throws RemoteException {
        System.out.println();
        if (file.getOwner().getUserID() != user.getUserID()){
            if (userIsLoggedIn(file.getOwner())){
                Client client = loggedUsers.get(file.getOwner().getUserID());
                client.output("A user has " + operation + " your file: " + file.getFileName());
            }
        }
    }
}
