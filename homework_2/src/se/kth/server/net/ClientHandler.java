package se.kth.server.net;

import java.util.concurrent.LinkedBlockingQueue;
import static se.kth.common.Message.deserialize;
import static se.kth.common.Message.serialize;
import se.kth.server.controller.Controller;
import java.util.concurrent.ForkJoinPool;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import se.kth.common.MessageType;
import se.kth.common.GameState;
import se.kth.common.Message;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 *  Client handler takes care of all messages to one client.
 */
class ClientHandler implements Runnable {
    private final LinkedBlockingQueue<Message> readingQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<>();
    private final ByteBuffer clientMessage = ByteBuffer.allocateDirect(8192);
    private final SocketChannel clientSocketChanel;
    private final HangmanServer hangmanServer;
    private GameState currentGameState = null;
    private final Controller controller;
    private SelectionKey selectionKey;
    private boolean connected;

    /**
     *  Makes an instance of a client handler on the client socket.
     * @param clientSocketChanel Connection to client.
     * @param hangmanServer The server that has main operations for reading and writing.
     */
    ClientHandler(HangmanServer hangmanServer, SocketChannel clientSocketChanel){
        this.clientSocketChanel = clientSocketChanel;
        controller = hangmanServer.controller;
        this.hangmanServer = hangmanServer;
        connected = true;
    }

    /**
     *  The main program for the client - server communication.
     *  Will parse the messages from the client and do the operations it asks for.
     */
    @Override
    public void run() {
        Iterator<Message> messageIterator = readingQueue.iterator();
        while (messageIterator.hasNext() && connected) {
            try {
                Message receivedMessage = messageIterator.next();
                switch (receivedMessage.getMessageType()){
                    case GUESS:
                        if (currentGameState != null){
                            currentGameState = controller.guess((String) receivedMessage.getBody(), currentGameState);
                            sendMessage(MessageType.RESPONSE_GUESS, currentGameState);
                        } else {
                            sendMessage(MessageType.RESPONSE_GUESS, "No game started");
                        }
                        break;
                    case START:
                        if (currentGameState == null){
                            currentGameState = controller.startGame();
                        } else {
                            currentGameState = controller.newGame(currentGameState);
                        }
                        sendMessage(MessageType.RESPONSE_START, currentGameState);
                        break;
                    case QUIT:
                        connected = false;
                        disconnect();
                        break;
                }
            }catch (Throwable err){
                System.err.println("Something went wrong: " + err.getMessage());
            }
            messageIterator.remove();
        }
    }

    /**
     *  Disconnects the client from the server.
     */
    void disconnect() {
        try {
            if (clientSocketChanel != null){
                System.out.println("Shutting down connection...");
                clientSocketChanel.close();
            }
        }catch (IOException exception){
            System.err.println("Unable to disconnect!");
        }
    }

    /**
     *  Reads message from the buffer and adds the message to the reading queue.
     * @throws IOException Error on client socket channel while reading.
     */
    void readMessage() throws IOException{
        clientMessage.clear();
        int numOfReadBytes = clientSocketChanel.read(clientMessage);
        if (numOfReadBytes == -1) throw new IOException("Client has closed connection.");
        readingQueue.add(deserialize(extractMessageFromBuffer()));
        ForkJoinPool.commonPool().execute(this);
    }

    /**
     * Extract the message from the buffer.
     * @return (String) message as string
     */
    private String extractMessageFromBuffer() {
        clientMessage.flip();
        byte[] bytes = new byte[clientMessage.remaining()];
        clientMessage.get(bytes);
        System.out.println(new String(bytes));
        return new String(bytes);
    }

    /**
     *  Writes the message to the client.
     * @throws IOException When something is wrong on the socket chanel while writing
     */
    void writeMessage() throws IOException{
        synchronized (sendingQueue) {
            while (sendingQueue.size() > 0) {
                String out = serialize(sendingQueue.poll());
                System.out.println(out);
                ByteBuffer message = ByteBuffer.wrap(out.getBytes());
                clientSocketChanel.write(message);
            }
        }
    }

    void setSelectionKey(SelectionKey selectionKey){
        this.selectionKey = selectionKey;
    }

    /**
     *  A function that will queue up the message that should be sent.
     * @param messageType What kind of message.
     * @param body The message body we want to send to the client.
     */
    private void sendMessage(MessageType messageType, Object body){
        Message message = new Message(messageType, body);
        synchronized (sendingQueue) {
            sendingQueue.add(message);
        }
        hangmanServer.queueMessageForWriting(this.selectionKey);
        hangmanServer.wakeUpSelector();
    }
}
