package players;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import core.Code;
import core.CodePeg;
import core.GuessRow;
import core.Match;
import core.Outcome;

public class GeneticPlayer extends AIPlayer{

	private static final int POPSIZE = 150, MAXGEN = 100, ELIGIBLES = 60,BIAS = 3;
	private Set<List<CodePeg>> population;
	private Set<List<CodePeg>> eligibles;
	private Random RNG = new Random();
	
	public GeneticPlayer(int codeSize, Map<String, CodePeg> pegSet) {
		super(codeSize, pegSet);
		this.population = new HashSet<>(POPSIZE);
		this.eligibles = new HashSet<>(ELIGIBLES);
	}
	
	@Override
	public void endRound() {
	}
	
	@Override
	public Code getGuess(List<GuessRow> visibleBoard) {
		Code nextMove = null;
		if(!visibleBoard.isEmpty())
		{	
			this.eligibles.clear();
			while(this.eligibles.size()==0)
			{
				this.generateInitialPop();
				this.generatePop(visibleBoard);
			}
			nextMove = new Code(this.selectGuess(visibleBoard));
		}
		else nextMove = this.initialGuess();
		return nextMove;
	}
	
	private Code initialGuess()
	{
		List<CodePeg> tmp = new LinkedList<>();
		int j = 1;
		if(this.codeSize == 1)
			tmp.add(pegSet.get(0));
		else
		{
			for(int i = 0; i<this.codeSize/2;i++)
				tmp.add(pegSet.get(0));
			for(int i = this.codeSize/2; i<this.codeSize;i++)
				tmp.add(pegSet.get((j++)%pegSet.size()));
		}
		return new Code(tmp);
	}
	
	private void generateInitialPop()
	{
		while(this.population.size()<POPSIZE)
		{
			List<CodePeg> tmp = this.getRandomNewCode();
			this.population.add(tmp);
		}
			
	}
	
	private void generatePop(List<GuessRow> visibleBoard)
	{
		for(int i = 0; i<MAXGEN; i++)
		{
			this.breed(visibleBoard);
		}
	}
	
	private List<CodePeg> getRandomNewCode()
	{
		List<CodePeg> tmp;
		do {
			tmp = RNG.ints(this.codeSize,0,pegSet.size()).mapToObj(pegSet::get).collect(Collectors.toList());
		}while(this.population.contains(tmp));
		return tmp;
	}
	
	private List<CodePeg> cross(List<CodePeg> p1,List<CodePeg> p2)
	{
		List<CodePeg> tmp;
		if(RNG.nextInt(2)==0)
		{
			int index = RNG.nextInt(this.codeSize-2)+1; 
			tmp = new ArrayList<>(p1);
			for(int i = index; i<this.codeSize;i++)
				tmp.set(i, p2.get(i));
		}
		else
		{
			int index1 = RNG.nextInt(this.codeSize-3)+1;
			int index2 = RNG.nextInt(this.codeSize-2)+index1+1;
			tmp = new ArrayList<>(p1);
			for(int i = index1; i<index2;i++)
				tmp.set(i, p2.get(i));
		}		
		return tmp;
	}
	
	private void mutate(List<CodePeg> tmp)
	{
		int index = RNG.nextInt(this.codeSize);
		tmp.set(index, pegSet.get(RNG.nextInt(this.pegSet.size())));
	}
	
	private void permute(List<CodePeg> tmp)
	{
		int index1,index2;
		index1 = RNG.nextInt(this.codeSize);
		do {
			index2 = RNG.nextInt(this.codeSize);
		}while(index1==index2);
		Collections.swap(tmp, index1, index2);
	}
	
	private void invert(List<CodePeg> tmp)
	{
		int index1 = RNG.nextInt(this.codeSize-3)+1;
		int index2 = RNG.nextInt(this.codeSize-2)+index1+1;
		Collections.reverse(tmp.subList(index1, index2));
	}
	private boolean isEligible(List<CodePeg> tmp,List<GuessRow> visibleBoard)
	{
		return this.eligibility(tmp,visibleBoard) == 0 && !visibleBoard.stream().map(GuessRow::getCode).map(Code::get).anyMatch(x->x.equals(tmp))? true : false;
	}
	private int eligibility(List<CodePeg> tmp,List<GuessRow> visibleBoard)
	{
		return visibleBoard.stream().mapToInt(x->{
			Outcome o = Match.check(x.getCode(), new Code(tmp));
			return Math.abs((x.getResponse().getBlackPegs()+x.getResponse().getWhitePegs())-(o.getBlackPegs()+o.getWhitePegs()));
		}).sum();
	}
	private int fitness(List<CodePeg> tmp, List<GuessRow> visibleBoard)
	{		
		return this.eligibility(tmp,visibleBoard) + 2*this.codeSize*visibleBoard.size();
	}
	
	private HashSet<List<CodePeg>> generateParents(List<GuessRow> visibleBoard)
	{
		HashSet<List<CodePeg>> parents = new HashSet<>(POPSIZE);
		List<List<CodePeg>> orderedfit = this.population.stream().sorted((x,y)-> fitness(y,visibleBoard) - fitness(x,visibleBoard)).collect(Collectors.toList()); 
		for(int i = 0; i<POPSIZE;i++)
		{
			List<CodePeg> selected = orderedfit.get(this.randomBiased(POPSIZE));
			if(!parents.contains(selected))
				parents.add(selected);
		}
		return parents;
	}
	
	private void breed(List<GuessRow> visibleBoard)
	{
		int p1,p2;
		List<CodePeg> tmp;
		List<List<CodePeg>> parents = new ArrayList<>(this.generateParents(visibleBoard));
		this.population.clear();
		for(int i = 0 ; i<POPSIZE; i++)
		{
			p1 = this.RNG.nextInt(parents.size());
			p2 = this.RNG.nextInt(parents.size());
			tmp = this.cross(parents.get(p1),parents.get(p2));
			if(RNG.nextInt(33)==0)
				this.mutate(tmp);
			if(RNG.nextInt(33)==0)
				this.permute(tmp);
			if(RNG.nextInt(50)==0)
				this.invert(tmp);
			if(this.isEligible(tmp,visibleBoard) && this.eligibles.size()<ELIGIBLES)
				this.eligibles.add(tmp);
			if(this.population.contains(tmp))
				tmp = this.getRandomNewCode();
			this.population.add(tmp);
		}
		
	}
	private List<CodePeg> selectGuess(List<GuessRow> visibleBoard)
	{
		Set<List<CodePeg>> tmp = new HashSet<>(this.eligibles);
		List<CodePeg> candidateGuess = null;
		int avg,min = Integer.MAX_VALUE;
		if(this.eligibles.size()==1) return this.eligibles.stream().findAny().get();
		for(List<CodePeg> guess : eligibles)
		{
			tmp.remove(guess);		
			avg = this.averageEsclusions(tmp, guess);
			if(avg<=min)
			{
				min = avg;
				candidateGuess = guess;
			}
			tmp.add(guess);
		}
		this.eligibles.remove(candidateGuess);
		return candidateGuess;
	}
	
	private int averageEsclusions(Set<List<CodePeg>> src,List<CodePeg> guess)
	{
		Set<List<CodePeg>> tmp = new HashSet<>(src);
		LinkedList<GuessRow> st = new LinkedList<>();
		int counter = 0;
		for(List<CodePeg> code : src)
		{
			tmp.remove(code);
			Outcome o = Match.check(new Code(code), new Code(guess));
			st.addFirst(new GuessRow(new Code(guess),0));
			st.peek().setResponse(o);
			counter += this.countEligibles(tmp, st);
			tmp.add(code);
			st.pollFirst();
		}
		return counter/src.size();
	}
	private int countEligibles(Set<List<CodePeg>> src, List<GuessRow> guess)
	{
		return (int) src.stream().filter(x->this.isEligible(x, guess)).count();
	}
	private int randomBiased (int max) {
	     double v = Math.pow(RNG.nextDouble(), BIAS); 
	     return (int)(v * max);
	 }
}
