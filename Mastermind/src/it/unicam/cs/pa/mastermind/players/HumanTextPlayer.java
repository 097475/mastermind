package players;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import core.Code;
import core.CodePeg;
import core.GuessRow;
import core.InfoManager;
import core.Match;
import core.Outcome;

public class HumanTextPlayer extends HumanPlayer{ 
	private BufferedReader in;
	private PrintStream out;
	private Code secretCode = null;
	public HumanTextPlayer(int codeSize, Map<String,CodePeg> pegSet,  InputStream in, PrintStream out) {
		super(codeSize,pegSet);
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = out;
	}

	@Override
	public void endRound() {
		Match.getMatch().printScores();
		out.println("Press Enter to continue...");
		try {
			in.readLine();
		} catch (IOException e) {
			out.println(e.getMessage());
		}
	}

	@Override
	public Code getGuess(List<GuessRow> visibleBoard) {
		Code code = null;
		visibleBoard.forEach(x->System.out.println(x.getCode()+" "+x.getResponse()));	
		this.out.println("Insert the guess (Available pegs: "+ pegSet.toString()+")");
		try {
			while(code == null)
				code = InfoManager.getInfo().parseCodeString(this.in.readLine());
		} catch (IOException e) {
			out.println(e.getMessage());
		}	
		return code;
	}

	@Override
	public Code getCode() {
		Code code = null;
		this.out.println("Insert the hidden code (Available pegs: "+ pegSet.toString()+")");
		try {
			while(code == null)
				code = InfoManager.getInfo().parseCodeString(this.in.readLine());
		} catch (IOException e) {
			out.println(e.getMessage());
		}
		return secretCode = code;
	}

	@Override
	public Outcome checkLastRow(Code guess) {  //get outcome
		Outcome o = null;
		this.out.println(guess.toString());	
		this.out.println("Insert the response (Available pegs: WHITE BLACK)");	
		this.out.println("(Your secret code was: "+secretCode.toString()+")");
		try {
			while(o == null)
				o = InfoManager.getInfo().parseOutcomeString(this.in.readLine());
		} catch (IOException e) {
			out.println(e.getMessage());
		}	
		return o;
	}

	@Override
	public void endMatch() {
	}



}
