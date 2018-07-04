package core;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;



@SuppressWarnings("deprecation")
public class Board extends Observable{
	private LinkedList<GuessRow> rows = new LinkedList<>();
	private final Code code;
	private int turn = 0;
	private final int boardSize;

	public Board (int boardSize, Code code)
	{
		this.boardSize = boardSize;
		this.code = code;
	}
	
	public void setGuess(Code guess){
		if(rows.size()>boardSize) throw new RuntimeException("Board is full!");
		this.rows.add(new GuessRow(guess,this.turn));
		this.setChanged();
		this.notifyObservers(this.getLastRow().getCode());
	}
									
	public void setResponse(Outcome response){
		if(this.getLastRow()==null || this.getLastRow().getResponse()!=null) throw new RuntimeException("There's no row to check!");
		this.getLastRow().setResponse(response);
		this.setChanged();
		this.notifyObservers(this.getLastRow().getResponse());
	}
		
	public void nextTurn()
	{
		turn++;
	}
	
	public int getTurn()
	{
		return turn;
	}

	public boolean hasEnded()
	{
		return (turn==this.boardSize || this.rows.peekLast().getResponse().equals(new Outcome(code.get().size(),0))) ? true : false;
	}
	
	public void print()
	{
		for(GuessRow row : rows)
		{
			if(row!=null)
			System.out.println("Guess: "+row.getCode().toString() +" Response: "+row.getResponse().toString());
		}
	}
	
	public GuessRow getLastRow()
	{
		return this.rows.isEmpty() ? null : this.rows.peekLast();
	}
	
	public List<GuessRow> getVisibleBoard()
	{
		return Collections.unmodifiableList(rows);
	}
}
