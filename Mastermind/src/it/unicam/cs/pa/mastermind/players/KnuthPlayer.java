package players;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import core.Code;
import core.CodePeg;
import core.GuessRow;
import core.Match;
import core.Outcome;





public class KnuthPlayer extends AIPlayer{
	private int alphabet;
	private Set<Code> possibleSolutions, availableGuesses;
	private List<Outcome> possibleOutcomes = new ArrayList<>(((this.codeSize+1)*(this.codeSize+2))/2); //0-01234 1-0123 2-012 3-01 4-0 at most
	
	public KnuthPlayer(int codeSize, Map<String,CodePeg> pegSet)
	{
		super(codeSize,pegSet);
		this.alphabet = pegSet.size();
		this.generateOutcomes();
		this.generateSets();
	}
	
	public void endRound()
	{
		this.generateSets();
	}
	
	private Code initialGuess()
	{
		List<CodePeg> tmp = new LinkedList<>();
		if(this.codeSize == 1)
			tmp.add(pegSet.get(0));
		else
		{
			for(int i = 0; i<this.codeSize/2;i++)
				tmp.add(pegSet.get(0));
			for(int i = this.codeSize/2; i<this.codeSize;i++)
				tmp.add(pegSet.get(1));
		}	
		return new Code(tmp);
	}
	
	@Override
	public Code getGuess(List<GuessRow> visibleBoard)
	{
		Code nextMove;
		if(!visibleBoard.isEmpty())
		{
			possibleSolutions.removeIf(x->!Match.check(x, visibleBoard.get(visibleBoard.size()-1).getCode()).equals(visibleBoard.get(visibleBoard.size()-1).getResponse()));
			nextMove = availableGuesses.parallelStream().collect(Collectors.groupingBy(z->{
			int max = 0;
			for(Outcome result : possibleOutcomes)
			{
				int count = 0;
				for(Code solution : possibleSolutions) 
					if(Match.check(z, solution).equals(result))
						count++;
				if(count > max)
					max = count;
			}
			return max;
			},TreeMap::new,Collectors.toList())).firstEntry().getValue().stream().min(Comparator.comparing(x->possibleSolutions.contains(x) ? 0 : 1)).get();
			availableGuesses.remove(nextMove);			
		}
		else nextMove = this.initialGuess();
		return nextMove;
	}
	

	private void generateSets()
	{
		int setSize = (int)(Math.pow((double)this.alphabet,(double)this.codeSize));
		List<CodePeg> string = new ArrayList<>();		
		for(int i = 0;i<this.codeSize;i++)
			string.add(pegSet.get(0));
		possibleSolutions = new HashSet<>();
		availableGuesses = new HashSet<>();
		for(int i = 0; i<setSize;i++)
		{		
			possibleSolutions.add(new Code(string));
			availableGuesses.add(new Code(string));
			for(int j = 0; j<this.codeSize; j++)
			{
				string.set(j, pegSet.get((pegSet.indexOf(string.get(j))+1)%alphabet));
				if(string.get(j) != pegSet.get(0)) break;
			}
		}
	}
	
	private void generateOutcomes()
	{
		for(int i = 0; i<= this.codeSize; i++)
		{
			for(int j = 0; j<this.codeSize+1-i;j++)
			{
				possibleOutcomes.add(new Outcome(i,j));
			}
		}
	}
}
