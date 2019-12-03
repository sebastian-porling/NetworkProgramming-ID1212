package client.view;

import java.util.ArrayList;

/**
 * Used for checking the user input
 */
public class CommandLine {
    private String[] params;
    private Command command;
    private static final String PARAM_DELIMITER = " ";

    /**
     *  Creates instance of a CommandLine object.
     *  Will parse the entered input.
     * @param enteredLine (STRING) Input from user.
     */
    CommandLine(String enteredLine){
        determineCommand(enteredLine);
    }

    Command getCommand(){
        if (command == null){
            return null;
        }
        return command;
    }

    /**
     *  Parses a user input to get the command
     * @param enteredLine
     * @return a Command
     */
    private Command parseCommand(String enteredLine) {
        Command command;
        try {
            command = Command.valueOf(enteredLine.toUpperCase());
        } catch (Throwable failedToReadCmd) {
            command = Command.NO_COMMAND;
        }
        return command;
    }

    String getParsedArgument(int index) throws IllegalArgumentException{
        if (params != null && params.length >= index){
            return params[index];
        }
        throw new IllegalArgumentException("Not enough parameters");
    }

    /**
     *  Parses a list of strings. Removes everything that is not alphanumeric.
     * @param enteredParams (STRING[]) Parameters from command line.
     * @return A parsed list of parameters.
     */
    private String[] parseParameters(String[] enteredParams){
        ArrayList<String> parsedParams = new ArrayList<>();
        for (int i = 1; i < enteredParams.length; i++) {
            parsedParams.add(enteredParams[i]);
        }
        return parsedParams.toArray(new String[0]);
    }

    /**
     * Checks all the available commands and parses their parameters.
     * @param enteredLine (STRING) the user operation with params like, "login user password"
     */
    private void determineCommand(String enteredLine){
        String[] enteredParams = enteredLine.split(PARAM_DELIMITER);
        if (enteredParams.length == 0){
            return;
        }
        String[] parsedParams = parseParameters(enteredParams);
        command = parseCommand(enteredParams[0]);

        switch (command){
            case LOGIN:
            case REGISTER:
            case UNREGISTER:
                if (parsedParams.length >= 2){
                    params = parsedParams;
                } else {
                    throw new IllegalArgumentException("Not enough parameters");
                }
                break;
            case DOWNLOAD:
                if (parsedParams.length >= 1){
                    params = parsedParams;
                } else {
                    throw new IllegalArgumentException("Not enough parameters");
                }
                break;
            case UPLOAD:
                if (parsedParams.length >= 3){
                    params = parsedParams;
                } else {
                    throw new IllegalArgumentException("Not enough parameters");
                }
                break;
            case UPDATE:
                if (parsedParams.length >= 1){
                    params = parsedParams;
                } else {
                    throw new IllegalArgumentException("Not enough parameters");
                }
                break;
            case REMOVE:
                if (parsedParams.length >= 1){
                    params = parsedParams;
                } else {
                    throw new IllegalArgumentException("Not enough parameters");
                }
                break;
        }
    }
}
