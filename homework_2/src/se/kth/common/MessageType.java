package se.kth.common;

/**
 *  The different message types used by the server and client.
 */
public enum  MessageType {
    /**
     *  Message to start the game, from client to server
     */
    START,

    /**
     *  Message to guess a character or word, from client to server
     */
    GUESS,

    /**
     *  Message to quit, from client to server
     */
    QUIT,

    /**
     *  Response message from server
     */
    RESPONSE_START,

    /**
     *  Response message from server
     */
    RESPONSE_GUESS

}
