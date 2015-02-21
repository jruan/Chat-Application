package GuiFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ChatSignInServer.Server;

public class chatFrame {
	private static JButton createChatButton;
	private static JButton joinChatButton;
	private static JTextField chatRoomNameArea;
	private static JComboBox chatRoomsAvailable;
	private static JTextArea MessageArea; 
	private static JTextArea TypeMessageArea;
	private static JTextArea UsersInChat;
	private static int count = 1;
	private static Server s = new Server();
	
	public void makeOrJoinChatroomFrame(){
		JFrame f = new JFrame("Create/Join Chat");
		f.setSize(400,100);
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel createChatRoom = new JPanel();
		JPanel joinChatRoom = new JPanel();
		
		createChatRoom.setPreferredSize(new Dimension(200, 100));
		joinChatRoom.setPreferredSize(new Dimension(200,100));
		
		JLabel chatRoomName = new JLabel("Room Name");
		JTextField nameArea = new JTextField(10);
		JButton create = new JButton("Create");
		this.createChatButton = create;
		this.chatRoomNameArea = nameArea;
		
		createChatRoom.add(chatRoomName, BorderLayout.WEST);
		createChatRoom.add(nameArea, BorderLayout.EAST);
		createChatRoom.add(create, BorderLayout.SOUTH);
		
		JComboBox listOfChatRooms = new JComboBox();
		JButton join = new JButton("Join");
		this.joinChatButton = join;
		listOfChatRooms.addItem("None");
		listOfChatRooms.setPreferredSize(new Dimension(100, 20));
		listOfChatRooms.setBackground(Color.WHITE);
		this.chatRoomsAvailable = listOfChatRooms;
		
		joinChatRoom.add(listOfChatRooms, BorderLayout.NORTH);
		joinChatRoom.add(join, BorderLayout.SOUTH);
		
		f.add(createChatRoom, BorderLayout.WEST);
		f.add(joinChatRoom, BorderLayout.EAST);
		
		ButtonActions actionsOnPush = new ButtonActions();
		actionsOnPush.createChatroomButton(create, nameArea);
		
		
		actionsOnPush.joinChatroomButton(join, listOfChatRooms);		
		f.setVisible(true);
	}
	
	public void buildChatFrame(String msg){
		Frame f = new Frame();
		f.buildFrame("Chat", false);
		this.MessageArea = f.getDisplayMessageArea();
		this.UsersInChat = f.getUserOnlineArea();
		this.TypeMessageArea = f.getTypeMessageArea();
		this.UsersInChat.append("Users in chat\n");
		JButton send = f.getButton();
		ButtonActions actionOnPush = new ButtonActions();
		JFrame frame = f.getFrame();
		actionOnPush.chatRoomCloseButton(frame, s);
		int i = msg.indexOf("*");
		String user = msg.substring(i + 1, msg.length()).trim();
		s.putIntoChatTextAreaCorrespondingToUser(user, this.MessageArea);
		displayMessage(msg);
		actionOnPush.chatRoomBroadcast(send, this.TypeMessageArea);
	}
	
	public void displayMessage(String msg){
		int i = msg.indexOf("*");
		String user = msg.substring(i+1, msg.length()).trim();
		HashMap<String,JTextArea> temp = s.getChatTextAreaCorrespondingToUser();
		msg = msg.substring(0, i).trim();
		this.MessageArea = temp.get(user);
		this.MessageArea.append(msg + "\n");
		s.putIntoChatTextAreaCorrespondingToUser(user, this.MessageArea);
	}
	
	
	public void displayChatUsers(String msg){
		int i = msg.indexOf(":");
		int underScore = msg.indexOf("_");
		String user = msg.substring(i+1, underScore).trim();
		String last = msg.substring(underScore +1, msg.length()); 
		if(count == 0){
			this.UsersInChat.setText("");
			this.UsersInChat.append("Users in chat\n");
			this.count = this.count + 1;
		}
		if(count == 1){
			if(last.equals("last")){
				this.UsersInChat.append(user + "\n");
				this.count = 0;
			}
			else{
				this.UsersInChat.append(user + "\n");
			}
		}
	}
	
	public void addRoomToComboBox(String chatroom){
		this.chatRoomsAvailable.addItem(chatroom);
	}

	public JButton getCreateChatButton() {
		return createChatButton;
	}

	public JButton getJoinChatButton() {
		return joinChatButton;
	}

	public JTextField getChatRoomNameArea() {
		return chatRoomNameArea;
	}

	public JComboBox getChatRoomsAvailable() {
		return chatRoomsAvailable;
	}

}
