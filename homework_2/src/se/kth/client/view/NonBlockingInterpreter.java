package se.kth.client.view;

import se.kth.client.controller.Controller;
import se.kth.client.net.OutputHandler;
import java.io.IOException;
import java.util.Scanner;

/**
 *  Is a commandline used for parsing and communicating with a server.
 */
public class NonBlockingInterpreter implements Runnable {
    private final InterpreterOutput interpreterOutput = new InterpreterOutput();
    private final Scanner console = new Scanner(System.in);
    private final Printer printer = new Printer();
    private static final String PROMPT = "$ ";
    private boolean running = false;
    private Controller controller;

    /**
     *  Starts the interpreter.
     */
    public void start(){
        if (running) return;
        running = true;
        controller = new Controller();
        controller.setViewObserver(interpreterOutput);
        new Thread(this).start();
    }

    /**
     *  The main function of the program.
     *  Will check the input if it is a valid command.
     *  If it is valid it will send that command to the connected server.
     */
    @Override
    public void run() {
        printer.println(welcomeMessage());
        while (running){
            try {
                CommandLine commandLine = new CommandLine(readNextLine());
                switch (commandLine.getCommand()){
                    case START:
                        controller.startNewGame();
                        break;
                    case CONNECT:
                        controller.connect();
                        break;
                    case GUESS:
                        controller.submitGuess(commandLine.getParameter());
                        break;
                    case QUIT:
                        controller.disconnect();
                        printer.println("You are now disconnected from the server!\n");
                        break;
                }
            }catch (IllegalArgumentException | IOException exception){
                printer.println(exception.getMessage() + "\n");
            }
        }
    }

    /**
     * Helper function for getting the next line from user input.
     * @return - (String) - The next user input.
     */
    private String readNextLine() {
        printer.print(PROMPT);
        return console.nextLine();
    }

    /**
     *  Creates a welcome message for the user.
     * @return (String) - A welcome message
     */
    private String welcomeMessage(){
        String border = "----------------";
        String stars = "******";
        String titleMessage = border + " WELCOME TO HANGMAN " + border;
        String infoMessagePartOne = stars + " To connect use: connect, To start game or get new word use: start " + stars;
        String infoMessagePartTwo = stars + " \tTo guess use: guess [word/letter], and to quit use: quit \t\t " + stars;
        return "\n" + titleMessage + "\n\n" + infoMessagePartOne + "\n" + infoMessagePartTwo + "\n\n";
    }

    /**
     *  Class used for asynchronous messaging from the server.
     */
    private class InterpreterOutput implements OutputHandler {
        @Override
        public void print(String msg) {
            printer.println(msg);
            printer.print(PROMPT);
        }
    }
}
