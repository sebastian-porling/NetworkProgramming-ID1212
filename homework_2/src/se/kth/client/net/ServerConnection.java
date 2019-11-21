package se.kth.client.net;

import se.kth.common.Message;
import se.kth.common.MessageType;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.LinkedBlockingQueue;
import static se.kth.common.Message.deserialize;
import static se.kth.common.Message.serialize;

/**
 *  Server connection is used for making a connection to a server
 *  It also takes care of all communication to the server
 *  connect(OutputHandler)  -   Connects the user to a server, the messages will be shown through handler
 *  disconnect()            -   Simply disconnects, resets sockets.
 *  startNewGame()          -   Will tell the server to start a new game of hangman.
 *  submitGuess(String)     -   Submit guess with a character or a word.
 */
public class ServerConnection implements Runnable {
    private final LinkedBlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Message> readingQueue = new LinkedBlockingQueue<>();
    private final ByteBuffer serverMessage = ByteBuffer.allocateDirect(8192);
    private InetSocketAddress serverAddress;
    private SocketChannel socketChannel;
    private OutputHandler viewObserver;
    private boolean timeToSend = false;
    private Selector selector;
    private boolean connected;

    /**
     * Makes a connection to the server and sets the view observer.
     * @throws IOException When we can't connect
     */
    public void connect() throws IOException {
        serverAddress = new InetSocketAddress(InetAddress.getLocalHost(), 4444);
        new Thread(this).start();
    }

    public void setViewObserver(OutputHandler viewObserver){
        this.viewObserver = viewObserver;
    }

    /**
     * Will disconnect from the connected server
     * @throws IOException When we can't disconnect
     */
    public void disconnect() throws IOException{
        if (connected){
            queueAndSend(MessageType.QUIT, "");
            socketChannel.close();
            socketChannel.keyFor(selector).cancel();
            socketChannel = null;
            connected = false;
        }
    }

    /**
     *  Starts a new game.
     * @throws IOException When we don't have connection
     */
    public void startNewGame() {
        if (connected){
            queueAndSend(MessageType.START, "");
        } else {
            viewObserver.print("Not connected to any server...");
        }
    }

    /**
     *  Makes a guess to the started game.
     * @param guess the guess can be a character or a word in the form of a string
     * @throws IOException When we don't have connection
     */
    public void submitGuess(String guess) {
        if (connected) {
            queueAndSend(MessageType.GUESS, guess);
        } else {
            viewObserver.print("Not connected to any server...");
        }
    }

    /**
     *  queues the message and wakes up the selector for sending
     * @param messageType (MessageType) The message type to be sent
     * @param body (String) The message to be sent
     */
    private void queueAndSend(MessageType messageType, String body) {
        Message message = new Message(messageType, body);
        synchronized (sendingQueue) {
            sendingQueue.add(message);
        }
        this.timeToSend = true;
        selector.wakeup();
    }

    /**
     *  Main program that handles what the server should do.
     */
    @Override
    public void run() {
        try {
            initSelector();
            while (connected) {
                if (timeToSend) {
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    timeToSend = false;
                }
                this.selector.select();
                for (SelectionKey key : this.selector.selectedKeys()) {
                    if (!key.isValid()) continue;
                    checkKey(key);
                    selector.selectedKeys().remove(key);
                }
            }
        } catch (IOException e) {
            viewObserver.print(e.getMessage());
        }
    }

    /**
     * Checks what operation should be done
     * @param key (SelectionKey) The key that we should operate with
     */
    private void checkKey(SelectionKey key) throws IOException{
        if (key.isConnectable()) makeConnection(key);
        else if (key.isReadable()) readFromServer(key);
        else if (key.isWritable()) sendToServer(key);
    }

    /**
     *  Initializes the selector.
     * @throws IOException When somethin is wrong with channel or selector.
     */
    private void initSelector() throws IOException {
        selector = SelectorProvider.provider().openSelector();
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        connected = true;
    }

    /**
     *  Establishes a connection with the server.
     * @param selectionKey (SelectionKey) The channel we establish a connection with
     */
    private void makeConnection(SelectionKey selectionKey) {
        try{
            socketChannel.finishConnect();
            selectionKey.interestOps(SelectionKey.OP_WRITE);
            viewObserver.print("Connected to server");
        }catch (IOException exception){
            viewObserver.print("Couldn't establish connection, try again and check IP and PORT.");
        }
    }

    /**
     *  Will send the a message to the server.
     *  It makes a message object which consists of a message type and the message to be sent
     * @param selectionKey (SelectionKey) The key
     * @throws IOException - Is thrown when we can't write to server
     */
    private void sendToServer(SelectionKey selectionKey) throws IOException{
        if (connected) {
            synchronized (sendingQueue) {
                while (sendingQueue.size() > 0) {
                    byte[] message = serialize(sendingQueue.poll()).getBytes();
                    ByteBuffer messageBuffer = ByteBuffer.wrap(message);
                    socketChannel.write(messageBuffer);
                    if (messageBuffer.hasRemaining()) return;
                }
            }
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

    /**
     *  Reads the messages from the server and prints them.
     * @param selectionKey
     * @throws IOException
     */
    private void readFromServer(SelectionKey selectionKey) throws IOException{
        serverMessage.clear();
        int numOfReadBytes = socketChannel.read(serverMessage);
        if (numOfReadBytes == -1){
            connected = false;
            throw new IOException("Server has closed connection!");
        }
        Message message = deserialize(getMessageFromBuffer());
        readingQueue.add(message);

        while (readingQueue.size() > 0) {
            Message messageToPrint = readingQueue.poll();
            viewObserver.print(formatMessage(messageToPrint));
        }
        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    /**
     *  Gets the serialized message from the server message buffer
     * @return (String) Serialized message
     */
    private String getMessageFromBuffer() {
        serverMessage.flip();
        byte[] bytes = new byte[serverMessage.remaining()];
        serverMessage.get(bytes);
        return new String(bytes);
    }

    /**
     *  Will parse the message and format it for the view.
     * @param message The message to be parsed.
     * @return the message as a string.
     */
    private String formatMessage(Message message){
        String returnMessage = "";
        switch (message.getMessageType()){
            case RESPONSE_GUESS:
            case RESPONSE_START:
                returnMessage =(String) message.getBody();
        }
        return returnMessage;
    }
}
