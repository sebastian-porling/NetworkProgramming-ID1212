package se.kth.client.view;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *  Parses user commands,
 */
class CommandLine {
    private static final String PARAM_DELIMITER = " ";
    private final String IP_REGEX =
            "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])";
    private final String PORT_REGEX =
            "(6553[0-5]|655[0-2][0-9]\\d|65[0-4](\\d){2}|6[0-4](\\d){3}|[1-5](\\d){4}|[1-9](\\d){0,3})";
    private final String ALPHANUMERIC_REGEX = "[a-zA-Z0-9.]+";
    private String[] params;
    private Command command;

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
     *  Gets the parameter of given index.
     * @return returns a parameter or null.
     */
    String getParameter(){
        if (params != null){
            return params[0];
        }
        return null;
    }

    /**
     *  Checks if the given string is alphanumeric.
     * @param line (STRING)
     * @return (BOOLEAN)
     */
    private boolean checkIfAlphanumeric(String line){ return line.matches(ALPHANUMERIC_REGEX); }

    /**
     *  Parses a list of strings. Removes everything that is not alphanumeric.
     * @param enteredParams (STRING[]) Parameters from command line.
     * @return A parsed list of parameters.
     */
    private String[] parseParameters(String[] enteredParams){
        ArrayList<String> parsedParams = new ArrayList<>();
        for (int i = 1; i < enteredParams.length; i++) {
            if (checkIfAlphanumeric(enteredParams[i])){
                parsedParams.add(enteredParams[i]);
            }
        }
        return parsedParams.toArray(new String[0]);
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

    /**
     *  Will determine what kind of command it is and check its parameters.
     * @param enteredLine The user input
     */
    private void determineCommand(String enteredLine){
        String[] enteredParams = enteredLine.split(PARAM_DELIMITER);
        if (enteredParams.length == 0){
            return;
        }
        String[] parsedParams = parseParameters(enteredParams);
        command = parseCommand(enteredParams[0]);
        switch (command){
            case GUESS:
                if (parsedParams.length == 0){
                    throw new IllegalArgumentException("Missing guessed letter/word");
                }
                params = parsedParams;
                break;
            case CONNECT:
                if (parsedParams.length == 0){
                    params = new String[] {"127.0.0.0", "4444"};
                    return;
                } else if (parsedParams.length == 2){
                    if (!Pattern.matches(IP_REGEX, parsedParams[0])){
                        throw new IllegalArgumentException("Invalid IP address");
                    }
                    if (!Pattern.matches(PORT_REGEX, parsedParams[1])){
                        throw new IllegalArgumentException("Invalid port");
                    }
                    params = new String[] {parsedParams[0], parsedParams[1]};
                }else{
                    throw new IllegalArgumentException("There is to many or to few parameters");
                }
                break;
        }
    }
}
