package se.kth.common;

import java.io.Serializable;

/**
 *  Is a DTO that has the state of the hang man game.
 */
public class GameState implements Serializable {
    private int score;
    private int remainingAttempts;
    private char[] secretWordState;
    private final String word;

    /**
     *  Makes an instance of game state.
     * @param score (Int) - The score for the state
     * @param remainingAttempts (Int) - The remaining attempts for the state
     * @param secretWordState (Char[]) - The secret version of the word
     * @param word (String) - The word
     */
    public GameState(int score, int remainingAttempts, char[] secretWordState, String word) {
        this.score = score;
        this.remainingAttempts = remainingAttempts;
        this.secretWordState = secretWordState;
        this.word = word;
    }

    /**
     * @return (INT) returns the number of remaining attempts
     */
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    /**
     * @return  (INT) returns current score.
     */
    public int getScore() {
        return score;
    }

    /**
     * @return (CHAR[]) The word but in hidden form.
     */
    public char[] getSecretWordState() {
        return secretWordState;
    }

    /**
     * @return (STRING) The word for the game.
     */
    public String getWord(){
        return word;
    }

    /**
     *  Will decrement the score by one.
     */
    public void decrementScore(){
        if (score > 0){
            score--;
        }
    }

    /**
     *  Will decrement the remaining number of attempts by one.
     */
    public void decrementRemainingAttempts(){
        if (remainingAttempts > 0){
            remainingAttempts--;
        }
    }

    /**
     *  Will set the secret word to the new secret word.
     * @param secretWordState (Char[]) - the new secret word
     */
    public void setSecretWordState(char[] secretWordState) {
        this.secretWordState = secretWordState;
    }

    /**
     *  Formats the game state in to a printable object.
     * @return (String) - The game state as a formatted string.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!word.equals("")){
            stringBuilder.append("Word: " + new String(secretWordState));
            stringBuilder.append(" \t Remaining nr attempts: " + remainingAttempts);
        }else {
            stringBuilder.append("Start new game to keep playing");
        }
        stringBuilder.append("\t Score: " + score + "\n");
        return stringBuilder.toString();
    }
}
