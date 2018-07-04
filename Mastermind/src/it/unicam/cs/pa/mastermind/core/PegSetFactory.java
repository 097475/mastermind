package core;
import java.util.Map;


@FunctionalInterface
public interface PegSetFactory {
	public Map<String,CodePeg> getPegSet(int n, boolean empty);
}
