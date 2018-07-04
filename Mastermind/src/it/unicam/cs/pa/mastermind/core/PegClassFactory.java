package core;

@FunctionalInterface
public interface PegClassFactory {
	public Class<? extends CodePeg> getClass(String str);
}
