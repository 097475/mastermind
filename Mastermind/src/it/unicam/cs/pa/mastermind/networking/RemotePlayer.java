package networking;
import java.util.List;

import core.Code;
import core.GuessRow;
import core.Outcome;
import core.Player;

public class RemotePlayer implements Player {

	Server server;
	int id;
	public RemotePlayer(Server server, int id) {
		// TODO Auto-generated constructor stub
		this.server = server;
		this.id = id;
	}

	@Override
	public Code getGuess(List<GuessRow> visibleBoard) {
		// TODO Auto-generated method stub
		return server.getGuess(visibleBoard,id);
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return server.getCode(id);
	}

	@Override
	public Outcome checkLastRow(Code guess) {
		// TODO Auto-generated method stub
		return server.getResponse(guess,id);
	}

	@Override
	public void endRound() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endMatch() {
		// TODO Auto-generated method stub
		
	}

}
