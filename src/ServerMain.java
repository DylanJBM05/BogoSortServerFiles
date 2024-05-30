/**
 * Server for doing bogo sort.
 * @author Dylan McGowan
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
	//Declarations
	private ServerSocket serverSocket;
	private double timeElapsed = 0;
	private boolean finished = false;
/**
 * Constructor that creates socket if possible
 * @param serverSocket - This is the socket that the server is currently using.
 */
	public ServerMain(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
/**
 * Method for starting server, first starts sorting, then takes in users.
 */
	public void startServer() {
		//Bogosort call
		startSort();
		try {
			//If server is connected
			while (!serverSocket.isClosed()) {
				//Accepts new clients
				Socket socket = serverSocket.accept();
				System.out.println("A new client has connected."); //Connection message
				ClientHandler clientHandler = new ClientHandler(socket, this); //Creates new handler for each client

				Thread thread = new Thread(clientHandler); //Each client is on it's own thread
				thread.start();
			}
		} catch (IOException e) {

		}
	}
/**
 * Method for closing server if things go wrong.
 */
	public void closeServerSocket() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
/**
 * Holds main operations, including starting the server
 * @param args - Default argument for main
 * @throws IOException - Throws in case server can't be created
 */
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(4999);
		ServerMain server = new ServerMain(serverSocket);
		server.startServer();
	}
/**
 * Starts the sorting creation using BogoSort class.
 */
	public void startSort() {
		//Creates bogoSort and it's thread
		BogoSort bogoSort = new BogoSort();
		Thread sortThread = new Thread(bogoSort);
		//Creates new thread for time calculations
		new Thread(new Runnable() {

			@Override
			public void run() {
				long timeStart = System.currentTimeMillis();
				
				while (!finished) {
					//Succeeds once every second to update time
					if ((System.currentTimeMillis() - timeStart) % 1000 == 0) {
						//Updates time elapsed
						timeElapsed = (System.currentTimeMillis() - timeStart) / 1000;
						finished = bogoSort.isFinished(); //Checks state of sorting
					}
				}
				//If completed display this to console.
				System.out.printf("Completed in %.3f seconds\n", timeElapsed);
			}
		}).start();
		sortThread.start(); //Start thread for sorting

	}
/**
 * Getter for time elapsed
 * @return - Returns time elapsed
 */
	public double getTimeElapsed() {
		return timeElapsed;
	}
	/**
	 * Getter for finished
	 * @return - Returns if bogoSort is finished
	 */
	public boolean isFinished() {
		return finished;
	}
}
