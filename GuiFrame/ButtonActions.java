package GuiFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import CSE110ChatSystem.ChatSignInClient.ChatClient;
import CSE110ChatSystem.ChatSignInClient.clientConnection;
import ChatSignInServer.Server;
import MessageTypes.TypeOfMessage;

public class ButtonActions {
	private clientConnection client = new clientConnection();
	private static ChatClient cl;
	private static String username;
	private static String chatRoomName;
	private static String privateReceiver;
	private static Set<String> inChatRoom = new HashSet<String>();
	
	public void signOn(JButton signOn, final JTextField username, final JTextField password){
		signOn.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						String user = username.getText();
						String pass = password.getText();		
						try {
							ChatClient c = client.makeConnection(user);
							setChatClient(c);
							setUser(user);
							TypeOfMessage messageType = new TypeOfMessage("Sign On:" + user + "^" + pass);
							c.send(messageType);
							c.receive();
						} catch (JMSException e1) {
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
				}
			);
	}
	
	public void signOff(JButton signOff){
		signOff.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){					
						try {
							System.out.println("in button");
							ChatClient c = getChatClient();
							String user = getUser();
							TypeOfMessage messageType = new TypeOfMessage("Sign Out:" + user);
							c.send(messageType);
							c.receive();
						} catch (JMSException e1) {
							e1.printStackTrace();
						}
					}
				}
			);
	}
	public void broadCastSend(JButton send, final JTextArea messageText, final JComboBox availablePrivateRecipient){
		send.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						String msg = messageText.getText();
						String user = getUser();
						JComboBox box = availablePrivateRecipient;
						ChatClient c = getChatClient();
						messageText.setText("");
						
						if(!(box.getSelectedItem().equals("Everyone"))){
							try {
								TypeOfMessage messageType = new TypeOfMessage("New Private: " + "[" + user + "] " + msg + "{" + box.getSelectedItem() + ")");
								c.send(messageType);
								setPrivateReceiver((String)box.getSelectedItem());
								box.setSelectedIndex(0);
								c.receive();
							} catch (JMSException ex) {
								ex.printStackTrace();
							}
						}
						
						else{
							try {
								TypeOfMessage messageType = new TypeOfMessage("Broadcast: " + "[" + user + "] " + msg);
								c.send(messageType);
								c.receive();
							} catch (JMSException ex) {
								ex.printStackTrace();
							}
						}
						
					}
				}
		);
	}
	
	public void privateSend(JButton send, final JTextArea messageText, final JFrame f, final Server s){
		send.addActionListener(
				new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						String msg = messageText.getText();
						String u = getUser();
						String receiver = "";
						ChatClient c = getChatClient();
						HashMap<JFrame, String> map = s.getprivateFrameCorrespondingToUser();
						String str = map.get(f);
						String [] usersInPrivateMessage  = str.split(":");
						for(int i = 0; i < usersInPrivateMessage.length; i++){
							if(!(usersInPrivateMessage[i].equals(u))){
								receiver = usersInPrivateMessage[i];
							}
						}
						receiver = receiver.trim();
						messageText.setText("");
						try {
							TypeOfMessage messageType = new TypeOfMessage("Existing Private: " + "[" + u +"] " + msg + "{" + receiver +")");
							c.send(messageType);
							c.receive();
						} catch (JMSException e1) {
							e1.printStackTrace();
						}
						
						
					}
					
				 }
				);
	}
	
	public void createChatroomButton(JButton create, final JTextField roomNameArea){
		create.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						String roomName = roomNameArea.getText();
						roomName = roomName.trim();
						Set<String> temp = getInChat();
						String user = getUser();
						if(!(temp.contains(user))){
							setChatRoomName(roomName);
							temp.add(user);
							setInChat(temp);
						}
						ChatClient c = getChatClient();
						roomNameArea.setText("");
						try{
							TypeOfMessage messageType = new TypeOfMessage("Create Chatroom: " + "[" + user + "] " + roomName);
							c.send(messageType);
							c.receive();
						}catch(JMSException ex){
							ex.printStackTrace();
						}
					}
					
				}
		);
	}
	
	public void joinChatroomButton(JButton join, final JComboBox listOfChats){
		join.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						if(!(listOfChats.getSelectedItem().equals("None"))){
							String roomName = (String) listOfChats.getSelectedItem();
							roomName = roomName.trim();
							Set<String> temp = getInChat();
							String user = getUser();
							if(!(temp.contains(user))){
								setChatRoomName(roomName);
								temp.add(user);
								setInChat(temp);
							}
							ChatClient c = getChatClient();
						
							try{
								TypeOfMessage messageType = new TypeOfMessage("Create Chatroom: " + "[" + user + "] " + roomName);
								c.send(messageType);
								c.receive();
								listOfChats.setSelectedIndex(0);
							}catch(JMSException ex){
								ex.printStackTrace();
							}
						}
					}
					
				}
		);
	}
	
	public void chatRoomBroadcast(JButton send,  final JTextArea messageText){
		send.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						String msg = messageText.getText();
						String user = getUser();
						String roomName = getChatRoomName();
						ChatClient c = getChatClient();
						messageText.setText("");
						try {
							TypeOfMessage messageType = new TypeOfMessage("Chatroom Broadcast: " + "[" + user + "]" + roomName + "-" + msg);
							c.send(messageType);
							c.receive();
						} catch (JMSException ex) {
							ex.printStackTrace();
						}
					}
				}
		);
	}
	
	public void chatRoomCloseButton(JFrame f, final Server s){
		f.addWindowListener(
				new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						ChatClient c = getChatClient();
						String user = getUser();
						HashMap<String, JTextArea> temp = s.getChatTextAreaCorrespondingToUser();
						Set<String> chat = getInChat();
						temp.remove(user);
						chat.remove(user);
						s.setChatTextAreaCorrespondingToUser(temp);
						TypeOfMessage messageType = new TypeOfMessage("Quit Chatroom: "  + user );
						try {
							c.send(messageType);
						} catch (JMSException e1) {
							e1.printStackTrace();
						}
					}
				});
	}
	
	public void closingPrivateChat(final JFrame f, final Server s){
		f.addWindowListener(
				new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						ChatClient c = getChatClient();
						String receiver = "";
						String closer = getUser();
						String sender = closer;
						HashMap<JFrame, String> map = s.getprivateFrameCorrespondingToUser();
						String str = map.get(f);
						String [] usersInPrivateMessage  = str.split(":");
						map.remove(f);
						s.setprivateFrameCorrespondingToUser(map);
						for(int i = 0; i < usersInPrivateMessage.length; i++){
							if(!(usersInPrivateMessage[i].equals(sender))){
								receiver = usersInPrivateMessage[i];
							}
						}
						receiver = receiver.trim();
						TypeOfMessage messageType = new TypeOfMessage("Close Private: "  + "[" + closer + "]" + "{" + sender + ":" + receiver + ">");
						try {
							c.send(messageType);
						} catch (JMSException e1) {
							e1.printStackTrace();
						}
					}
				}
				);
	}
	
	public void setInChat(Set<String> temp){
		this.inChatRoom = temp;
	}
	
	public Set<String> getInChat(){
		return this.inChatRoom;
	}
	
	public ChatClient getChatClient(){
		return this.cl;
	}
	
	public String getUser(){
		return this.username;
	}
	
	public void setChatClient(ChatClient cl){
		this.cl = cl;
	}
	
	public void setUser(String username){
		this.username = username;
		
	}
	
	public void setPrivateReceiver(String pR){
		this.privateReceiver = pR;
	}
	
	public String getPrivateReceiver(){
		return this.privateReceiver;
	}

	public String getChatRoomName() {
		return chatRoomName;
	}

	public void setChatRoomName(String chatRoomName) {
		this.chatRoomName = chatRoomName;
	}

}
