package ChatSignInServer;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import MessageTypes.TypeOfMessage;

public class Server {
	private String username;
	private String password;
	private HashMap<String,String> map;
	private static Set<String> usersOnline = new HashSet<String>();
	private static sendPrivate privateSender = new sendPrivate();
	private static ChatRoom c = new ChatRoom();
	private static HashMap<JFrame, String> privateFrameCorrespondingToUser = new HashMap<JFrame, String>();
	private static HashMap<String, JTextArea> textAreaCorrespondingToUser = new HashMap<String, JTextArea>();
	private static HashMap<String, JTextArea>  chatTextAreaCorrespondingToUser = new HashMap<String, JTextArea>();
	
	public Server(){
		this.map = new HashMap<String, String>();
		loadFromDataBase();
	}

	public String receive(Serializable originalMessage) throws JMSException {	
		TypeOfMessage messageType = (TypeOfMessage)originalMessage;
		
		String msg = messageType.getMessage();
		int i = msg.indexOf(":");
		String type = msg.substring(0, i);
		type = type.trim();
		String message = msg.substring(i+1, msg.length());
		message = message.trim();
		pickATypeToHandle(type, message);
		return message;
	}
	
	
	public void pickATypeToHandle(String type, String message){
		switch(type){
			case "Sign On" : handleLogIn(message);
							break;
			case "Sign Out" : userSignout(message);
							  c.removeFromChat(message);
						     break;
			case "Broadcast" : handleRegularMessage(message);
							break;
			case "New Private" : handleNewPrivateMessate(message);
								break;
			case "Existing Private" : handlePrivateMessage(message);
									break;
			case "Close Private" : privateSender.handleClosing(message);
									break;
			case "Create Chatroom":	c.createChatroom(message);
								break;
			case "Chatroom Broadcast" : int x = message.indexOf("[");
										int y = message.indexOf("]");
										int z = message.indexOf("-");
										String username = message.substring(x+ 1, y);
										String chatroomname= message.substring(y + 1, z);
										String msg = message.substring(z + 1, message.length());
										username = username.trim();
										chatroomname = chatroomname.trim();
										msg = msg.trim();
										msg = "~C[" + username + " -chat] " + msg;
										c.broadcast(chatroomname, msg);
										break;
			case "Quit Chatroom" :  c.removeFromChat(message);
									break;
			default:				break;
		}
	}
	
	
	public void handleLogIn(final String originalMessage){
		registerUser(originalMessage);
		loadFromDataBase();
		
		if(!verifyPassword(this.username, this.password)){
	    	sendMessage(this.username, "Wrong Password");
			return;
		}
		
		 sendMessage( this.username, "&true");
		 this.usersOnline.add(this.username);
		 listUsers();
		 Set<String> chatRooms = c.getChatRooms();
		 if(chatRooms.size()!= 0){
		    for(String chatrooms: chatRooms){
		    	c.addChatToBox(chatrooms, this.username);
		   	}
		 }
	}
	
	public void listUsers(){
		int numberOfUser = 0;
		for(String u: this.usersOnline){
			numberOfUser = 0;
			for(String UsersOnline: this.usersOnline){
				numberOfUser++;
				if(numberOfUser == this.usersOnline.size()){
					if(!(UsersOnline.equals(u)))
						sendMessage(u, "-&BUsers Online: " + UsersOnline + "_last");
					else if(UsersOnline.equals(u))
						sendMessage(u, "-&BUsers Online: _equalsLast" );
				}
				else{
					if(!(UsersOnline.equals(u))){
						sendMessage(u, "-&BUsers Online: " + UsersOnline + "_not");
					}
				}
			}
		}
	}
	
	public Set<String> getUsersOnline(){
		return this.usersOnline;
	}

	/**
	 * @return
	 */
	public boolean verifyPassword(String user, String password) {
		return this.map.get(user).equals(password);
	}
	
	public void registerUser(final String originalMessage) {
		int index = originalMessage.indexOf('^');

		this.username = originalMessage.substring(0, index);
		this.username = this.username.trim();
		this.password = originalMessage.substring(index+1, originalMessage.length());
		this.password = this.password.trim();
		if(!(isInMap(this.username))){
			addIntoMap(this.username, this.password);	
		}
	}
	
	public void handleNewPrivateMessate(String originalMessage){
		privateSender.sendFirstPrivateMessage(originalMessage);
	}
	
	/**
	 * 
	 * @param originalMessage
	 * handles private messages
	 */
	
	public void handlePrivateMessage(String originalMessage){
		privateSender.sendContinuingPrivateMessage(originalMessage);
	}

	/**
	 * @param originalMessage
	 */
	public void handleRegularMessage(final String originalMessage) {
		int x = originalMessage.indexOf("]");
		String sender =originalMessage.substring(1,x);

		for(String user: this.usersOnline){
			sendMessage(user, "~B" + originalMessage);
		}
		
	}
	
	public void sendMessage( String username, String text )
	{
		final String finalText = text;
		
		MessageCreator messageCreator = new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage( finalText );
			}
        }; 
        
        AnnotationConfigApplicationContext context = 
		          new AnnotationConfigApplicationContext(serverConnection.class);
        
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        jmsTemplate.send( username, messageCreator);
	}
		
	
	public void userSignout(String user){
		this.usersOnline.remove(user);
		sendMessage(user, "Sign Out");
		
		listUsers();
	}
	
	public boolean isInMap(String user){
		return this.map.containsKey(user);
	}
	public void addIntoMap(String u,String p){
		if(u == null || p == null) {
			return;
		}
		try {
			PrintWriter out = new PrintWriter(new FileWriter("Database.txt", true)); 
		    out.println(u);
		    out.println(p);
		    out.close();
		} catch (IOException e) {
		}
		this.map.put(u, p);
	}
	
	public void loadFromDataBase(){
		String u = "";
		String pass = "";
		try {
		BufferedReader br = new BufferedReader(new FileReader("Database.txt"));
		
        while ((u = br.readLine()) != null && (pass = br.readLine()) != null) {
	     this.map.put(u, pass);
		}
        br.close();
		}
		catch(FileNotFoundException e) {}
		catch(IOException e) {}
	}
	
	/*
	 * This method is for testing purpose only
	 */
	
	public Set<String> getUserOnline(){
		return this.usersOnline;
	}
	
	public void putIntoPrivateFrameCorrespondingToUser(JFrame f, String s){
		this.privateFrameCorrespondingToUser.put(f, s);
	}
	public HashMap<JFrame, String> getprivateFrameCorrespondingToUser(){
		return this.privateFrameCorrespondingToUser;
	}
	
	public void putIntoTextAreaCorrespondingToUser(String s, JTextArea area){
		this.textAreaCorrespondingToUser.put(s, area);
	}
	
	public HashMap<String, JTextArea> getTextAreaCorrespondingToUser(){
		return this.textAreaCorrespondingToUser;
	}
	
	public void setTextAreaCorrespondingToUser(HashMap<String, JTextArea> map){
		this.textAreaCorrespondingToUser = map;
	}
	
	public void setprivateFrameCorrespondingToUser(HashMap<JFrame, String> map){
		this.privateFrameCorrespondingToUser = map;
	}
	public void putIntoChatTextAreaCorrespondingToUser(String user, JTextArea area){
		this.chatTextAreaCorrespondingToUser.put(user, area);
	}
	
	public HashMap<String, JTextArea> getChatTextAreaCorrespondingToUser(){
		return this.chatTextAreaCorrespondingToUser;
	}
	
	public void setChatTextAreaCorrespondingToUser(HashMap<String, JTextArea> map){
		this.chatTextAreaCorrespondingToUser = map;
	}
}
