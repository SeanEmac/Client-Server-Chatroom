import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ChatServer extends JFrame  {
	
	private Container container;
	private JTextArea messageArea = new JTextArea(10, 50);
	private JPanel midPanel;
	
	public ChatServer() {
		super("Server");
		
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
		container.add(midPanel, BorderLayout.CENTER);
		setSize(600, 700);
		setVisible(true);
	}
	
	private void run() throws IOException{
		System.out.println("Server Running");
	}
	
    public static void main(String[] args) throws Exception {
        ChatServer server = new ChatServer();
        server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        server.setVisible(true);
        server.run();
    }
    
}
