package se.kth.server.startup;

import se.kth.server.controller.Controller;
import se.kth.server.net.HangmanServer;

/**
 *  Main class for starting the HangmanServer program.
 */
class Main {

    /**
     * Will start a server at a given port,
     * if no port is given it will start on 4444.
     * @param args The first argument should be a port number between 1-65535
     */
    public static void main(String[] args) {
        Controller controller = new Controller();
        HangmanServer hangmanServer = new HangmanServer(controller);
        if (args.length >= 1){
            hangmanServer.parsePort(args[0]);
        }
        hangmanServer.serve();
    }
}
