package core;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;



class Tests {

	@Test
	public void test()
	{	
		Properties settings = new Properties();
		settings.setProperty("pegs","6" );
		settings.setProperty("Rounds", "10");
		settings.setProperty("BoardSize","8");
		settings.setProperty("CodeSize", "4");
		settings.setProperty("EmptyAllowed","False");
		settings.setProperty("CodeBreaker", "True");
		settings.setProperty("PegType", "Letters");
		settings.setProperty("Player1", "Random");
		settings.setProperty("Player2", "Random");
		settings.setProperty("UI", "gui");
		settings.setProperty("Match", "Local");
		Settings.setGameProperties(settings);
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(()->{
			Robot r;
			try {
				r = new Robot();
				while(true) {				
				int keyCode = KeyEvent.VK_F2; 
				r.keyPress(keyCode);
				r.keyRelease(keyCode);
				}
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}});
		Match.init();

		
	}
	
}
