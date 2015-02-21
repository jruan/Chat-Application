package ChatSignInServer;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class serverFrame extends JFrame {
	
	public serverFrame(){
		super("Server Connection");
		setSize(250, 100);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		final JPanel p = new JPanel();
		
		JButton connect = new JButton("Connect");
		p.add(connect);
		
		add(p);
		
		connect.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						serverConnection s = new serverConnection();
						try {
							if(s.makeConnection()){
								JLabel connected = new JLabel("Server has Connected....................");
								p.add(connected);
							}
							
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
				
				);
	}
	
	public static void main(String[]args){
		serverFrame s = new serverFrame();
		s.setVisible(true);
	}
}
