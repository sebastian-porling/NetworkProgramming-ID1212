package se.kth.client.view;

/**
 *  Defines all commands that can be done by the user interface.
 */
public enum Command {

    /**
     *  Establishes a connection to a server.
     */
    CONNECT,

    /**
     *  Makes guess for the current game, can either be a character or a word.
     */
    GUESS,

    /**
     *  Used when user input doesn't match any of the above.
     */
    NO_COMMAND,

    /**
     *  Disconnects connection from the connected server.
     */
    QUIT,

    /**
     *  Starts a new game of Hangman.
     */
    START

}
