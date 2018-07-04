package core;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Outcome implements Serializable{
	private int blackPegs;
	private int whitePegs;

	public Outcome()
	{
		this(0,0);
	}

	public Outcome(int b, int w)
	{
		this.blackPegs = b;
		this.whitePegs = w;
	}

	public void addWhitePeg() {
		this.whitePegs++;
	}

	public void addBlackPeg() {
		this.blackPegs++;
	}

	public int getWhitePegs()
	{
		return this.whitePegs;
	}

	public int getBlackPegs()
	{
		return this.blackPegs;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		Outcome other = (Outcome) obj;
		return (this.blackPegs==other.blackPegs && this.whitePegs == other.whitePegs) ? true : false;
	}

	@Override
	public String toString() { 
		return "White pegs: "+this.whitePegs+" Black pegs: "+this.blackPegs+"\n";
	} 
}
