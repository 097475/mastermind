package core;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class Code implements Serializable{
	private LinkedList<CodePeg> code;
	
	public Code(List<CodePeg> code)
	{
		this.code = new LinkedList<>(code);
	}
	
	@Override
	public String toString()
	{
		return code.stream().map(x->x.toString()+" ").reduce("", String::concat);
	}
	
	public void print()
	{
		System.out.println(this.toString());
	}
	
	public List<CodePeg> get()
	{
		return code;
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
		    Code other = (Code) obj;
		   return (this.code.equals(other.get())) ? true : false;
	}
	@Override
	public int hashCode()
	{
		return this.code.hashCode();
	}
}
