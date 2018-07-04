package core;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public interface Player {
	public Code getGuess(List<GuessRow> visibleBoard);
	public Code getCode();
	public Outcome checkLastRow(Code guess);
	public void endRound();
	public void endMatch();
	public static Code getRandomCode(int codeSize, List<CodePeg> pegSet)  //map?
	{
		Random rand = new Random();
		List<CodePeg> randomCode = new LinkedList<>();
		for(int i = 0; i<codeSize; i++)
		{
			randomCode.add(pegSet.get(rand.nextInt(pegSet.size())));
		}	
		return new Code(randomCode);		
	}
	
}
