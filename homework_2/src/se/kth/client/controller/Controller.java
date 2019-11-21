package se.kth.client.controller;

import se.kth.client.net.OutputHandler;
import se.kth.client.net.ServerConnection;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;

/**
 *  This controller connects the view with the controller.
 *  All the methods are asynchronous. And will print the result through a observer.
 *  connect(OutputHandler)  -   Connects to the server and register the output handler
 *  submitGuess(String)     -   Submits the guessed character or word.
 *  startNewGame()          -   Starts a new instance of the game, score is remained if started before.
 *  disconnect()            -   Disconnects from the server.
 */
public class Controller {
    private final ServerConnection serverConnection;
    private boolean connected = false;

    /**
     *  Creates instance of controller class
     */
    public Controller(){
        serverConnection = new ServerConnection();
    }

    /**
     *  Establishes a connection with a server,
     *  and sets the observer.
     * @param observer Used for showing incoming messages
     */
    public void connect() {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect();
            }catch (IOException exception){
                throw new UncheckedIOException(exception);
            }
        }).thenRun(() ->  connected = true);
    }

    public void setViewObserver(OutputHandler observer){
        CompletableFuture.runAsync(() -> serverConnection.setViewObserver(observer));
    }

    /**
     *  Disconnects from the server.
     * @throws IOException
     */
    public void disconnect() throws IOException{
        if (connected){
            serverConnection.disconnect();
        }
    }

    /**
     *  Starts a new hangman game.
     */
    public void startNewGame(){
        if (connected){
            CompletableFuture.runAsync(() -> serverConnection.startNewGame());
        }
    }

    /**
     *  Submits a game to the running game.
     * @param guess A character or word.
     */
    public void submitGuess(String guess){
        if (connected) {
            CompletableFuture.runAsync(() -> serverConnection.submitGuess(guess));
        }
    }
}
