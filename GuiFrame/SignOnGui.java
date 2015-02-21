package GuiFrame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;

import javax.jms.JMSException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import CSE110ChatSystem.ChatSignInClient.ChatClient;
import CSE110ChatSystem.ChatSignInClient.clientConnection;
import MessageTypes.TypeOfMessage;

public class SignOnGui {
	private static JButton signOnButton;
	private static JButton signOffButton;
	private static JTextField username;
	private static JTextField password;
	private clientConnection client = new clientConnection();
	public void signOnFrame(){
		JFrame f = new JFrame("Sign In");
		f.setSize(250,150);
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		final JTextField username = new JTextField(10);
		final JPasswordField password = new JPasswordField(10);

		JPanel jp = new JPanel();
		JLabel lb = new JLabel("Password");
		JLabel lbu = new JLabel("Username");
		JButton signOn = new JButton("Sign On");
		JButton signOff = new JButton("Sign Off");
		this.signOnButton = signOn;
		this.signOffButton = signOff;
		f.add(jp);
		
		password.setEchoChar('*');
		this.username = username;
		this.password = password;
	
		jp.add(lbu, BorderLayout.WEST);
		jp.add(username, BorderLayout.EAST);
		jp.add(lb, BorderLayout.WEST);
		jp.add(password, BorderLayout.EAST);
		jp.add(signOn, BorderLayout.WEST);
		jp.add(signOff, BorderLayout.EAST);
		f.setVisible(true);
	}
	
	public JButton getSignOnButton(){
		return this.signOnButton;
	}
	
	public JButton getSignOffButton(){
		return this.signOffButton;
	}
	
	public JTextField getUserName(){
		return this.username;
	}
	
	public JTextField getPassword(){
		return this.password;
	}
}
