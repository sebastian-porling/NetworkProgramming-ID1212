package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *  Interface used for referencing between the different hosts.
 */
public interface Client extends Remote {
    /**
     * The method the server can use for giving output to the client
     * @param message (STRING) the message to be printed
     * @throws RemoteException if something is wrong with the connection
     */
    void output(String message) throws RemoteException;
}
