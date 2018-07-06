package core;
import java.util.Map;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import gui.GUI;
import networking.HumanRemotePlayer;
import networking.Server;
import players.HumanGUIPlayer;


public class Main {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Properties settings = Settings.getGameProperties();
		switch(settings.getProperty("Match"))
		{
		case "Local":	Match.init(); break;
		case "Server": Server.init(); Match.init(); break;
		case "Client": Map<String,CodePeg> pegSet =  new BasePegSetFactory(pegs.ColorPeg.class).getPegSet(6,false);
			new HumanRemotePlayer(new HumanGUIPlayer(4, pegSet, new GUI(Boolean.parseBoolean(settings.getProperty("CodeBreaker")),pegSet))); break;
		
		}

		
	}

}
