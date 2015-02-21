package ChatSignInServer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.swing.JTextArea;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class sendPrivate {
	private static List<String> availablePrivateChats;
	private static HashMap<String, String> availablePrivateChatsClosed;
	private static Server s = new Server();
	
	public sendPrivate(){
		this.availablePrivateChats = new LinkedList<String>();
		this.availablePrivateChatsClosed = new HashMap<String, String>();
	}
	
	public void sendFirstPrivateMessage(String msg){
		String messageReceiver = "";
		String sender = "";
		int closeBrackets = msg.indexOf("]");
		int openBrackets = msg.indexOf("[");
		int parantheses = msg.lastIndexOf(")");
		int curlyBracs = msg.lastIndexOf("{");
		
		sender = msg.substring(openBrackets + 1, closeBrackets);
		messageReceiver = msg.substring(curlyBracs + 1, parantheses);
		String senderToReceiver = sender + ":" + messageReceiver;
		String receiverToSender = messageReceiver + ":" + sender;
		String message = ".[" + sender + " -private] " + msg.substring(closeBrackets + 1, curlyBracs) + "<none>";
		String usersInPrivateChat = "-&P" + sender + ":" + messageReceiver;
		String otherWayAround = "-&P" + messageReceiver + ":" + sender;
		String messageForSender = "~P[" + sender +"- private] " + msg.substring(closeBrackets + 1, curlyBracs) + "<" + sender +":"+ messageReceiver + ">";
		String messageForReceiver = "~P[" + sender +"- private] " + msg.substring(closeBrackets + 1, curlyBracs) + "<" + messageReceiver+":"+ sender + ">";

		if(!(checkIfChatExist(senderToReceiver))){
			addChatToList(senderToReceiver);
			addChatToList(receiverToSender);
			this.availablePrivateChatsClosed.put(senderToReceiver, "none");
			this.availablePrivateChatsClosed.put(receiverToSender, "none");
			jmsSend(message,messageReceiver);
			jmsSend(message, sender);
			jmsSend(usersInPrivateChat, sender);
			jmsSend(otherWayAround, messageReceiver);
		}
		
		else if(checkIfChatExist(senderToReceiver)){
			if(this.availablePrivateChatsClosed.get(senderToReceiver).equals("both")){
				jmsSend(message, sender);
				jmsSend(message,messageReceiver);
				jmsSend(otherWayAround, messageReceiver);
				jmsSend(usersInPrivateChat, sender);
				this.availablePrivateChatsClosed.put(senderToReceiver, "none");
				this.availablePrivateChatsClosed.put(receiverToSender, "none");
			}	
			else{
				handleMessageSendAfterClosingJFrame(senderToReceiver,  message,  sender,  usersInPrivateChat,  messageReceiver,  otherWayAround,  messageForSender,  messageForReceiver,receiverToSender);
			}
		}
	}
	
	public void sendContinuingPrivateMessage(String msg){
		String messageReceiver = "";
		String sender = "";
		int closeBrackets = msg.indexOf("]");
		int openBrackets = msg.indexOf("[");
		int parantheses = msg.lastIndexOf(")");
		int curlyBracs = msg.lastIndexOf("{");
		
		sender = msg.substring(openBrackets + 1, closeBrackets).trim();
		messageReceiver = msg.substring(curlyBracs + 1, parantheses).trim();
		String messageForSender = "~P[" + sender +"- private] " + msg.substring(closeBrackets + 1, curlyBracs) + "<" + sender +":"+ messageReceiver + ">";
		messageForSender = messageForSender.trim();
		String messageForReceiver = "~P[" + sender +"- private] " + msg.substring(closeBrackets + 1, curlyBracs) + "<" + messageReceiver+":"+ sender + ">";
		String message = ".[" + sender + " -private] " + msg.substring(closeBrackets + 1, curlyBracs) + "<none>";

		if(this.availablePrivateChatsClosed.get(sender + ":" + messageReceiver).equals("none")){
			jmsSend(messageForReceiver, messageReceiver);
			jmsSend(messageForSender, sender);
		}
		else{
			handleMessageSendAfterClosingJFrame(sender +":"+ messageReceiver,  message,  sender,  "-&P" + sender + ":" + messageReceiver,  messageReceiver,  "-&P" + messageReceiver + ":" + sender,  messageForSender,  messageForReceiver, messageReceiver +":" + sender);
		}
	}
	
	public void handleClosing(String msg){ 
		int closeBrackets = msg.indexOf("]");
		int openBrackets = msg.indexOf("[");
		int curlyBracs = msg.indexOf("{");
		int greater = msg.indexOf(">");
		int colon = msg.indexOf(":");
		String sender = msg.substring(curlyBracs + 1, colon).trim();
		String receiver = msg.substring(colon + 1, greater).trim();
		String ReceiverToSender = receiver + ":" + sender;
		String senderToReceiver = msg.substring(curlyBracs + 1, greater).trim();
		String closer = msg.substring(openBrackets+1, closeBrackets).trim();
		HashMap<String, JTextArea> temp = s.getTextAreaCorrespondingToUser();
		temp.remove(closer+":"+receiver);
		s.setTextAreaCorrespondingToUser(temp);
		if(this.availablePrivateChatsClosed.containsKey(senderToReceiver) && this.availablePrivateChatsClosed.containsKey(ReceiverToSender)){
			if(this.availablePrivateChatsClosed.get(senderToReceiver).equals("none") && this.availablePrivateChatsClosed.get(ReceiverToSender).equals("none")){
				this.availablePrivateChatsClosed.put(senderToReceiver, closer);
				this.availablePrivateChatsClosed.put(ReceiverToSender, closer);
			}
			else if(!(this.availablePrivateChatsClosed.get(senderToReceiver).equals("none"))){
				this.availablePrivateChatsClosed.put(senderToReceiver, "both");
				this.availablePrivateChatsClosed.put(ReceiverToSender, "both");
			}
		}
	}
	
	public void handleMessageSendAfterClosingJFrame(String senderToReceiver, String message, String sender, String usersInPrivateChat, String receiver, String otherWayAround, String messageForSender, String messageForReceiver, String receiverToSender){
		if(this.availablePrivateChatsClosed.get(senderToReceiver).equals(sender)){
			jmsSend(message, sender);
			jmsSend(usersInPrivateChat, sender);
			jmsSend(messageForReceiver, receiver);
			this.availablePrivateChatsClosed.put(senderToReceiver, "none");
			this.availablePrivateChatsClosed.put(receiverToSender, "none");

		}
		
		else if(this.availablePrivateChatsClosed.get(senderToReceiver).equals(receiver)){
			jmsSend(message, receiver);
			jmsSend(otherWayAround, receiver);
			jmsSend(messageForSender, sender);
			this.availablePrivateChatsClosed.put(senderToReceiver, "none");
			this.availablePrivateChatsClosed.put(receiverToSender, "none");

		}
	}
	
	public void jmsSend(final String msg, String MessageReceiver){
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(serverConnection.class);
	
		MessageCreator messageCreator = new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(msg);
			}
		};
		
		JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
		jmsTemplate.send(MessageReceiver, messageCreator);
	}
	
	public void addChatToList(String senderToReceiver){
		this.availablePrivateChats.add(senderToReceiver);
	}
	
	public boolean checkIfChatExist(String senderToReceiver){
		return this.availablePrivateChats.contains(senderToReceiver);
	}
	
}
