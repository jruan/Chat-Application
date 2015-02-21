package CSE110ChatSystem.ChatSignInClient;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;

import GuiFrame.clientGui;
import MessageTypes.TypeOfMessage;


public class ChatClient implements MessageListener {

	private MessageProducer producer;
	private MessageConsumer consumer;
	private Session session;
	private String userName;
	private static clientGui gui;
	private static ActiveMQConnection connection;
	
	public ChatClient(MessageProducer producer, MessageConsumer consumer,Session session, String user, ActiveMQConnection connection) {
		this.producer = producer;
		this.consumer = consumer;
		this.session = session;
		this.userName = user;
		this.connection = connection;
	}


	public void send(TypeOfMessage messageObject) throws JMSException {
		producer.send(this.session.createObjectMessage(messageObject));
	}
	
	public void receive() throws JMSException {
		consumer.setMessageListener(this);
	}
	
	public void setUserName(String u){
		this.userName = u;
	}
	
	public String getUserName(){
		return this.userName;
	}
		
	public void onMessage(Message message){
		TextMessage msg = null; 
   	    try { 
        if (message instanceof TextMessage) { 
            msg = (TextMessage) message; 
          
            if(msg.getText().charAt(0) == '&'){
            	System.out.println("Connection has started.");
            	gui = new clientGui(msg.getText());
            }
            
            else if(msg.getText().charAt(0) == '-' && msg.getText().charAt(1) == '&'){
            	gui = new clientGui(msg.getText());
            }
            
            else if(msg.getText().equals("Wrong Password")){
            	gui = new clientGui("#W");
            }
            
            else if(msg.getText().equals("Sign Out")){
            	this.connection.close();
            	System.exit(1);
            }	
            
            else if(msg.getText().charAt(0) == '.'){
            	gui = new clientGui(msg.getText());
            }
            
            else if(msg.getText().charAt(0) == '{'){
            	gui = new clientGui(msg.getText());
            }
            
            else if(msg.getText().charAt(0) == '/'){
            	gui = new clientGui(msg.getText());
            }
            
            else if(msg.getText().charAt(0) == '~'){
            	gui = new clientGui(msg.getText());
            }
                        
            else{
            	gui = new clientGui(msg.getText());
            }
            
        } else { 
             System.out.println("Message is not a " + "TextMessage"); 
        	} 
   	    } catch (JMSException e) { 
   	    	System.out.println("JMSException in onMessage(): " + e.toString()); 
   	    } catch (Throwable t) { 
   	    	System.out.println("Exception in onMessage():" + t.getMessage()); 
   	    }
	}
}