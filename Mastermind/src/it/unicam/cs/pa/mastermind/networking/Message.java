package networking;
import java.io.Serializable;

import core.Status;

@SuppressWarnings("serial")
public class Message implements Serializable{

	MessageType type;
	Object arg;
	Status status;
	Integer turn;
	private Integer P1Score;
	private Integer P2Score;
	private Integer round;
	
	public Message(MessageType type,Object arg, Status status, Integer turn, Integer P1Score, Integer P2Score, Integer round) {
		this.type = type;
		this.arg = arg;
		this.status = status;
		this.turn = turn;
		this.P1Score=P1Score;
		this.P2Score=P2Score;
		this.round = round;
	}

	public Message(MessageType type,Object arg, Status status, Integer turn)
	{
		this(type,arg,status,turn,null,null,null);
	}
	public Message(MessageType type,Status status, Integer turn)
	{
		this(type,null,status,turn,null,null,null);
	}
	public Message(MessageType type,Object arg)
	{
		this(type,arg,null,null,null,null,null);
	}
	public Message(MessageType type,Integer P1Score, Integer P2Score, Integer round)
	{
		this(type,null,null,null,P1Score,P2Score,round);
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
	public Integer getTurn()
	{
		return turn;
	}

	public Integer getP1Score() {
		return P1Score;
	}


	public Integer getP2Score() {
		return P2Score;
	}



	public Integer getRound() {
		return round;
	}

}
