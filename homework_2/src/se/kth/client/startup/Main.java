package se.kth.client.startup;

import se.kth.client.view.NonBlockingInterpreter;

/**
 *  Start-up for client program.
 */
class Main {

    /**
     *  Main function, starts the client program.
     * @param args Arguments ignored.
     */
    public static void main(String[] args) {
        new NonBlockingInterpreter().start();
    }
}
