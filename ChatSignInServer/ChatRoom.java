package ChatSignInServer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class ChatRoom {
	private static HashMap<String, Set<String>> chatroom;
	private static HashMap<String, String> userAlreadyInChat;
	private static String newChatter;
	private static Server s = new Server();
	
	public ChatRoom(){
		this.chatroom = new HashMap<String,Set<String>>();
		this.userAlreadyInChat = new HashMap<String, String>();
	}
	
	public void createChatroom(String message){
		int k = message.indexOf("[");
		int l = message.indexOf("]");
		String user = message.substring(k+ 1, l);
		String roomName = message.substring(l + 1, message.length());
		user = user.trim();
		roomName = roomName.trim();
		if(!(chatRoomExist(roomName))){
			Set<String> userSet = new HashSet<String>();
			if(!(checkIfAlreadyInAChat(user))){
				userSet.add(user);
				this.chatroom.put(roomName, userSet);
				this.userAlreadyInChat.put(user, roomName);
				String msg = "{Welcome to " + roomName + " room. *" + user ;
				jmsSend(user, msg);
				listUsersInChat(roomName);
				addChatToBox(roomName, "none");
			}
			else if(checkIfAlreadyInAChat(user)){
				String msg = "~CAlready in a chatroom, can not join another one. *" + user;
				jmsSend(user, msg);
			}
		}		
		else if(chatRoomExist(roomName)){
			if(checkIfAlreadyInAChat(user)){
				String msg = "~CAlready in a chatroom, can not join another one. *" + user;
				jmsSend(user, msg);
			}
			else if(!(checkIfAlreadyInAChat(user))){
				this.userAlreadyInChat.put(user, roomName);
				addUserIntoRoom(roomName, user);
			}
		}
	}
	
	public void addUserIntoRoom(final String nameOfChat, String user){
		if(chatRoomExist(nameOfChat)){
			Set<String> temp = chatroom.get(nameOfChat);
			temp.add(user);
			this.newChatter = user;
			this.chatroom.put(nameOfChat, temp);
			String msg = "{Welcome to " + nameOfChat + " room.*" + user;
			jmsSend(user, msg);	
			listUsersInChat(nameOfChat);
		}
	}
	
	public boolean chatRoomExist(String nameOfRoom){
		return this.chatroom.containsKey(nameOfRoom);
	}
	
	public boolean checkIfAlreadyInAChat(String user){		
		if(this.userAlreadyInChat.containsKey(user)){
			return true;
		}
		
		return false;
	}
	
	public void broadcast(String nameOfRoom, final String msg){
		Set<String> temp = chatroom.get(nameOfRoom);
		
		for(String user : temp){
			jmsSend(user, msg + "*" + user);
		}
	}
	
	public void addChatToBox(String chatroomName, String newSignOn){
		Set<String> userOnline = s.getUsersOnline();
		if(newSignOn.equals("none")){
			for(String user: userOnline){
				jmsSend(user, "/" + chatroomName);
			}
		}
		else{
			jmsSend(newSignOn, "/" + chatroomName);
		}
	}
	
	public void listChat(String user){
		Set<String> chatRooms = this.chatroom.keySet();
		if(chatRooms.size() == 0){
			jmsSend(user, "Chatrooms available: none");
		}
		for(final String chat: chatRooms){
			String msg = "Chatrooms available: " + chat;
			jmsSend(user, msg);
		}
	}
	
	public void listUsersInChat(String chatRoomName){
		Set<String> usersInChat = this.chatroom.get(chatRoomName);
		int numberOfUser = 0;
		for(String u: usersInChat){
			numberOfUser = 0;
			for(String UsersOnline:usersInChat){
				numberOfUser++;
				if(numberOfUser == usersInChat.size()){
					jmsSend(u, "-&CUsers Online: " + UsersOnline + "_last");
				}
				else{
					jmsSend(u, "-&CUsers Online: " + UsersOnline + "_not");
				}
			}
		}
	}
	
	public void removeFromChat(String user){
		String chatRoom = "";
		if(this.userAlreadyInChat.containsKey(user)){
			chatRoom = this.userAlreadyInChat.get(user);
		}
		Set<String> temp = this.chatroom.get(chatRoom);
		if(temp.contains(user)){
			temp.remove(user);
			this.chatroom.put(chatRoom, temp);
			this.userAlreadyInChat.remove(user);
		}	
		listUsersInChat(chatRoom);
	}
	
	public void jmsSend(String user, final String msg){
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(serverConnection.class);
	
		MessageCreator messageCreator = new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(msg);
			}
		};
		
		JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
		jmsTemplate.send(user, messageCreator);
	}
	 
	public Set<String> getUsersInChat(String chatroom){
		return this.chatroom.get(chatroom);
	}
	
	public Set<String> getChatRooms(){
		return this.chatroom.keySet();
	}

}
