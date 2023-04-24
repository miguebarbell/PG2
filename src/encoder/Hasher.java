package encoder;

public interface Hasher {
	boolean compare(String s1, String s2);

	String hasher(String password);
}
