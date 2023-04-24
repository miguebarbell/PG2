package exeptions;

public class TrackingException extends Exception {
	TrackingException() {
		System.out.println("This is already tracked.");
	}
	TrackingException(String message) {
		System.out.println(message);
	}
}
