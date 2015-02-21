package CSE110ChatSystem.ChatSignInClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
//import org.apache.activemq.broker.region.Queue;



public class clientConnection {
	private ChatClient c;
	private String u;
	
/*
	 * This inner class is used to make sure we clean up when the client closes
	 */
	static private class CloseHook extends Thread {
		ActiveMQConnection connection;
		private CloseHook(ActiveMQConnection connection) {
			this.connection = connection;
		}
		
		public static Thread registerCloseHook(ActiveMQConnection connection) {
			Thread ret = new CloseHook(connection);
			Runtime.getRuntime().addShutdownHook(ret);
			return ret;
		}
		
		public void run() {
			try {
				System.out.println("Closing ActiveMQ connection");
				connection.close();
			} catch (JMSException e) {
				/* 
				 * This means that the connection was already closed or got 
				 * some error while closing. Given that we are closing the
				 * client we can safely ignore this.
				*/
			}
		}
	}


	/*
	 * This method wires the client class to the messaging platform
	 * Notice that ChatClient does not depend on ActiveMQ (the concrete 
	 * communication platform we use) but just in the standard JMS interface.
*/

	//make connection from client
	public ChatClient makeConnection(String username) throws JMSException, URISyntaxException{
		ActiveMQConnection connection = ActiveMQConnection.makeConnection(Constant.ACTIVEMQ_URL);
		connection.start();
		String user = username;
		CloseHook.registerCloseHook(connection);
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue destQueue = session.createQueue(Constant.QUEUENAME);
		MessageProducer producer = session.createProducer(destQueue);
		Queue dest2 = session.createQueue(user);
		MessageConsumer consumer = session.createConsumer(dest2);
		
		return new ChatClient(producer, consumer, session, user, connection);
	}
	
}


