package core;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;

import gui.ObserverGUI;
import players.HumanGUIPlayer;

@SuppressWarnings("deprecation")
public class Match{
	private final Properties settings;
	private final int pegs,rounds,codeSize,boardSize;
	private final boolean emptyAllowed,codeBreaker;
	private final String UI,p1,p2;
	private final PlayerFactory playerfactory;
	private final PegSetFactory pegfactory;
	private final Map<String,CodePeg> pegSet;
	private final Class<? extends CodePeg> pegType;
	private static Match match;
	private ObserverGUI g;
	private Board currentBoard;
	private Status status;
	private Code currentSecretCode,currentGuess;
	private Outcome currentOutcome;
	private Player[] players;
	private int currentRound = 0,scores[];


	
	private Match()
	{
		this.settings = Settings.getGameProperties();
		this.pegs = Integer.parseInt(settings.getProperty("pegs"));
		this.rounds = Integer.parseInt(settings.getProperty("Rounds"));
		this.codeSize = Integer.parseInt(settings.getProperty("CodeSize"));
		this.boardSize = Integer.parseInt(settings.getProperty("BoardSize"));
		this.emptyAllowed = Boolean.parseBoolean(settings.getProperty("EmptyAllowed"));
		this.codeBreaker = Boolean.parseBoolean(settings.getProperty("CodeBreaker"));
		this.pegType = new BasePegClassFactory().getClass((settings.getProperty("PegType")));
		this.pegfactory = new BasePegSetFactory(this.pegType);
		this.pegSet = pegfactory.getPegSet(this.pegs,this.emptyAllowed);
		this.playerfactory = new BasePlayerFactory();
		this.UI = settings.getProperty("UI");
		this.p1=settings.getProperty("Player1");
		this.p2=settings.getProperty("Player2");
		if(this.UI == "gui" &&  this.p1!="Human" && this.p2!="Human" ) g = new ObserverGUI(this.pegSet);		
	}
	
	public static void init()
	{	
		match = new Match();
		match.initializePlayers();
		match.runMatch();
	}
	public static void startRemoteMatch()
	{
		match = new Match();
	}
	private void initializePlayers()
	{
		this.players = new Player[2];
		this.players[codeBreaker ? 1 : 0] = playerfactory.createPlayer(this.UI, this.pegSet, this.codeSize, this.p1, this.codeBreaker);
		this.players[!codeBreaker ? 1 : 0] = playerfactory.createPlayer(this.UI, this.pegSet, this.codeSize, this.p2, !this.codeBreaker);			
		this.scores = new int[2];
		this.scores[0] = 0;
		this.scores[1] = 0;
	}
	public static Match getMatch()
	{
		if(match==null)
			throw new RuntimeException("Match hasn't been started!");
		return match;
	}
	public Status getStatus()
	{
		return this.status;
	}
	public int getTurn()
	{
		return this.currentBoard.getTurn();
	}
	public int getRound()
	{
		return this.currentRound;
	}
	public void runMatch()
	{
		do {
			this.initRound();
			do{
				this.execTurn();
			}while(!this.currentBoard.hasEnded());
			this.finalizeRound();		
		}while(currentRound<rounds);
		this.endMatch();
	}
	private void initRound()
	{
		this.status = Status.AWAITINGCODE;
		this.currentSecretCode = players[currentRound%2].getCode();
		try{
			this.verifyCode(currentSecretCode);
		}catch(Exception ex)
		{
			throw new RuntimeException("Unlawful secret code submitted!",ex);
		}
		this.currentBoard = new Board(boardSize,currentSecretCode);
		this.setObservers();
	}
	private void setObservers()
	{
		if(this.g!=null) {
			this.currentBoard.addObserver(g);
			this.g.setCode(this.currentSecretCode);
			this.g.showGUI();
		}
			
		if(this.players[0] instanceof HumanGUIPlayer) {
			this.currentBoard.addObserver(((HumanGUIPlayer)this.players[0]).getGUI());
		}
		if(this.players[1] instanceof HumanGUIPlayer) {
			this.currentBoard.addObserver(((HumanGUIPlayer)this.players[1]).getGUI());
		}
	}
	private void execTurn()
	{
		this.status = Status.AWAITINGGUESS;
		this.getPlayerGuess();
		this.status = Status.AWAITINGRESPONSE;
		this.getPlayerOutcome();
		this.currentBoard.nextTurn();
	}
	private void getPlayerGuess()
	{
		this.currentGuess = this.players[(this.currentRound+1)%2].getGuess(this.currentBoard.getVisibleBoard());
		try{
			this.verifyCode(this.currentGuess);
		}catch(Exception ex)
		{
			throw new RuntimeException("Unlawful guess submitted!",ex);
		}
		this.currentBoard.setGuess(this.currentGuess);
	}
	private void getPlayerOutcome()
	{
		this.currentOutcome = players[currentRound%2].checkLastRow(this.currentGuess);
		try{
			this.verifyOutcome(this.currentOutcome);
		}catch(Exception ex)
		{
			throw new RuntimeException("Unlawful response submitted!",ex);
		}
		this.currentBoard.setResponse(this.currentOutcome);
	}
	private void finalizeRound()
	{
		this.scores[this.currentRound%2]+=this.currentBoard.getTurn() + (this.currentBoard.getLastRow().getResponse().equals(new Outcome(this.codeSize,0)) ? 0 : 1);
		this.players[(this.currentRound+1)%2].endRound();
		this.players[this.currentRound%2].endRound();
		if(this.g!=null) this.g.endRound();
		else if (this.p1!="Human"&& this.p2!="Human") this.printScores();
		this.currentRound++;
	}
	
	private void endMatch()
	{
		if(this.g!=null)
			this.g.endMatch();
		this.players[0].endMatch();
		this.players[1].endMatch();
		System.exit(0);			
	}
	
	
	
		
	public void printScores()
	{
		System.out.println("The secret code is: "+currentSecretCode.toString());
		this.currentBoard.print();
		System.out.println("Player "+this.players[0]+" score: "+this.scores[0]+"\tPlayer "+this.players[1]+" score: "+this.scores[1]);
	}
	public boolean verifyCode(Code code) throws NoSuchElementException,IllegalArgumentException
	{			
		if(!code.get().stream().allMatch(this.pegSet::containsValue)) throw new NoSuchElementException("Empty spaces not allowed!");
		if(code.get().size()!=this.codeSize) throw new IllegalArgumentException("Code size ("+code.get().size()+" different from expected ("+this.codeSize+")");
		return true;
	
	}
	
	public boolean verifyOutcome(Outcome o) throws IllegalArgumentException
	{
		if(o.getBlackPegs()+o.getWhitePegs()>this.codeSize) throw new IllegalArgumentException("Too many pegs");
		if(!o.equals(Match.check(this.currentSecretCode, this.currentGuess))) throw new IllegalArgumentException("Invalid response, expected :"+Match.check(currentSecretCode, this.currentBoard.getLastRow().getCode()).toString());
		return true;
	}
	public Code parseCodeString(String str) throws NoSuchElementException,IllegalArgumentException
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
	public Outcome parseOutcomeString(String str) throws NoSuchElementException,IllegalArgumentException
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

	public static Outcome check(Code code, Code guess) {
		Outcome response = new Outcome();
		LinkedList<CodePeg> tmpCode = new LinkedList<>(code.get());
		LinkedList<CodePeg> tmpGuess = new LinkedList<>( guess.get());
		ListIterator<CodePeg> guessIterator = tmpGuess.listIterator();
		ListIterator<CodePeg> codeIterator = tmpCode.listIterator();
		while(guessIterator.hasNext() && codeIterator.hasNext())
		{
			if(guessIterator.next()==codeIterator.next())
			{
				response.addBlackPeg();
				guessIterator.remove();
				codeIterator.remove();
			}
		}
		tmpCode.stream().forEach(x -> {
			if(tmpGuess.contains(x))
			{response.addWhitePeg();tmpGuess.remove(x);}
			});		
		return response;
	}
	
	
	
	

	public int getP1Score()
	{
		return this.scores[0];  //p1 if  started as codemaker
	}
	public int getP2Score()
	{
		return this.scores[1];  //p1 if started as codebreaker
	}

}
