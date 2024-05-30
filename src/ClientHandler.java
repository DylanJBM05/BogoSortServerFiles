/**
 * Class that handles all clients and contains arraylist of all the clients
 * @author Dylan McGowan
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
	//Static array list containing all the handlers
	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
	
	//Declarations
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String clientUserName;
	private ServerMain server;
	
	/**
	 * Constructor for ClientHandler class, creates message if successfully connected to server.
	 * @param socket - Socket that is connected to the end user
	 * @param server - Server class that this class was called from
	 */
	public ClientHandler(Socket socket, ServerMain server) {
		this.server = server;
		try {
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//Creates username based off of where the client is in the list
			this.clientUserName = String.valueOf(clientHandlers.size() + 1);
			clientHandlers.add(this);
			broadcastMessage("Successfully connected to server.");
		} catch(IOException e) {
			//Closes handler if can't connect properly.
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	/**
	 * Thread for broadcasting commands back to user. Sends back if finished or not and gives message if command isn't found.
	 */
	@Override
	public void run() {
		String messageFromClient;
		
		while(socket.isConnected()) {
			try {
				//Reads any messages sent from client
				messageFromClient = bufferedReader.readLine();
				//If the client message is equal to 'finished'
				if (messageFromClient.toLowerCase().equals("finished")) {
					if (server.isFinished()) {
						//Broadcast finished method
						broadcastMessage(String.format("Yes it was, in %.3f seconds.",server.getTimeElapsed()));
					}else {
						//Broadcast not finished method
						broadcastMessage(String.format("Not finished yet, %.3f seconds elapsed",server.getTimeElapsed()));
					}
				} else {
					//Broadcast correct command that can be used
					broadcastMessage("Type 'finished' to get seconds elapsed.");
				}
			} catch (IOException e) {
				//Close everything if something goes wrong.
				closeEverything(socket, bufferedReader, bufferedWriter);
				break;
			}
		}
	}
	/**
	 * Broadcast message method, broadcasts message to itself that includes message that the above method sends.
	 * @param messageToSend
	 */
	public void broadcastMessage(String messageToSend) {
		//Cycles through clients for itself
		for(ClientHandler clientHandler : clientHandlers) {
			try {
				if(clientHandler.clientUserName.equals(clientUserName)) {
					//Writes message and flushes buffer
					clientHandler.bufferedWriter.write(messageToSend);
					clientHandler.bufferedWriter.newLine();
					clientHandler.bufferedWriter.flush();
				}
			} catch(IOException e) {
				//Closes just in case something goes wrong.
				closeEverything(socket, bufferedReader, bufferedWriter);
			}
		}
	}
	/**
	 * Deletes itself so that arraylist doesn't get full.
	 */
	private void removeClientHandler() {
		clientHandlers.remove(this);
	}
	/**
	 * Closes everything when client is finished
	 * @param socket - Client's socket
	 * @param bufferedReader - Reader connected to socket.
	 * @param bufferedWriter - Writer connected to socket.
	 */
	private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		//Removes itself first
		removeClientHandler();
		try {
			//Removes reader
			if(bufferedReader != null) {
				bufferedReader.close();
			}
			//Removes writer
			if(bufferedWriter != null) {
				bufferedWriter.close();
			}
			//Removes connection
			if(socket != null) {
				socket.close();
			}
		//If doesn't disconnect properly print why.
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
