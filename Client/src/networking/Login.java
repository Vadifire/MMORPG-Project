package networking;
import javax.swing.*;

import main.GameClient;
import networking.packets.PacketLogin;
import networking.packets.PacketSendClick;

import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame implements ActionListener
{
	JTextField username = new JTextField(10);
	JTextField password = new JTextField(10);
	JButton login = new JButton("Login");
	public static JLabel status = new JLabel("Welcome. Please log in.");
	JLabel user = new JLabel("Username:");
	JLabel pass = new JLabel("Password:");

	public Login(){
		super("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400,200);
		setResizable(false);
		GridBagLayout g = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,0,0,0);

		setLayout(g);
		
		c.gridx = 0;
		c.gridy = 2;
		add(user,c);
		
		c.gridx = 1;
		c.gridy = 2;
		add(username,c);
		
		c.gridx = 0;
		c.gridy = 3;
		add(pass,c);
		
		c.gridx = 1;
		c.gridy = 3;
		add(password,c);
		
		c.gridx = 1;
		c.gridy = 4;
		add(login,c);
		
		c.gridx = 0;
		c.gridy = 0;
		add(status,c);
		login.addActionListener(this);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == login){
			if (GameClient.playerId == -1){
				status.setText("No server response - please try again.");
				return;
			}
			PacketLogin packet = new PacketLogin(); //tell server of login attempt
			packet.username = username.getText();
			packet.password = password.getText().toLowerCase();
			
			if (packet.username.length() > 14)
			{
				status.setText("Username is "+(packet.username.length()-14)+" characters too long.");
				return;
			}
			if (packet.username.length() < 1)
			{
				status.setText("Please enter a username.");
				return;
			}
			if (packet.password.length() > 50)
			{
				status.setText("Password is too long.");
				return;
			}
			if (packet.password.length() < 5)
			{
				status.setText("Password must be at least 5 characters.");
				return;
			}

				
			packet.loginInt = GameClient.playerId*1000+GameClient.CLIENT_VERSION;//first 3 digits reserved to client version
			GameClient.network.client.sendTCP(packet);
			GameClient.username = packet.username;

			status.setText("Awaiting server response...");
			
		}
	}
}