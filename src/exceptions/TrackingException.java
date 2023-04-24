package exceptions;

public class TrackingException extends Exception {
   public TrackingException() {
	   System.out.println("Tracker not found");
   }
   
   public TrackingException(String message){
	   System.out.println(message);
   }
}
