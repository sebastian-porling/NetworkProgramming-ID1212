package se.kth.server.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 *  Takes care of the selection of a word for the game
 */
public class Words {
    private final List<String> WORDS = new ArrayList<>();
    private final String WORDS_PATH = "/assets/words.txt";
    private final String PATH = System.getProperty("user.dir") + WORDS_PATH;

    /**
     *  Creates an instance of the word class and loads the words.
     */
    public Words() {
        loadWords();
    }

    /**
     *  Will take a random word from the list of words.
     * @return  a random word.
     */
    public String getRandomWord(){
        Random random = new Random();
        return WORDS.get(random.nextInt(WORDS.size())).toLowerCase();
    }

    /**
     *  Will load the words from the word file.
     *  And put all word instances in a list.
     */
    private void loadWords(){
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(PATH));
            String line = reader.readLine();
            while (line != null){
                line = line.replaceAll("\n", "");
                String[] lineComponents = line.split(" ");
                Collections.addAll(WORDS, lineComponents);
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.err.println("Word file not found.");
        }
    }
}
