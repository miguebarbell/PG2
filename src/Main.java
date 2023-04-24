import exeptions.LoginException;
import runner.Runner;


public class Main {
	public static void main(String[] args) {
		try {
			Runner.main(args);

			
			throw new LoginException();
		} catch (LoginException e) {

		}
	}
}
