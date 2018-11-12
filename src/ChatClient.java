import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Random;
import javax.swing.*;

@SuppressWarnings("serial")
public class ChatClient extends JFrame {
	
    private Container container;
    private JTextField textField = new JTextField(10);
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
	
	public ChatClient() {
		super("Chat");
		container = getContentPane();
		
		// Setup Colour changer
		Random rand = new Random();
		randomNum = rand.nextInt(3); // 0-3
		
		// Set up buttons
		send = new JButton("Send");
		colour = new JButton("Colour");
		upload = new JButton("Upload");
		profile = new JButton("Profile");
		ButtonHandler handler = new ButtonHandler();
		send.addActionListener(handler);
		colour.addActionListener(handler);
		upload.addActionListener(handler);
		profile.addActionListener(handler);
		
		// Font and text areas
		Font font1 = new Font("SansSerif", Font.BOLD, 20);
		Font font2 = new Font("SansSerif", Font.BOLD, 18);
		textField.setFont(font1);
		messageArea.setFont(font2);
		messageArea.setEditable(false);
		
		// Create image Icon
		imageLabel = new JLabel();
		Image img = (new ImageIcon("profileIcon.png")).getImage();
        Image newimg = img.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH); 
        ImageIcon Icon = new ImageIcon(newimg);
        imageLabel.setIcon(Icon);
        
        // Top Panel with Image Icon
        topPanel = new JPanel();
		topPanel.setOpaque(false);
        topPanel.add(imageLabel);
        
        // Mid Panel with Text Area
        midPanel = new JPanel();
        midPanel.setOpaque(false);
        midPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
		midPanel.setLayout(new BorderLayout());
		midPanel.add(messageArea);
		
		// Text input & Send button
        inputPanel = new JPanel();
        inputPanel.setOpaque(false);
    	inputPanel.setLayout(new BorderLayout());
		inputPanel.add(textField, BorderLayout.CENTER);
		inputPanel.add(send, BorderLayout.LINE_END);
		
		// Options Buttons
        optionsPanel = new JPanel();
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		optionsPanel.setLayout(new GridLayout(1,3));
		optionsPanel.add(colour);
		optionsPanel.add(upload);
		optionsPanel.add(profile);
		
		// Add the 2 panels to the Bottom
		bottomPanel = new JPanel();
		bottomPanel.setOpaque(false);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(inputPanel, BorderLayout.NORTH);
		bottomPanel.add(optionsPanel, BorderLayout.SOUTH);
		
		// Add all to container
		container.add(topPanel, BorderLayout.NORTH);
        container.add(midPanel, BorderLayout.CENTER);
        container.add(bottomPanel, BorderLayout.SOUTH);
        
		setSize(600, 700);
		setVisible(true);
		
	}
	
	private class ButtonHandler implements ActionListener {
		  public void actionPerformed( ActionEvent event )
		  {
			  if(event.getActionCommand().equals("Send")) {
				  // Send
			  }
			  else if(event.getActionCommand().equals("Colour")) {
				  if(randomNum == 0) container.setBackground(Color.green);
				  else if(randomNum == 1) container.setBackground(Color.yellow);
				  else if(randomNum == 2) container.setBackground(Color.red);
				  randomNum = (randomNum + 1) % 3;
			  }
			  else if(event.getActionCommand().equals("Upload")) {
				  // Upload  
			  }
			  else if(event.getActionCommand().equals("Profile")) {
				  // Profile  
			  }
		  }
	}
	
	private void run() throws IOException{
		System.out.println("Client Running");
	}
	
    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.setVisible(true);
        client.run();
    }
}
