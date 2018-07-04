package networking;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import core.Code;
import core.GuessRow;
import core.Match;
import core.Outcome;

public class Server {
	 ServerSocket providerSocket;
	    Socket connection[];
	    ObjectOutputStream out[];
	    ObjectInputStream in[];
	    Message message;
	    int connections = 0;

	public Server() {
		try {
			providerSocket = new ServerSocket(8080);
			 connection = new Socket[2];
			 out = new ObjectOutputStream[2];
			 in = new ObjectInputStream[2];
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void listen()
	{
		try {
		System.out.println("Waiting for connection");
		while(connections<2)
		{
			connection[connections] = providerSocket.accept();
			 out[connections] = new ObjectOutputStream(connection[connections].getOutputStream());
	         out[connections].flush();
	         in[connections] = new ObjectInputStream(connection[connections].getInputStream());
	         System.out.println("Connection received from " + connection[connections].getInetAddress().getHostName());
	         connections++;
		}
			

		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	void sendMessage(Message msg, int id)
    {
        try{
            out[id].writeObject(msg);
            out[id].flush();
            System.out.println("server>" + msg);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }


	public Code getCode(int id) {
		// TODO Auto-generated method stub
		sendMessage(new Message(MessageType.CODE,Match.getMatch().getStatus(),Match.getMatch().getTurn()),id);
		return (Code)waitMessage(id).getArgument();
	
	}
	public Code getGuess(List<GuessRow> visibleBoard, int id) {
		// TODO Auto-generated method stub
		sendMessage(new Message(MessageType.GUESS,visibleBoard,Match.getMatch().getStatus(),Match.getMatch().getTurn()),id);
		return (Code)waitMessage(id).getArgument();
	}

	public Outcome getResponse(Code guess, int id)
	{
		sendMessage(new Message(MessageType.OUTCOME,guess,Match.getMatch().getStatus(),Match.getMatch().getTurn()),id);
		return (Outcome)waitMessage(id).getArgument();
		
	}
	

	private Message waitMessage(int id)
	{
		 try {
			message = (Message)in[id].readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         return message;
	}
	
}
