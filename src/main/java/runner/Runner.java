package runner;

import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import dao.UserDaoSql;
import dao.UserTypes;
import exceptions.LoginException;
import exceptions.TrackingException;
import populator.Populator;
import utility.ConsoleColors;
import dao.Album;
import dao.AlbumDaoSql;
import dao.Progress;
import dao.ProgressDaoSql;
import dao.User;

public class Runner {
	// fix static error, then get rid of this object
	private static ConsoleColors c = new ConsoleColors();
	private static Scanner scan = new Scanner(System.in);

	public static void main(String[] args) {

		// comment this line out for persistent data
		Populator.reset();

		UserDaoSql userCaller = new UserDaoSql();
		String welcome = c.GREEN_BOLD + "\nWelcome to our tracking app." + c.RESET;
		String loginMenu = "1. Login\n2. Register\n3. Quit\nPlease choose (1, 2, or 3):" + c.YELLOW;

		boolean isLogging = true;

		clear();
		System.out.print(welcome + "\n" + loginMenu);

		do {
			String ans = scan.nextLine().toUpperCase();
			System.out.print(c.RESET);
			if (ans.isEmpty()) {
				clear();
				System.out.print(loginMenu);
				continue;
			}

			switch (ans.charAt(0)) {
			case 'L':
			case '1':
				System.out.println(c.GREEN_BOLD + "\nPlease log in!" + c.RESET);
				System.out.print("username:" + c.YELLOW);
				try {
					String username = scan.nextLine();
					System.out.print(c.RESET + "password:" + c.YELLOW);
					String password = scan.nextLine();
					System.out.println(c.RESET);

					User loggedUser = new User(username, password);
					User verifiedUser = userCaller.loginUser(loggedUser);
					if (verifiedUser != null) {
						// call menu function
						clear();
						System.out.println(c.GREEN + "You have successfully logged in " + verifiedUser.getUsername()
								+ "!" + c.RESET);
						try {
							loggedMenu(verifiedUser);
							System.out.print(loginMenu);
						} catch (TrackingException e) {
							e.printStackTrace();
						}
					} else {
						// throw LoginException and catch it
						throw new LoginException();

					}
				} catch (InputMismatchException e) {
					e.printStackTrace();
					System.out.println("Must enter a letter");
				} catch (NullPointerException e) {
					e.printStackTrace();
					System.out.println("Account not found, please try again");
				} catch (LoginException e) {
					clear();
					System.out.println(c.RED + "Login failed. Invalid username or password." + c.RESET);
					System.out.print(loginMenu);
				}
				break;
			case 'R':
			case '2':
				System.out.println(c.YELLOW + "\nPlease try to use a unique username and a difficult password.\n"
						+ c.RESET + "We store your password with MD5 message-digest algorithm, 128bit hash value.");
				System.out.println("\nusername:" + c.YELLOW);
				String newUsername = scan.nextLine();
				System.out.println(c.RESET + "\npassword:" + c.YELLOW);
				String password = scan.nextLine();
				System.out.print(c.RESET);

				boolean result = userCaller.createUser(newUsername, password);
				if (result) {
					System.out.println(c.GREEN + "\nUser " + newUsername + " created successfully." + c.RESET);
				} else {
					System.out.println(c.RED + "\nError, try again with another username." + c.RESET);
				}
				clear();
				System.out.print(loginMenu);
				break;

			case 'Q':
			case '3':
				isLogging = false;
				System.out.println(c.GREEN_BOLD_BRIGHT + "Thanks for using our progress tracking app. Have a great day!"
						+ c.RESET);

				break;

			default:
				clear();
				System.out.println(c.RED + "Invalid input. Please try again." + c.RESET);
				System.out.print(loginMenu);
				break;
			}

		} while (isLogging);

		scan.close();
	}

	private static void clear() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 50; i++)
			sb.append("\n");
		String banner = c.PURPLE + c.WHITE_BACKGROUND + "\r\n"
				+ "  _________      __   _____ _                     _______             _             \r\n"
				+ " |__   __\\ \\    / /  / ____| |                   |__   __|           | |            \r\n"
				+ "    | |   \\ \\  / /  | (___ | |__   _____      __    | |_ __ __ _  ___| | _____ _ __ \r\n"
				+ "    | |    \\ \\/ /    \\___ \\| '_ \\ / _ \\ \\ /\\ / /    | | '__/ _` |/ __| |/ / _ \\ '__|\r\n"
				+ "    | |     \\  /     ____) | | | | (_) \\ V  V /     | | | | (_| | (__|   <  __/ |   \r\n"
				+ "    |_|      \\/     |_____/|_| |_|\\___/ \\_/\\_/      |_|_|  \\__,_|\\___|_|\\_\\___|_|   \r\n"
				+ "                                                                                    \r\n" + c.RESET;
		sb.append(banner);

		System.out.println(sb);
	}

	public static void loggedMenu(User user) throws TrackingException {			
		//2 separate menus for user and admin
		//chooseOption() will remerge the two
		
		boolean doLoop = true;
		int ans = 0;
		do {
			if(user.getUserType() == UserTypes.USER)
				printUserMenu(user);
			else
				printAdminMenu(user);
			
			try {
				ans = Integer.parseInt(scan.nextLine().trim());
			} catch (NumberFormatException e) {
				System.out.println(c.RED + "Input must be a valid integer" + c.RESET);
			}
			System.out.print(c.RESET);
			
			if(user.getUserType() == UserTypes.USER) {
				if(ans >= 1 && ans <= 4) { //valid
					chooseOption(ans + 1, user);
				}
				if(ans == 4) doLoop = false; //log out
			}
			else {
				if(ans >= 1 && ans <= 5) { //valid
					chooseOption(ans, user);
				}
				if(ans == 5) doLoop = false; //log out
			}
			ans = 0;
		} while(doLoop);
			
	}

	public static void chooseOption(int ans, User user) throws TrackingException {
		AlbumDaoSql albumCaller = new AlbumDaoSql();	
		ProgressDaoSql progressCaller = new ProgressDaoSql();		
		clear();
		
		switch (ans) {
		case 0: break;
		case 1:
			addAlbum(albumCaller);
			break;

		case 2:
			// Add Progress
			// Assumed that user already has an id
			int userId = user.getUser_id();

			List<Album> albList = albumCaller.getAllAlbums();
			
			int albumId = -1;
			do {				
				System.out.println(c.CYAN + "ADD PROGRESS" + c.RESET);
				System.out.println("\nID  -  Title");
				albList.forEach(album -> {
					if (album.getAlbum_id() < 10)
						System.out.printf(" %s  -  %s\n", album.getAlbum_id(), album.getAlbum());
					else
						System.out.printf("%s  -  %s\n", album.getAlbum_id(), album.getAlbum());
				});
				System.out.print("Please choose an ID:" + c.YELLOW);
				try {	
					albumId = Integer.parseInt(scan.nextLine().trim());										
					if(albumId < -1) {
						clear();
						System.out.println(c.RED + "Invalid album ID. Try again." + c.RESET);
					}
				} catch (Exception e) {
					clear();
					System.out.println(c.RED + "Invalid album ID. Try again." + c.RESET);
				}				
				
				System.out.print(c.RESET);
			} while(albumId <= 0);

			String progressChoice;
			String[] progressStatus = { "not completed", "in-progress", "completed", "" };

			progressMenu();

			int choice = 0;
			try {	
				choice = Integer.parseInt(scan.nextLine().trim());										
				if(choice < 0) {
					clear();
					System.out.println(c.RED + "Invalid album ID. Try again." + c.RESET);
				}
			} catch (Exception e) {
				clear();
				System.out.println(c.RED + "Invalid album ID. Try again." + c.RESET);
			}

			String message = c.RED + "Invalid progress entered" + c.RESET;
			boolean stillChoosing = true;
			while (stillChoosing) {
				clear();
				progressMenu();
				switch (choice) {
				case 6:
					progressChoice = progressStatus[0];
					Progress progressAdded = new Progress(userId, albumId, progressChoice);
					boolean progressAddResult = progressCaller.addProgress(progressAdded);
					if (progressAddResult) {
						System.out.println(progressAdded);
						System.out.println(c.GREEN + "Progress tracker successfully added" + c.RESET);
					} else {						
						throw new TrackingException(message);
					}
					stillChoosing = false;
					break;
				case 7:
					progressChoice = progressStatus[1];
					Progress progressAdded2 = new Progress(userId, albumId, progressChoice);
					boolean progressAddResult2 = progressCaller.addProgress(progressAdded2);
					if (progressAddResult2) {
						System.out.println(progressAdded2);
						System.out.println(c.GREEN + "Progress tracker successfully added" + c.RESET);
					} else {
						throw new TrackingException(message);
					}
					stillChoosing = false;
					break;
				case 8:
					progressChoice = progressStatus[2];
					Progress progressAdded3 = new Progress(userId, albumId, progressChoice);
					boolean progressAddResult3 = progressCaller.addProgress(progressAdded3);
					if (progressAddResult3) {
						System.out.println(progressAdded3);
						System.out.println(c.GREEN + "Progress tracker successfully added" + c.RESET);
					} else {
						throw new TrackingException(message);
					}
					stillChoosing = false;
					break;

				case 0: break;
				default:
					System.out.println(message);
				}
			}

			break;

		case 3:
			// Update Progress
			List<Progress> progList = progressCaller.getAllUserTrackers(user.getUser_id());
			int userId2 = user.getUser_id();

			System.out.println(c.CYAN + "ADD PROGRESS" + c.RESET);
			viewAlbums(progList);

			System.out.println("\n");

			int albumId2 = scan.nextInt();

			int choice2;
			String progressChoice2;
			String[] progressStatus2 = { "not completed", "in-progress", "completed", "" };
			progressUpdateMenu();



			choice2 = scan.nextInt();

			boolean stillChoosing2 = true;
			while (stillChoosing2) {
				switch (choice2) {
				case 6:
					progressChoice2 = progressStatus2[0];
					Progress progressAdded = new Progress(userId2, albumId2, progressChoice2);
					boolean progressAddResult = progressCaller.updateProgress(progressAdded);
					if (progressAddResult) {
						System.out.println(progressAdded);
						System.out.println(c.GREEN + "Progress tracker successfully updated" + c.RESET);
					} else {
						System.out.println(c.RED + "Could not update progress tracker" + c.RESET);
						throw new TrackingException();
					}
					stillChoosing2 = false;
					break;
				case 7:
					progressChoice2 = progressStatus2[1];
					Progress progressAdded2 = new Progress(userId2, albumId2, progressChoice2);
					boolean progressAddResult2 = progressCaller.updateProgress(progressAdded2);
					if (progressAddResult2) {
						System.out.println(progressAdded2);
						System.out.println(c.GREEN + "Progress tracker successfully updated" + c.RESET);
					} else {
						System.out.println(c.RED + "Could not update progress tracker" + c.RESET);
						throw new TrackingException();
					}
					stillChoosing2 = false;
					break;
				case 8:
					progressChoice2 = progressStatus2[2];
					Progress progressAdded3 = new Progress(userId2, albumId2, progressChoice2);
					boolean progressAddResult3 = progressCaller.updateProgress(progressAdded3);
					if (progressAddResult3) {
						System.out.println(progressAdded3);
						System.out.println(c.GREEN + "Progress tracker successfully updated" + c.RESET);
					} else {
						System.out.println(c.RED + "Could not update progress tracker" + c.RESET);
						throw new TrackingException();
					}
					stillChoosing2 = false;
					break;

				default:
					System.out.println("Incorrect input");
					System.out.println("What's the status of the album to update?" + "\n"
							+ "Please enter 6 for not completed " + " 7 for in-progress or 8 for completed");
					stillChoosing2 = false;
					break;
				}
			}

			break;

		case 4:
			List<Progress> progList2 = progressCaller.getAllUserTrackers(user.getUser_id());
			// View Albums and their trackers
			System.out.println("Your progress trackers and albums");
			System.out.println("----------------------------------------------------------------------------");

			viewAlbums(progList2);

			System.out.println("\n");

			break;

		case 5:			
			System.out.println("You have logged out\n");
			break;

		default:
			System.out.println("Invalid input, try again!\n");
			scan.nextLine();
		}
	}
		
	public static void printUserMenu(User user) {
		clear();
		
		System.out.println(c.CYAN + "Hello, " + user.getUsername() + c.RESET);		
		System.out.println("1: Add Progress");
		System.out.println("2: Update Progress");
		System.out.println("3: List Albums");
		System.out.println("4: Logout");
		System.out.print("Please choose an option (1-4):" + c.YELLOW);
	}
	
	public static void printAdminMenu(User user) {
		clear();
		
		System.out.println(c.CYAN + "Hello, " + user.getUsername() + c.RESET);
		System.out.println("You are logged in as an " + c.WHITE_UNDERLINED + "admin." + c.RESET);
		System.out.println(c.WHITE_BOLD + "1: Add Album" + c.RESET);
		System.out.println("2: Add Progress");
		System.out.println("3: Update Progress");
		System.out.println("4: List Albums");
		System.out.println("5: Logout");
		System.out.print("Please choose an option (1-5):" + c.YELLOW);
	}
	
	
	public static void progressMenu() {
		System.out.println(c.CYAN + "How far are you?" + c.RESET);
		System.out.println("6: Not Started");
		System.out.println("7: In Progress");
		System.out.println("8: Completed");
		System.out.print("Choose your progress (6-8):" + c.YELLOW);
	}
	public static void progressUpdateMenu() {
		System.out.println(c.CYAN + "Update Progress?" + c.RESET);
		System.out.println("6: Not Started");
		System.out.println("7: In Progress");
		System.out.println("8: Completed");
		System.out.print("Choose your progress (6-8):" + c.YELLOW);
	}

	public static void addAlbum(AlbumDaoSql albumCaller) {
		System.out.println("What's the name of the new album?");
		String testVar = scan.next();
		String albumName = testVar + scan.nextLine();

		Album albumAdded = new Album(albumName);
		boolean addResult = albumCaller.addAlbum(albumAdded);
		if (addResult) {
			System.out.println(albumAdded);
			System.out.println("Album successfully added");
		} else {
			System.out.println("Could not add album");
		}
	}

	public static void viewAlbums(List<Progress> progList) {

		AlbumDaoSql albumCaller = new AlbumDaoSql();
		if (null == progList || progList.isEmpty()) {
			System.out.println("\nYou aren't tracking any albums.\n");
			return;
		}
		List<Album> albums = albumCaller.getAllAlbums();
		progList.forEach(progress -> {

			Album progressAlbum =
					albums.stream().filter(album -> album.getAlbum_id() == progress.getTrack_id())
					      .findFirst().orElse(null);
			
			if(progressAlbum == null) return;
			
			if (progressAlbum.getAlbum_id() < 10)	System.out.printf("\n %s - %s -> %s", progressAlbum.getAlbum_id(),
					progressAlbum.getAlbum(), progress.getProgress());
			else System.out.printf("\n%s - %s -> %s", progressAlbum.getAlbum_id(),
					progressAlbum.getAlbum(), progress.getProgress());
			System.out.println("\nRating = " + progressAlbum.getRating());
		});
	}

}
