package core;
public class GuessRow{
	private Outcome response;
	private final Code code;
	private final int turn;
	
	public GuessRow(Code code, int turn)
	{
		this.code = code;
		this.turn = turn;
	}
	public Code getCode() {
		return code;
	}
	public Outcome getResponse() {
		return response;
	}

	public void setResponse(Outcome response) {
		this.response = this.response == null ? response : this.response;
	}
	
	public int getTurn(){
		return this.turn;
	}
	
}
