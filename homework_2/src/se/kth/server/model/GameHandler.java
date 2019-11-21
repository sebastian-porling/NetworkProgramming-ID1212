package se.kth.server.model;

import se.kth.common.GameState;
import se.kth.common.NotValidGuessException;
import se.kth.server.integration.Words;

/**
 *  The class for handling all the game related operations.
 */
public class GameHandler {
    private final Words words;

    /**
     *  Makes an instance of a game handler.
     */
    public GameHandler(){
        words = new Words();
    }

    /**
     *  Helper function for making the "secret word"
     * @param word A random word.
     * @return Char array with the same amount of characters as the word but with _ instead.
     */
    private char[] makeHiddenWord(String word){
        char[] hiddenWord = new char[word.length()];
        for (int i = 0; i < word.length(); i++) {
            hiddenWord[i] = '_';
        }
        return hiddenWord;
    }

    /**
     *  Helper function for estimate how many attempts needed for the word.
     * @param wordLength Length of the word.
     * @return Number of attempts needed.
     */
    private int estimateRemainingAttempts(int wordLength){
        if (wordLength < 5){
            return 5;
        }
        return wordLength;
    }

    /**
     *  Will start a game,
     *  which makes a game instance with word, secret, score etc.
     * @return game state.
     */
    public GameState startGame(){
        GameState gameState;
        String randomWord = "word";
        char[] hiddenWord = makeHiddenWord(randomWord);
        int remainingAttempts = estimateRemainingAttempts(randomWord.length());
        int score = 0;
        gameState = new GameState(score, remainingAttempts, hiddenWord, randomWord);
        return gameState;
    }

    /**
     *  Will make a new game when an a game state already exists.
     * @param currentGameState the current game state
     * @return new game state with the same score as previous state.
     */
    public GameState newGame(GameState currentGameState){
        String randomWord = words.getRandomWord();
        char[] hiddenWord = makeHiddenWord(randomWord);
        int remainingAttempts = estimateRemainingAttempts(randomWord.length());
        return new GameState(currentGameState.getScore(), remainingAttempts, hiddenWord, randomWord);
    }

    /**
     *  When the user has no remaining attempts left we will decrement the score.
     *  Otherwise just return the current game state
     * @param currentGameState the current game state
     * @return current game state or game state with a decremented score.
     */
    private GameState gameStateWhenLoosing(GameState currentGameState){
        currentGameState.decrementRemainingAttempts();
        if (currentGameState.getRemainingAttempts() == 0){
            currentGameState.decrementScore();
            currentGameState = newGame(currentGameState);
        }
        return currentGameState;
    }

    /**
     *  Used for checking the guessed char and updated the game state
     *  if it was correct or not.
     * @param guess guessed character from the user.
     * @param currentGameState current game state.
     * @return return the new game state. Or just the same if the guess have already been made.
     */
    private GameState guessChar(char guess, GameState currentGameState){
        boolean wrongGuess = true;
        char[] word = revealWord(currentGameState.getWord());
        char[] hiddenWord = currentGameState.getSecretWordState();
        for (int i = 0; i < currentGameState.getWord().length(); i++) {
            if (guess == word[i]){
                hiddenWord[i] = guess;
                wrongGuess = false;
            }
        }
        if (!wrongGuess){
            currentGameState.setSecretWordState(hiddenWord);
        } else {
            currentGameState = gameStateWhenLoosing(currentGameState);
        }
        return currentGameState;
    }

    /**
     *  Will reveal the hidden word.
     * @param word the word to be hidden
     * @return revealed word as char array.
     */
    private char[] revealWord(String word){
        char[] revealedWord = new char[word.length()];
        for (int i = 0; i < word.length(); i++) {
            revealedWord[i] = word.charAt(i);
        }
        return revealedWord;
    }

    /**
     *  Used for checking if the guessed word is correct.
     *  If it is correct we will remove the word and hidden word and increment the score.
     * @param guess The guessed word
     * @param currentGameState the current game state.
     * @return new game state.
     */
    private GameState guessWord(String guess, GameState currentGameState) {
        if (guess.equals(currentGameState.getWord())){
            int score = currentGameState.getScore() + 1;
            char[] empty = {};
            return new GameState(score, 0, empty, "");
        } else {
            currentGameState = gameStateWhenLoosing(currentGameState);
            return currentGameState;
        }
    }

    /**
     * Will check if the word is valid
     * @param guess word to be guessed
     * @param currentGameState current game state
     * @return returns true if valid, false otherwise
     */
    private boolean checkIfValidWord(String guess, GameState currentGameState){
        return guess.length() > 1 && !(guess.length() > currentGameState.getWord().length());
    }

    /**
     * Will check the guess if it is a word or a character and check if it is correct.
     * @param guess word or character as a string
     * @param currentGameState the current game state.
     * @return new game state.
     * @throws NotValidGuessException if the guess does not have the right amount of characters at the word.
     */
    public GameState guess(String guess, GameState currentGameState) throws NotValidGuessException{
        GameState newGameState;
        if (guess.length() == 1){
            char guessedCharacter = guess.charAt(0);
            newGameState = guessChar(guessedCharacter, currentGameState);
        } else if(checkIfValidWord(guess, currentGameState)){
            newGameState = guessWord(guess, currentGameState);
        } else {
            throw new NotValidGuessException(guess, currentGameState.getWord().length());
        }
        return newGameState;
    }
}
