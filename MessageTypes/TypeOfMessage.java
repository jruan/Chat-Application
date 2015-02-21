package MessageTypes;

import java.io.Serializable;

public class TypeOfMessage implements Serializable {
	private String messageType;
	
	public TypeOfMessage(String msg){
		this.messageType = msg;
	}
	
	public String getMessage(){
		return this.messageType;
	}
}
