package encoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HasherImpl implements Hasher {
	@Override
	public boolean compare(String s1, String s2) {
		String hasheds1 = hasher(s1);
		String hasheds2 = hasher(s2);
		return (hasheds2.equals(s1) || hasheds1.equals(s2));
	}

	@Override
	public String hasher(String password) {
		String algorithm = "SHA3-256";
		StringBuilder hashed = new StringBuilder();
		try {
			MessageDigest digester = MessageDigest.getInstance(algorithm);
			byte[] bytes = digester.digest(password.getBytes(StandardCharsets.UTF_8));
			for (byte bte : bytes) {
				String hex = Integer.toHexString(0xff & bte);
				if (hex.length() == 1) {
					hashed.append('0');
				}
				hashed.append(hex);
			}

		} catch (NoSuchAlgorithmException e) {
			System.out.println(algorithm + "isn't supported");
		}
		return hashed.toString();
	}
}
