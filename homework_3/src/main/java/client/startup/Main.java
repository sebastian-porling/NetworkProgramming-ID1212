package client.startup;

import client.view.NonBlockingInterpreter;
import java.rmi.Naming;
import common.Server;

/**
 *  The main class for the client
 */
public class Main {

    /**
     *  Starts a client that communicates to the server in the register.
     * @param args Ignored.
     */
    public static void main(String[] args) {
        try {
            Server server = (Server) Naming.lookup("server");
            new NonBlockingInterpreter().start(server);
        }catch (Exception exception){
            System.out.println("Unable to start the interpreter");
        }
    }
}
