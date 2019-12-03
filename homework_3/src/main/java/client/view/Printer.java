package client.view;

/**
 *  A class for handling the output in the client.
 */
public class Printer {

    /**
     *  Prints the line without line break
     * @param output (STRING) the line to be printed
     */
    synchronized void print(String output){
        System.out.print(output);
    }

    /**
     *  Prints the line with line break
     * @param output (STRING) the line to be printed
     */
    synchronized void println(String output){
        System.out.println(output);
    }
}
