package core;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class BasePegSetFactory implements PegSetFactory {

	private final Class<? extends CodePeg> pegClass;
	private static Map<Class<? extends CodePeg>,BiFunction<Integer,Boolean,Map<String, CodePeg>>> factories = new HashMap<>();
	
	public <E extends CodePeg> BasePegSetFactory(Class<E> pegs)
	{
		this.pegClass = pegs;
		register(pegs.ColorPeg.class,this::baseFactory);
		register(pegs.DigitPeg.class,this::baseFactory);
		register(pegs.LetterPeg.class,this::baseFactory);
	}
	public <E extends CodePeg> Map<String, CodePeg> baseFactory(int n, boolean empty)
	{
		Map<String,CodePeg> pegSet;
		if(this.pegClass.getEnumConstants()==null) throw new RuntimeException("Class is not an Enum!");
		if(this.pegClass.getEnumConstants().length<n) throw new RuntimeException("Class does not contain enough pegs!");
		pegSet = new LinkedHashMap<>(n + (empty ? 1:0));
		Arrays.stream(this.pegClass.getEnumConstants()).forEach(x->{ if(pegSet.size()<n)pegSet.put(x.toString(),x);});
		if(empty) pegSet.put("EMPTY",CodePeg.EmptyEnum.EMPTY);
		return pegSet;
	}
	public static void register(Class<? extends CodePeg> cls,BiFunction<Integer,Boolean,Map<String, CodePeg>> func)
	{
		if(!factories.containsKey(cls))
			factories.put(cls, func);
	}

	@Override
	public Map<String, CodePeg> getPegSet(int n, boolean empty) {
		// TODO Auto-generated method stub
		return factories.get(pegClass).apply(n, empty);
	}
	


}
