package exceptions;

public class LoginException extends Exception{
	
	public LoginException() {
		System.out.println("Incorrect username or password, please try again");
	}
	
	LoginException(String reason){
		System.out.println(reason);
	}

}
