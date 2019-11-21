package se.kth.server.controller;

import se.kth.common.GameState;
import se.kth.common.NotValidGuessException;
import se.kth.server.model.GameHandler;

/**
 *  This controller takes care of the state of the game through the model.
 *  All the methods are done with the GameHandler class.
 */
public class Controller {
    private final GameHandler gameHandler;

    /**
     *  Creates an instance of the controller
     */
    public Controller(){
        gameHandler = new GameHandler();
    }

    /**
     * Will submit the guess.
     * @param characters Guessed word or character
     * @param currentState  The current game state
     * @return New State
     */
    public GameState guess(String characters, GameState currentState){
        GameState newState;
        try {
            newState = gameHandler.guess(characters, currentState);
        }catch (NotValidGuessException exception){
            newState = currentState;
            System.err.println("*** Got a not valid guess ***");
        }
        return newState;
    }

    /**
     * Starts a new game.
     * @return Game state
     */
    public GameState startGame(){
        return gameHandler.startGame();
    }

    /**
     * Will make a new word and the other stuff needed for a new hangman game.
     * Score is remained.
     * @param currentGameState Current game state.
     * @return New game state.
     */
    public GameState newGame(GameState currentGameState){
        return gameHandler.newGame(currentGameState);
    }
}
