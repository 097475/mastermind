package players;
import java.util.List;
import java.util.Map;

import core.Code;
import core.CodePeg;
import core.GuessRow;
import core.Outcome;
import core.Player;

public abstract class HumanPlayer implements Player {

	protected final int codeSize;
	protected final Map<String,CodePeg> pegSet;
	
	public HumanPlayer(int codeSize, Map<String,CodePeg> pegSet)
	{
		this.codeSize = codeSize;
		this.pegSet = pegSet;
	}
	
	public abstract void endRound();
	public abstract void endMatch();
	public abstract Code getGuess(List<GuessRow> visibleBoard);
	public abstract Code getCode();
	public abstract Outcome checkLastRow(Code guess);
	
	

}
