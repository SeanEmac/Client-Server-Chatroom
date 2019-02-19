import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;
import java.io.*;

// Sean McCann 16343643
@SuppressWarnings("serial")
public class ChatServer extends JFrame  {
	
	private Container container;
	private static JTextArea messageArea = new JTextArea();
	private JPanel midPanel;
	
	private ServerSocket serverSocket;
	// Store our output streams so that we can write to all of them
	private static HashSet<DataOutputStream> Dos = new HashSet<DataOutputStream>();
	// Keep a record of all chat history
	public volatile static String chatHistory = "";
	
	private int port = 3001;
	
	public ChatServer() {
		super("Server");
		
		// Set up GUI
		container = getContentPane();
		
		// Set Font
		Font font = new Font("SansSerif", Font.BOLD, 18);
		messageArea.setFont(font);
		messageArea.setEditable(false);
		
		// Create Panel
		midPanel = new JPanel();
        midPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		midPanel.setLayout(new BorderLayout());
		midPanel.add(messageArea);
		
		// Add to container and show
		container.add(messageArea, BorderLayout.CENTER);
		messageArea.setText( "Server awaiting connections:\n" );
		
		setSize(600, 700);
		setVisible(true);
	}
	
	private void run() throws IOException, InterruptedException{
		// Create a server socket
		System.out.println("Server Running");
		serverSocket = new ServerSocket(port);
	
		try {
			// By using a thread we can create multiple client connections
			while (true) {
				// Keep waiting for new client connections
				// If we get a new connection, make a Thread to handle that client and start it
	            new ClientHandlerThread(serverSocket.accept()).start();
	        }
		}
		finally {
			// Close the Socket when done
            serverSocket.close();
        }
	}
	
	// Thread to Handle each individual client
	private static class ClientHandlerThread extends Thread {
		private Socket socket;
		private byte[] byteArray;
		private BufferedImage image;
		
		public ClientHandlerThread(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			try {
				// Create an input and output stream for the client
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				// The client will first send its username so Welcome them
				String username = dis.readUTF();
				String welcome = "Welcome to the chat " + username + "\n";
				// Write the connection to the server TextArea
				messageArea.append(username + " connected on " + socket.getRemoteSocketAddress()+ "\n");
				chatHistory += welcome;
				// Chat history is a complete log of all chats
				// it is sent back the the client every time and writes over the old text 
				
				// Add the output stream to the list of clients, this way we can loop through all of them
				Dos.add(dos);
				// Write the old chat history for new clients
				for (DataOutputStream output : Dos) {
					output.writeUTF(chatHistory + "\n");
                }
				
				// This loop keeps waiting for inputs from the client
				while (true) {
					// Keep reading inputs from the client
					String input = dis.readUTF();
					// flag that an image is on its way
					if(input.startsWith("IMAGE")) {
						
						// Prepare and read an image
						byteArray = new byte[10000000];
						dis.read(byteArray);
						image = ImageIO.read(new ByteArrayInputStream(byteArray));
						
						// Make the File with the requested format
						String uniqueID = UUID.randomUUID().toString();
						String date = new SimpleDateFormat("ddMMyyyy").format(new Date());
						String filename = uniqueID + "_" + date + ".jpg";
						File saveFile = new File(filename);
						
						// Save the image to the file
						ImageIO.write(image, "jpg", saveFile);
						
						messageArea.append(username + " uploaded an image\n");
						image = null;
						byteArray = null;
					}
					else {
						// Not an image, Add the text to the chat history and write back to ALL clients 
						chatHistory += input + "\n";
						for (DataOutputStream output : Dos) {
							output.writeUTF(chatHistory + "\n");
	                    }
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
    public static void main(String[] args) throws Exception {
    	// Make a server and Run it
        ChatServer server = new ChatServer();
        server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        server.run();
    }
    
}
