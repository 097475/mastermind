package networking;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

import core.Code;
import core.GuessRow;
import core.InfoManager;
import core.Match;
import core.Outcome;
import core.Player;
import core.Settings;
import players.HumanGUIPlayer;


@SuppressWarnings("deprecation")
public class HumanRemotePlayer extends Observable implements Player{

	Player player;
	Socket requestSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    Message message;
    private Code secretCode;
	public HumanRemotePlayer(Player player) {
		this.player = player;
		
		try {
			requestSocket = new Socket("localhost", 8096);
			out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());
            System.out.println("Connected to localhost in port 8080");
            this.addObserver(((HumanGUIPlayer) player).getGUI());
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
            System.out.println("client>" + msg.getType());
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
                 InfoManager.getInfo().updateInfo(message);
                 if(message.getArgument()!=null)
                 {
                	 this.setChanged();
             		 this.notifyObservers(message.getArgument());
                 }               
                 switch(message.getType())
                 {
                 case CODE: InfoManager.getInfo().setCode(this.getCode());
                	 sendMessage(new Message(MessageType.CODE,this.secretCode)); break;
                 case GUESS: sendMessage(new Message(MessageType.GUESS,this.getGuess((List<GuessRow>)message.getArgument()))); break;
                 case OUTCOME: InfoManager.getInfo().setGuess((Code)message.getArgument());
                	 sendMessage(new Message(MessageType.OUTCOME,this.checkLastRow((Code)message.getArgument()))); break;
                 case ENDROUND: this.endRound();break;
                 case ENDMATCH: this.endMatch();break;
                 case INFO: Settings.setGameProperties((Properties)message.getArgument()); Match.startRemoteMatch(); break;//set the match
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
		return secretCode = player.getCode();
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
