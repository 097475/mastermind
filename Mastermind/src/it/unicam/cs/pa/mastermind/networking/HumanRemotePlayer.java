package networking;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import core.Code;
import core.GuessRow;
import core.Outcome;
import core.Player;


public class HumanRemotePlayer implements Player{

	Player player;
	Socket requestSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    Message message;
	public HumanRemotePlayer(Player player) {
		this.player = player;
		
		try {
			requestSocket = new Socket("localhost", 8080);
			out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());
            System.out.println("Connected to localhost in port 8080");
            this.run();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	void sendMessage(Message msg)
    {
        try{
            out.writeObject(msg);
            out.flush();
            System.out.println("client>" + msg);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
	@SuppressWarnings("unchecked")
	private void run()
	{
		 do{
             try{
                 message = (Message)in.readObject();
                // GUIMatch.setMatch(message.getStatus(),message.getTurn());
                 switch(message.getType())
                 {
                 case CODE: sendMessage(new Message(MessageType.CODE,this.getCode())); break;
                 case GUESS: sendMessage(new Message(MessageType.GUESS,this.getGuess((List<GuessRow>)message.getArgument()))); break;
                 case OUTCOME: sendMessage(new Message(MessageType.OUTCOME,this.checkLastRow((Code)message.getArgument()))); break;
                 case ENDROUND: this.endRound();break;
                 case ENDMATCH: this.endMatch();break;
                 case INFO: break;//set the match
                 default:break;
                 }
             }
             catch(ClassNotFoundException classNot){
                 System.err.println("data received in unknown format");
             } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }while(message.getType()!=MessageType.ENDMATCH);
	}

	@Override
	public Code getGuess(List<GuessRow> visibleBoard) {
		// TODO Auto-generated method stub
		return player.getGuess(visibleBoard);
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return player.getCode();
	}

	@Override
	public Outcome checkLastRow(Code guess) {
		// TODO Auto-generated method stub
		return player.checkLastRow(guess);
	}

	@Override
	public void endRound() {
		// TODO Auto-generated method stub
		player.endRound();
	}

	@Override
	public void endMatch() {
		// TODO Auto-generated method stub
		player.endMatch();
	}


}
