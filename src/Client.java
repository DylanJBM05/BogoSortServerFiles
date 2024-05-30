/**
 * Class for client method for BogoSort server.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	//Declarations
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	
	/**
	 * Constructor for client once it connects
	 * @param socket - Connection to server
	 */
	public Client(Socket socket) {
		try {
			//Creates reader and writer connected to socket.
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			//Closes itself if something goes wrong.
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	/**
	 * Method for sending messages to the server.
	 */
	public void sendMessage() {
		try {
			//Uses system input to check server
			Scanner scanner = new Scanner(System.in);
			while(socket.isConnected()) {
				//Checks input in console
				String messageToSend = scanner.nextLine();
				//Sends input to server and flushes
				bufferedWriter.write(messageToSend);
				bufferedWriter.newLine();
				bufferedWriter.flush();
			}
			//When done close scanner
			scanner.close();
		} catch (IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	/**
	 * Method for listening to messages from server, run on seperate thread.
	 */
	public void listenForMessage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String msgFromServer;
			
				while(socket.isConnected()) {
					try {
						//Checks for message using blocking method
						msgFromServer = bufferedReader.readLine();
						System.out.println(msgFromServer);
					} catch (IOException e) {
						//Closes everything on exit
						closeEverything(socket, bufferedReader, bufferedWriter);
					}
				}
			}
		}).start();
	}
	
	/**
	 * Method for closing server if crashes or closes
	 * @param socket - Connection to server
	 * @param bufferedReader - Reader for server
	 * @param bufferedWriter - Writer to server
	 */
	private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		try {
			//Closes reader
			if(bufferedReader != null) {
				bufferedReader.close();
			}
			//Closes writer
			if(bufferedWriter != null) {
				bufferedWriter.close();
			}
			//Closes connection
			if(socket != null) {
				socket.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Main method for client when they start program.
	 * @param args - Default argument for main
	 * @throws IOException - Connection timeout throw.
	 */
	public static void main(String[] args) throws IOException {
		//Connection to server
		Socket socket = new Socket("localhost", 4999);
		Client client = new Client(socket);
		
		//Method calls
		client.listenForMessage();
		client.sendMessage();
	}
}
