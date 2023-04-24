package exeptions;

public class LoginException extends Exception {
	public LoginException() {
		System.out.println("Usernname is required");
	}
	LoginException(String reason) {
		System.out.println(reason);
	}
}
