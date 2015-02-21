package GuiFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Frame  {
	private static JTextArea displayMessageArea;
	private static JTextArea UserOnlineArea;
	private static JTextArea typeMessageArea;
	private static JFrame f;
	private static JComboBox UsersForPrivateMessaging = new JComboBox();
	private static JButton sendButton;
	
	public void buildFrame(String nameOfFrame, boolean exitOnClose){
		JFrame frame = new JFrame(nameOfFrame);
		frame.setSize(500,500);
		frame.setResizable(false);
		if(exitOnClose)
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.f = frame;
		final JTextArea TypeMessageArea = new JTextArea(9,31);
		JScrollPane scroll = new JScrollPane();
		TypeMessageArea.setLineWrap(true);
		JButton send = new JButton("Send");
		this.sendButton = send;
		this.typeMessageArea = TypeMessageArea;
		send.setPreferredSize(new Dimension(150, 200));
		send.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));		
		scroll.setViewportView(TypeMessageArea);
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BorderLayout());
		messagePanel.add(scroll, BorderLayout.WEST);
		messagePanel.add(send, BorderLayout.EAST);
		messagePanel.setBackground(Color.GRAY);
		messagePanel.setPreferredSize((new Dimension(350,150)));
		
		JPanel chatPanel = new JPanel();
		chatPanel.setPreferredSize(new Dimension(350, 150));
		JTextArea chatArea = new JTextArea(34,30);
		chatArea.setEditable(false);
		chatPanel.setBackground(Color.white);
		JScrollPane chatScroll = new JScrollPane();
		chatScroll.setViewportView(chatArea);
		chatPanel.setLayout(new BorderLayout());
		chatPanel.add(chatScroll,BorderLayout.NORTH);
		this.displayMessageArea = chatArea;
		
		JPanel usersInChat = new JPanel();
		usersInChat.setBackground(Color.LIGHT_GRAY);
		usersInChat.setLayout(new BorderLayout());
		usersInChat.setPreferredSize(new Dimension(150,200));
		JScrollPane scrollpane = new JScrollPane();
		if(nameOfFrame.equals("Broadcast")){
			JTextArea userInChatTextArea = new JTextArea(18,5);
			userInChatTextArea.setEditable(false);
			this.UserOnlineArea = userInChatTextArea;
			userInChatTextArea.append("Online Users");
			userInChatTextArea.append("\n");
			scrollpane.setViewportView(userInChatTextArea);
			this.UsersForPrivateMessaging.addItem("Everyone");
			usersInChat.add(this.UsersForPrivateMessaging, BorderLayout.NORTH);
			usersInChat.add(scrollpane, BorderLayout.CENTER);
		}
		
		else if(nameOfFrame.equals("Private") || nameOfFrame.equals("Chat")){
			usersInChat.setBackground(Color.LIGHT_GRAY);
			usersInChat.setLayout(new BorderLayout());
			JTextArea ChatUsers = new JTextArea(23,5);
			ChatUsers.setEditable(false);
			JScrollPane privateScroll = new JScrollPane();
			privateScroll.setViewportView(ChatUsers);
			usersInChat.setPreferredSize(new Dimension(150,200));
			usersInChat.add(privateScroll, BorderLayout.NORTH);
			this.UserOnlineArea = ChatUsers;
		}
		
		frame.add(usersInChat, BorderLayout.EAST);
		frame.add(chatPanel, BorderLayout.CENTER);
		frame.add(messagePanel, BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}
	
	public JButton getButton(){
		return this.sendButton;
	}
	
	public JFrame getFrame(){
		return this.f;
	}
	
	public JTextArea getDisplayMessageArea() {
		return this.displayMessageArea;
	}

	public JTextArea getUserOnlineArea() {
		return this.UserOnlineArea;
	}

	public JComboBox getUsersForPrivateMessaging() {
		return UsersForPrivateMessaging;
	}

	public JTextArea getTypeMessageArea() {
		return typeMessageArea;
	}

}
