package GuiFrame;

import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import ChatSignInServer.Server;

public class privateMessageGui extends JFrame {
	private static JTextArea MessageArea; 
	private static JTextArea TypeMessageArea;
	private static JTextArea usersPrivate;
	private static Server s = new Server();
	 JFrame frame;
	
	public void createFrame(String msg){
		Frame f = new Frame();
		f.buildFrame("Private", false);
		
		msg = msg.substring(1, msg.length()).trim();
		
		this.MessageArea = f.getDisplayMessageArea();
		this.usersPrivate = f.getUserOnlineArea();
		this.TypeMessageArea = f.getTypeMessageArea();
		displayMessage(msg);

		JButton send = f.getButton();
		ButtonActions actionOnPush = new ButtonActions();
		this.frame = f.getFrame();
		actionOnPush.privateSend(send, this.TypeMessageArea, this.frame, this.s);
		actionOnPush.closingPrivateChat(frame, s);
	}

	public void displayMessage(String msg){
		int lessThan = msg.indexOf("<");
		int greaterThan = msg.indexOf(">");
		String userToReceiver = msg.substring(lessThan + 1, greaterThan).trim();
		HashMap<String, JTextArea> messageTextArea = this.s.getTextAreaCorrespondingToUser();
		msg = msg.substring(1,lessThan).trim();
		
		if(userToReceiver.equals("none")){
			this.MessageArea.append(msg + "\n");
		}
		else{
			this.MessageArea = messageTextArea.get(userToReceiver);
			this.MessageArea.append(msg + "\n");
			this.s.putIntoTextAreaCorrespondingToUser(userToReceiver, this.MessageArea);
		}
	}
	
	public void displayPrivateUsers(String msg){
		this.usersPrivate.setText(" ");
		String[]users = msg.split(":");
		for(int i = 0; i < users.length; i ++){
			this.usersPrivate.append(users[i] + "\n");
		}
		String user1 = users[0];
		String user2 = users[1];
		s.putIntoPrivateFrameCorrespondingToUser(getJFrame(), user1+":"+user2);
		s.putIntoTextAreaCorrespondingToUser(user1 + ":" + user2, getMessageArea());
	}
	
	public JFrame getJFrame(){
		return this.frame;
	}
	
	public JTextArea getMessageArea(){
		return this.MessageArea;
	}
}
