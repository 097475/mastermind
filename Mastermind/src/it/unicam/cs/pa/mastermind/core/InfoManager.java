package core;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import networking.Message;

public class InfoManager {

	private static InfoManager info;
	private boolean isClient = Settings.getGameProperties().getProperty("Match") == "Client" ? true : false;
	private int serverTurn = 0,serverRound = 0,serverP1score = 0,serverP2score = 0;
	private final int codeSize;
	private Status serverStatus;
	private Code currentSecretCode,currentGuess;
	
	private InfoManager()
	{
		this.codeSize = Integer.parseInt(Settings.getGameProperties().getProperty("CodeSize"));
	}

	public int getTurn()
	{
		return isClient ? this.serverTurn : Match.getMatch().getTurn();
	}
	
	public Status getStatus()
	{
		return isClient ? this.serverStatus : Match.getMatch().getStatus();
	}
	
	public Code parseCodeString(String str)
	{
		return Match.getMatch().parseCodeString(str);
	}
	
	/*public Code parseCodeString(String str) throws NoSuchElementException,IllegalArgumentException
	{
		List<CodePeg> tmpCode = new ArrayList<>();
		try(Scanner sc = new Scanner(str))
		{
			while(sc.hasNext())
			{
				String tmp = sc.next();
				tmpCode.add(this.pegSet.get(tmp));
			}			
		}
		
		Code c = new Code(tmpCode);	
		return this.verifyCode(c)==true ? c : null;
	}
		public boolean verifyCode(Code code) throws NoSuchElementException,IllegalArgumentException
	{			
		if(!code.get().stream().allMatch(this.pegSet::containsValue)) throw new NoSuchElementException("Empty spaces not allowed!");
		if(code.get().size()!=this.codeSize) throw new IllegalArgumentException("Code size ("+code.get().size()+" different from expected ("+this.codeSize+")");
		return true;
	
	}*/
	public Outcome parseOutcomeString(String str)
	{
		Outcome o = new Outcome();
		try(Scanner sc = new Scanner(str))
		{
			while(sc.hasNext())
			{
				String tmp = sc.next();
				switch(tmp)
				{
					case "WHITE": o.addWhitePeg(); break;
					case "BLACK": o.addBlackPeg(); break;
					case "EMPTYKEYSLOT":break;
					default: throw new NoSuchElementException("Peg "+tmp+" isn't a KeyPeg");
				}
			}			
		}
		return this.verifyOutcome(o)==true ? o : null;
	}
	
	public boolean verifyOutcome(Outcome o) throws IllegalArgumentException
	{
		if(o.getBlackPegs()+o.getWhitePegs()>this.codeSize) throw new IllegalArgumentException("Too many pegs");
		if(!o.equals(Match.check(this.currentSecretCode, this.currentGuess))) throw new IllegalArgumentException("Invalid response, expected :"+Match.check(currentSecretCode, this.currentGuess).toString());
		return true;
	}
	public void setCode(Code code)
	{
		this.currentSecretCode = code;
	}
	public void setGuess(Code code)
	{
		this.currentGuess = code;
	}
	public static InfoManager getInfo()
	{
		return info==null ? info = new InfoManager() : info;
	}
	public void updateInfo(int turn, Status status)
	{
		
		this.serverStatus = status;
	}

	public int getP1Score() {
		// TODO Auto-generated method stub
		return isClient ? this.serverP1score : Match.getMatch().getP1Score();
	}

	public int getP2Score() {
		// TODO Auto-generated method stub
		return isClient ? this.serverP2score : Match.getMatch().getP2Score();
	}

	public int getRound() {
		// TODO Auto-generated method stub
		return isClient ? this.serverRound : Match.getMatch().getRound();
	}

	public void updateInfo(Message message) {
		// TODO Auto-generated method stub
		if(message.getTurn()!=null) this.serverTurn = message.getTurn();
		if(message.getStatus()!=null) this.serverStatus = message.getStatus();
		if(message.getRound()!=null) this.serverRound = message.getRound();
		if(message.getP1Score()!=null) this.serverP1score = message.getP1Score();
		if(message.getP2Score()!=null) this.serverP2score = message.getP2Score();
	}
	
	
}
