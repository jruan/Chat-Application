package GuiFrame;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import CSE110ChatSystem.ChatSignInClient.ChatClient;
import CSE110ChatSystem.ChatSignInClient.clientConnection;

public class clientGui  {

	private static JTextArea displayMessageArea;
	private static JTextArea usersAvailable;
	private static JTextArea typeMessageArea;
	private static JComboBox usersForPrivateChat = new JComboBox();
	private static privateMessageGui pMessage = new privateMessageGui();
	private static ButtonActions actionsOnButtonPush = new ButtonActions();
	private static SignOnGui sGui = new SignOnGui();
	private static JTextField password;
	private static int count = 1;
	private static chatFrame cF= new chatFrame();
	
	public clientGui(String msg){	
		
		if(msg.charAt(0) == '#'){
			sGui.signOnFrame();
			JButton signOn = sGui.getSignOnButton();
			JButton signOff = sGui.getSignOffButton();
	
			final JTextField username = sGui.getUserName();
			this.password = sGui.getPassword();	
			actionsOnButtonPush.signOn(signOn, username, password);
		}
	
		
		else if(msg.equals("&true")){
			Frame f = new Frame();
			SignOnGui sGui = new SignOnGui();
			f.buildFrame("Broadcast", true);
			cF.makeOrJoinChatroomFrame();
			JButton send = f.getButton();
			JButton signOff = sGui.getSignOffButton();
		    actionsOnButtonPush.signOff(signOff);

			this.displayMessageArea = f.getDisplayMessageArea();
			this.usersAvailable = f.getUserOnlineArea();
			this.usersForPrivateChat = f.getUsersForPrivateMessaging();
			this.typeMessageArea = f.getTypeMessageArea();
			actionsOnButtonPush.broadCastSend(send, this.typeMessageArea, this.usersForPrivateChat);	
		}
		
		else if(msg.charAt(0) == '-' && msg.charAt(1) == '&'){
			handleDisplayUser(msg);
		}
	
		else if(msg.charAt(0) == '.'){
			pMessage.createFrame(msg);
		}
		
		else if(msg.charAt(0) == '~'){
			handleDisplayMessage(msg);
		}
		
		else if(msg.charAt(0) == '{'){
			msg = msg.substring(1, msg.length());
			cF.buildChatFrame(msg);
		}
		
		else if(msg.charAt(0) == '/'){
			msg = msg.substring(1, msg.length());
			cF.addRoomToComboBox(msg);
		}
	}
	
	public void handleDisplayMessage(String msg){
		if(msg.charAt(1) == 'P'){
			msg = msg.substring(2, msg.length());
			pMessage.displayMessage(msg);
		}
			
		else if(msg.charAt(1) == 'B'){
			msg = msg.substring(2, msg.length());
			displayMessage(msg);
		}
		else if(msg.charAt(1) == 'C'){
			msg = msg.substring(2, msg.length());
			cF.displayMessage(msg);	
		}
	}
	
	public void handleDisplayUser(String msg){
		if(msg.charAt(2) == 'P'){
			msg = msg.substring(3, msg.length());
			pMessage.displayPrivateUsers(msg);
		}
		else if(msg.charAt(2) =='B'){
			displayOnline(msg);
		}
		else if(msg.charAt(2) == 'C'){
			cF.displayChatUsers(msg);	
		}
	}
	
	public void setJComboBox(JComboBox j){
		this.usersForPrivateChat = j;
	}
	
	public JComboBox getJComboBox(){
		return this.usersForPrivateChat;
	}
	
	public void displayMessage(String msg){
		this.displayMessageArea.append(msg);
		this.displayMessageArea.append("\n");
	}
	
	public void displayOnline(String msg){
		int i = msg.indexOf(":");
		int underScore = msg.indexOf("_");
		String user = msg.substring(i+1, underScore);
		user = user.trim();
		String last = msg.substring(underScore +1, msg.length()); 
		if(count == 0){
			this.usersAvailable.setText("");
			this.usersAvailable.append("Online Users\n");
			this.usersForPrivateChat.removeAllItems();
			this.usersForPrivateChat.addItem("Everyone");
			this.count = this.count + 1;
		}
		if(count == 1){
			if(last.equals("last")){
				this.usersAvailable.append(user + "\n");
				this.usersForPrivateChat.addItem(user);
				this.count = 0;
			}
			else if(last.equals("equalsLast")){
				this.count = 0;
			}
			else{
				this.usersAvailable.append(user + "\n");
				this.usersForPrivateChat.addItem(user);
			}
		}
	}
	public static void main(String[]args){
		new clientGui("#sign on");
	}
}
