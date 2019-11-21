package se.kth.server.net;

import java.util.concurrent.LinkedBlockingQueue;
import java.nio.channels.ServerSocketChannel;
import se.kth.server.controller.Controller;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.util.regex.Pattern;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;

/**
 *  Hangman server consists of methods to check validity of a port
 *  and start a server that accepts connections.
 */
public class HangmanServer {
	private final LinkedBlockingQueue<SelectionKey> sendingQueue = new LinkedBlockingQueue<>();
	private ServerSocketChannel serverSocketChannel;
	public final Controller controller;
	private Selector selector;
	private boolean running;
	private int port;

	/**
	 *  Creates an instance of hangman server with port 4444.
	 */
	public HangmanServer(Controller controller){
		this.port = 4444;
		this.controller = controller;
	}

	/**
	 * Parses a given port. If not valid 4444 will be used.
	 * @param port The port to be used for the server.
	 */
	public void parsePort(String port){
		final String PORT_REGEX =
				"(6553[0-5]|655[0-2][0-9]\\d|65[0-4](\\d){2}|6[0-4](\\d){3}|[1-5](\\d){4}|[1-9](\\d){0,3})";
		if (Pattern.matches(PORT_REGEX, port)){
			this.port = Integer.valueOf(port);
		}
	}

	/**
	 *  Starts the server.
	 */
	public void serve(){
		try {
			System.out.println("Starting server...");
			initSelector();
			initServerSocketChannel();
		}catch (IOException exception){
			System.out.println("\nUnable to start server on given port!");
			System.exit(1);
		}
		running = true;
		System.out.println("Accepting new connections!");
		processConnections();
	}

	/**
	 *	Adds the key for the queue to be ready for sending a message to a client.
	 * @param selectionKey (SelectionKey) The key to be added to the queue
	 */
	public void queueMessageForWriting(SelectionKey selectionKey) {
		sendingQueue.add(selectionKey);
	}


	/**
	 * Opens the selector.
	 * @throws IOException When something is wrong with the selector.
	 */
	private void initSelector() throws IOException{ selector = Selector.open(); }

	/**
	 *	Initializes the server on the given port and registers the selector.
	 * @throws IOException When something is wrong when initializing
	 */
	private void initServerSocketChannel() throws IOException{
		InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket = serverSocketChannel.socket();
		serverSocket.bind(inetSocketAddress);
		System.out.println("Server on: " + serverSocket);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 *
	 */
	private void processConnections(){
		while (running){
			try {
				while (!sendingQueue.isEmpty()) {
					sendingQueue.poll().interestOps(SelectionKey.OP_WRITE);
				}
				int numKeys = selector.select();
				if (numKeys > 0){
					Set eventKeys = selector.selectedKeys();
					for (Object eventKey : eventKeys) {
						SelectionKey key = (SelectionKey) eventKey;
						if (!key.isValid()) continue;
						int keyOps = key.readyOps();
						if ((keyOps & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) acceptClientConnection(key);
						if ((keyOps & SelectionKey.OP_READ) == SelectionKey.OP_READ) acceptMessageFromConnection(key);
						if ((keyOps & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) writeMessageToConnection(key);
						selector.selectedKeys().remove(key);
					}
				}
			}catch (IOException exception){
				System.err.println("Couldn't establish a connection.");
			}
		}
	}

	/**
	 *	Establishes a connection with a new client.
	 *	It attaches a client handler to the key.
	 * @param key (SelectionKey) The key the connection comes from.
	 * @throws IOException If something happens when establishing connection.
	 */
	private void acceptClientConnection(SelectionKey key) throws IOException {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		Socket socket = socketChannel.socket();
		ClientHandler clientHandler = new ClientHandler(this, socketChannel);
		System.out.println("Connection on " + socket + ".");
		SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ, clientHandler);
		clientHandler.setSelectionKey(selectionKey);
	}

	/**
	 *	Reads a message from the connection.
	 * @param key (SelectionKey) The key the client is attached to.
	 */
	private void acceptMessageFromConnection(SelectionKey key){
		System.out.println("\nReading message");
		ClientHandler clientHandler = (ClientHandler) key.attachment();
		try {
			clientHandler.readMessage();
		} catch (IOException exception){
			removeClient(key);
		}

	}

	/**
	 * Removes the disconnects the client from the key and cancels it.
	 * @param key (SelectionKey) The key the client is attached to.
	 */
	private void removeClient(SelectionKey key) {
		ClientHandler clientHandler = (ClientHandler) key.attachment();
		clientHandler.disconnect();
		key.cancel();
	}

	/**
	 *	Writes a message to the client attached to the key.
	 * @param key (SelectionKey) The connection to the client
	 * @throws IOException If something is wrong when writing
	 */
	private void writeMessageToConnection(SelectionKey key) throws IOException {
		System.out.println("\nWriting message");
		ClientHandler clientHandler = (ClientHandler) key.attachment();
		clientHandler.writeMessage();
		key.interestOps(SelectionKey.OP_READ);
	}

	/**
	 *	Wakes up the selector
	 */
	public void wakeUpSelector() {
		selector.wakeup();
	}
}

