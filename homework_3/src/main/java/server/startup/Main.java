package server.startup;

import server.controller.Controller;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * The main class for the server
 */
public class Main {

    /**
     *  Starts the server, starts the registry and binds the controller to the registry.
     * @param args Ignored
     */
    public static void main(String[] args) {
        try {
            new Main().startRegistry();
            Naming.rebind("server", new Controller());
            System.out.println("Server is running.");
        } catch (MalformedURLException | RemoteException exception) {
            System.out.println("Could not start server.");
        }
    }

    /**
     *  Starts the RMI registry
     * @throws RemoteException if something is wrong with the connection to RMI
     */
    private void startRegistry() throws RemoteException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryIsRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
    }
}
