package core;

import java.util.Map;

@FunctionalInterface
public interface PlayerFactory {
	public Player createPlayer(String UI,Map<String,CodePeg> pegSet, int codeSize, String type, boolean codeBreaker);
}
