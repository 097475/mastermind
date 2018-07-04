package players;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import core.Code;
import core.CodePeg;
import core.GuessRow;
import core.Match;
import core.Outcome;
import core.Player;

public abstract class AIPlayer implements Player{
	protected final int codeSize;
	protected final List<CodePeg> pegSet;
	private Code secretCode = null;
	
	public AIPlayer(int codeSize, Map<String,CodePeg> pegSet)
	{
		this.codeSize = codeSize;
		this.pegSet = new ArrayList<CodePeg>(pegSet.values());
	}
	
	public abstract void endRound();
	
	public abstract Code getGuess(List<GuessRow> visibleBoard);
	
	public void endMatch()
	{
	}
	
	public final Code getCode()
	{
		return this.secretCode = Player.getRandomCode(codeSize, pegSet);
	}
	public final Outcome checkLastRow(Code guess)
	{
		return Match.check(this.secretCode, guess);
	}
	
}
