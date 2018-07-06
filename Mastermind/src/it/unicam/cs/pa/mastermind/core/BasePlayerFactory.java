package core;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import gui.GUI;
import networking.RemotePlayer;
import networking.Server;
import players.GeneticPlayer;
import players.HumanGUIPlayer;
import players.HumanTextPlayer;
import players.KnuthPlayer;
import players.RandomPlayer;

public class BasePlayerFactory implements PlayerFactory {

	private static Map<String,BiFunction<Integer,Map<String,CodePeg>,Player>> AIPlayers = new HashMap<>();
	
	public BasePlayerFactory()
	{
		register("Knuth", KnuthPlayer::new);
		register("Random", RandomPlayer::new);
		register("Genetic", GeneticPlayer::new);
	}
	
	@Override
	public Player createPlayer(String UI,Map<String,CodePeg> pegSet, int codeSize, String type, boolean codeBreaker) {
		Player p;
		if(type == "Human" && UI =="gui")
			p = new HumanGUIPlayer(codeSize,pegSet,new GUI(codeBreaker,pegSet));
		else if(type=="Human" && UI == "TextUI")
			p = new HumanTextPlayer(codeSize,pegSet,System.in,System.out);
		else if(type=="Remote")
			p = new RemotePlayer(Server.getServer(),codeBreaker==true ? 1 : 0);
		else
			p =  AIPlayers.get(type).apply(codeSize, pegSet);
		
		if(p==null) throw new RuntimeException("Requested player has not been registered!");
		else return p;
	}
	
	public static void register(String name,BiFunction<Integer,Map<String,CodePeg>,Player> newAI)
	{
		if(!AIPlayers.containsKey(name))
			AIPlayers.put(name, newAI);
	}


}
