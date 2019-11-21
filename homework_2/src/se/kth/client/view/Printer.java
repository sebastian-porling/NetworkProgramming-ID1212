package se.kth.client.view;

/**
 *  Does synchronized printing to the user
 */
class Printer {

    /**
     *  Prints to the standard out synchronously
     * @param output (String) - The string to be printed
     */
    synchronized void print(String output){
        System.out.print(output);
    }

    /**
     *  Prints to the standard out synchronously with new line.
     * @param output (String) - The string to be printed
     */
    synchronized void println(String output){
        System.out.println(output);
    }
}
