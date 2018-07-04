package players;
import java.util.List;
import java.util.Map;

import core.Code;
import core.CodePeg;
import core.GuessRow;
import core.Outcome;
import core.Player;
import gui.GUI;

public class HumanGUIPlayer extends HumanPlayer implements Player{

	private Code guess;
	private Outcome outcome;
	private GUI g;
	private Code secretCode;
	public HumanGUIPlayer(int codeSize, Map<String, CodePeg> pegSet, GUI g) {
		super(codeSize, pegSet);
		this.g = g;
		this.g.setPlayer(this);
	}

	public GUI getGUI()
	{
		return this.g;
	}
	@Override
	public void endRound() {
		this.g.endRound();
	}

	@Override
	public synchronized Code getGuess(List<GuessRow> visibleBoard) {
		this.g.nextTurn();
		this.guess = null;
		try {
			if(this.guess==null)
				this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		return this.guess;
	}

	@Override
	public synchronized Code getCode() {
		// TODO Auto-generated method stub
		this.secretCode = null;
		try {
			if(this.secretCode==null)
				this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		return this.secretCode;
	}

	@Override
	public synchronized Outcome checkLastRow(Code guess) {
		this.g.nextTurn();
		this.outcome = null;
		try {
			if(this.outcome==null)
				this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		return this.outcome;
	}
	
	public synchronized void receiveCode(Code c)
	{
		this.secretCode = c;
		this.notify();
	}
	public synchronized void receiveGuess(Code c)
	{
		this.guess = c;
		this.notify();
	}
	public synchronized void receiveOutcome(Outcome o)
	{
		this.outcome = o;
		this.notify();
	}

	@Override
	public void endMatch() {
		this.g.endMatch();
	}

}
