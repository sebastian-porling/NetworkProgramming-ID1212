package client.view;

import client.integration.FileManager;
import common.Client;
import common.Credentials;
import common.Server;
import common.UserDTO;
import server.model.File;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

/**
 *  The class that handles the input and output for the user.
 */
public class NonBlockingInterpreter implements Runnable {
    private static final String PROMPT = "$ ";
    private final Scanner console = new Scanner(System.in);
    private InterpreterOutput interpreterOutput;
    private final Printer printer = new Printer();
    private final Client client;
    private UserDTO user = null;
    boolean running = false;
    private Server server;

    public NonBlockingInterpreter() throws RemoteException{
        client = new InterpreterOutput();
    }

    /**
     *  Starts the console for the user and registers the server
     * @param server (SERVER) The server to be registered
     * @throws RemoteException if something is wrong with the connection
     */
    public void start(Server server) throws RemoteException{
        if (running){
            return;
        }
        this.interpreterOutput = new InterpreterOutput();
        this.server = server;
        running = true;
        new Thread(this).start();
    }

    /**
     *  The main for the console program.
     *  The commands it takes are login, logout, register, unregister, list, remove, update, upload, download, quit
     */
    public void run(){
        System.out.println("Connection established.");
        System.out.println("You can now interact with the server");
        while (running){
            try {
                CommandLine commandLine = new CommandLine(readNextLine());
                Command command = commandLine.getCommand();
                if (checkIfLoggedInBeforeCommand(command)){
                    printer.println("Please login before doing that operation");
                    continue;
                }
                switch (command){
                    case LOGIN:
                        user = server.login(interpreterOutput, new Credentials(commandLine.getParsedArgument(0), commandLine.getParsedArgument(1)));
                        printer.println(user.getUserName() + " is logged in to the server.");
                        break;
                    case LOGOUT:
                        server.logout(user);
                        user = null;
                        printer.println("Logged out.");
                        break;
                    case REGISTER:
                        server.register(new Credentials(commandLine.getParsedArgument(0), commandLine.getParsedArgument(1)));
                        printer.println("The user is registered to the server.");
                        break;
                    case UNREGISTER:
                        server.unregister(new Credentials(commandLine.getParsedArgument(0), commandLine.getParsedArgument(1)));
                        printer.println("The user is unregistered to the server.");
                        break;
                    case LIST:
                        List<File> list = server.listFiles();
                        printer.println("Name\t\tOwner\tSize\tWrite\tRead");
                        for (File file: list) {
                            printer.println(file.toString());
                        }
                        break;
                    case REMOVE:
                        server.removeFile(user, commandLine.getParsedArgument(0));
                        break;
                    case UPDATE:
                        String filename = commandLine.getParsedArgument(0);
                        byte[] fileContent = FileManager.readFile(filename);
                        server.updateFile(user, filename, fileContent.length);
                        break;
                    case UPLOAD:
                        String filename1 = commandLine.getParsedArgument(0);
                        byte[] fileContent1 = FileManager.readFile(filename1);
                        server.uploadFile(user, filename1, fileContent1.length,
                                Boolean.parseBoolean(commandLine.getParsedArgument(1)),
                                Boolean.parseBoolean(commandLine.getParsedArgument(2)));
                        break;
                    case DOWNLOAD:
                        server.downloadFile(user, commandLine.getParsedArgument(0));
                        break;
                    case QUIT:
                        printer.println("Quitting...");
                        if (user != null){
                            server.logout(user);
                        }
                        running = false;
                        break;
                    }
            }catch (IllegalArgumentException | IOException exception){
                if (exception.getMessage().contains("RemoteException")){
                    printer.println(exception.getCause().getMessage());
                } else if (exception.getMessage().contains("ConnectException")){
                    printer.println(exception.getCause().getMessage());
                    printer.println("Please restart the client");
                } else {
                    printer.println(exception.getMessage());
                }
            }
        }
        System.exit(0);
    }

    /**
     *  Checks if the user tries to make a command that is illegal before loggin in.
     * @param command (COMMAND) the command to be checked
     * @return (BOOLEAN) true if it is illegal
     */
    private Boolean checkIfLoggedInBeforeCommand(Command command){
        return this.user == null && !command.equals(Command.REGISTER)
                && !command.equals(Command.UNREGISTER)
                && !command.equals(Command.LOGIN)
                && !command.equals(Command.QUIT);
    }

    /**
     * Helper function for getting the next line from user input.
     * @return (STRING) the next line
     */
    private String readNextLine() {
        printer.print(PROMPT);
        String nextLine = console.nextLine();
        return nextLine;
    }

    /**
     *  The class observer that is sent to the server so we can get output for the client.
     */
    private class InterpreterOutput extends UnicastRemoteObject implements Client {
        public InterpreterOutput() throws RemoteException{

        }

        /**
         * The method the server will use for output
         * @param message (STRING) the message to print for the client
         */
        @Override
        public void output(String message){
            printer.println(message);
        }
    }
}
