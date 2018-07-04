package networking;
import java.io.Serializable;

import core.Status;

@SuppressWarnings("serial")
public class Message implements Serializable{

	MessageType type;
	Object arg;
	Status status;
	int turn;
	
	public Message(MessageType type,Object arg, Status status, int turn) {
		this.type = type;
		this.arg = arg;
		this.status = status;
		this.turn = turn;
	}

	public Message(MessageType type,Status status, int turn)
	{
		this(type,null,status,turn);
	}
	public Message(MessageType type,Object arg)
	{
		this(type,arg,null,0);
	}
	public MessageType getType()
	{
		return type;
	}
	public Object getArgument()
	{
		return arg;
	}

	public Status getStatus()
	{
		return status;
	}
	public int getTurn()
	{
		return turn;
	}
}
