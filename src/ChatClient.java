import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

// Sean McCann 16343643
@SuppressWarnings("serial")
public class ChatClient extends JFrame implements Runnable {
	
    private Container container;
    private JTextField textField = new JTextField(30);
    private JTextArea messageArea = new JTextArea(10, 50);
    private JPanel topPanel;
    private JPanel midPanel;
    private JPanel bottomPanel;
    private JPanel inputPanel;
    private JPanel optionsPanel;
    private JLabel imageLabel;
    private JButton send;
    private JButton colour;
    private JButton upload;
    private JButton profile;
    private int randomNum;
    private String username;
    private JFileChooser jfc;
    
    private DataOutputStream output;
    private DataInputStream input;
    private Socket socket;
    private int port = 3001;
    
    ByteArrayOutputStream baos;
    BufferedImage imageSend;
    private File imageFile;
    
	public ChatClient() throws IOException {
		super("Chat");
		
		// Set up GUI
		container = getContentPane();
		
		// Set up buttons
		send = new JButton("Send");
		colour = new JButton("Colour");
		profile = new JButton("Profile");
		upload = new JButton("Upload");
		ButtonHandler handler = new ButtonHandler();
		send.addActionListener(handler);
		colour.addActionListener(handler);
		profile.addActionListener(handler);
		upload.addActionListener(handler);
		
		// Setup Colour changer and call the action listener
		Random rand = new Random();
		randomNum = rand.nextInt(5); // 0-5
		colour.doClick();
		
		// Set the default button for Enter Key to the Send button
		SwingUtilities.getRootPane(container).setDefaultButton(send);

		// Font and text areas
		Font font1 = new Font("SansSerif", Font.BOLD, 20);
		Font font2 = new Font("SansSerif", Font.BOLD, 18);
		textField.setFont(font1);
		messageArea.setFont(font2);
		messageArea.setEditable(false);
		
		// Create image Icon
		imageLabel = new JLabel();
		imageLabel.setHorizontalAlignment(JLabel.CENTER);
		Image img = (new ImageIcon("profileIcon.png")).getImage();
        Image newimg = img.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH); 
        ImageIcon Icon = new ImageIcon(newimg);
        imageLabel.setIcon(Icon);
        
        // Text input & Send button
        inputPanel = new JPanel();
        inputPanel.setOpaque(false);
    	inputPanel.setLayout(new BorderLayout());
		inputPanel.add(textField, BorderLayout.CENTER);
		inputPanel.add(send, BorderLayout.LINE_END);
		
        // Top Panel with Image Icon and text input
		topPanel = new JPanel();
		topPanel.setOpaque(false);
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
		topPanel.setLayout(new BorderLayout());
		topPanel.add(imageLabel, BorderLayout.NORTH);
		topPanel.add(inputPanel, BorderLayout.SOUTH);
        
        // Mid Panel with Text Area
        midPanel = new JPanel();
        midPanel.setOpaque(false);
        midPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
		midPanel.setLayout(new BorderLayout());
		midPanel.add(messageArea);
		
		// Options Buttons
        optionsPanel = new JPanel();
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		optionsPanel.setLayout(new GridLayout(1,3));
		optionsPanel.add(colour);
		optionsPanel.add(profile);
		optionsPanel.add(upload);
		
		// Add the options panel to the bottom
		bottomPanel = new JPanel();
		bottomPanel.setOpaque(false);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(optionsPanel, BorderLayout.SOUTH);
		
		// Add all to container
		container.add(topPanel, BorderLayout.NORTH);
        container.add(midPanel, BorderLayout.CENTER);
        container.add(bottomPanel, BorderLayout.SOUTH);
        
		setSize(600, 700);
		setVisible(true);
		
		// Ask the client for a username before starting
		username = JOptionPane.showInputDialog(container, "Please sign in with a Username");
		
		// Auto select the text field
		textField.requestFocusInWindow();
	}
	
	// Button Handlers 
	private class ButtonHandler implements ActionListener {
		public void actionPerformed( ActionEvent event )
		{
			// If they hit Enter or press Send
			if(event.getActionCommand().equals("Send")) {
				try {
					// Write the username + text value out to server
					output.writeUTF(username + ":\t" + textField.getText());
					textField.setText(null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// Originally assigned a random colour, increment colour every time they click
			else if(event.getActionCommand().equals("Colour")) {
				if(randomNum == 0) container.setBackground(Color.green);
				else if(randomNum == 1) container.setBackground(Color.yellow);
				else if(randomNum == 2) container.setBackground(Color.red);
				else if(randomNum == 3) container.setBackground(Color.blue);
				else if(randomNum == 4) container.setBackground(Color.pink);
				randomNum = (randomNum + 1) % 5;
			}
			
			// Pick a profile picture
			else if(event.getActionCommand().equals("Profile")) {
				// Java File Chooser
				jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				int returnValue = jfc.showOpenDialog(null);
				
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					// Save the selected file for use in 'Upload'
					imageFile = jfc.getSelectedFile();
					System.out.println(imageFile.getAbsolutePath());
					
					try {
						// Change the users profile picture to the selected image
						Image Selected = ImageIO.read(imageFile);
						Image newimg = Selected.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH);
				        ImageIcon Icon = new ImageIcon(newimg);
				        imageLabel.setIcon(Icon);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
			
			// Upload image to server
			else if(event.getActionCommand().equals("Upload")) {
				if(imageFile == null){
					// If they have not selected an image
					JOptionPane.showMessageDialog(container, "Please Select an image first.");
				}
				else {
					try {
						// Read the image we saved earlier
						// Write the image to a Byte Array Output Stream
						imageSend = ImageIO.read(imageFile);
						baos = new ByteArrayOutputStream();
						ImageIO.write(imageSend, "jpg", baos);
						
						// Let the server know we will send an image next
						// Write the Byte Array to the server
						output.writeUTF("IMAGE");
						output.write(baos.toByteArray());
						output.flush();
						
						// Clear variables for next time
				        // I think this helped with errors
				        imageSend = null;
				        imageFile = null;
				        JOptionPane.showMessageDialog(container, "Uploaded to Server!");
				        
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void run() {
		System.out.println("Client Running");
		try {
			// Connect to the Server through a socket
			socket = new Socket("localhost", port);
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			output.writeUTF(username);
			// First send them the username
			while (true) {
				// Read in any inputs from the server and print them to the messageArea
				// The whole chat history is sent every time so just set not append
				String msg = input.readUTF();
				messageArea.setText(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
    public static void main(String[] args) throws Exception {
    	// Create a client and Run it
        ChatClient client = new ChatClient();
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.run();
    }
}
