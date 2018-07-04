package players;
import java.util.List;
import java.util.Map;

import core.Code;
import core.CodePeg;
import core.GuessRow;
import core.Player;



public class RandomPlayer extends AIPlayer{
	
	public RandomPlayer(int codeSize, Map<String,CodePeg> pegSet)
	{
		super(codeSize,pegSet);
	}

	@Override
	public Code getGuess(List<GuessRow> visibleBoard) {
		return Player.getRandomCode(this.codeSize, this.pegSet);
	}

	
	public void endRound() {
	}

}
