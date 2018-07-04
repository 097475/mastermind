package core;

import java.util.HashMap;
import java.util.Map;

public class BasePegClassFactory implements PegClassFactory{

	private static Map<String,Class<? extends CodePeg>> classes = new HashMap<>();
	public BasePegClassFactory() {
		register("Colors", pegs.ColorPeg.class);
		register("Digits", pegs.DigitPeg.class);
		register("Letters", pegs.LetterPeg.class);
	}
	@Override
	public Class<? extends CodePeg> getClass(String str) {
		Class<? extends CodePeg> c = classes.get(str);
		if(c==null) throw new RuntimeException("Class has not been registered!");
		else return c;
	}
	
	public static void register(String name,Class<? extends CodePeg> cls)
	{
		if(!classes.containsKey(name))
			classes.put(name, cls);
	}

}
