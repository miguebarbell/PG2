package runner;

import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import dao.UserDaoSql;
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
	private static ConsoleColors c = new ConsoleColors();
	
	public static void main(String[] args) {

		Populator.reset();

		Scanner scan = new Scanner(System.in);

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
				System.out.print("\nusername:" + c.YELLOW);
				try {
					String username = scan.nextLine();
					System.out.print(c.RESET + "password:" + c.YELLOW);
					String password = scan.nextLine();
					System.out.println(c.RESET);
					
					User loggedUser = new User(username, password);
					User verifiedUser = userCaller.loginUser(loggedUser);
					if (verifiedUser != null) {
						// call menu function
						System.out.println(c.GREEN + "You have successfully logged in " + verifiedUser.getUsername() + "!" + c.RESET);
						loggedMenu(verifiedUser, scan);

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
					System.out.println(c.RED + "Login failed." + c.RESET);
					System.out.print(loginMenu);
				}
				break;
			case 'R':
			case '2':
				System.out.println(
						"\nPlease try to use a unique username and a difficult password.\nWe store your password "
								+ "with MD5 message-digest algorithm, 128bit hash value.");
				System.out.println("\nusername:");
				String newUsername = scan.nextLine();
				System.out.println("\npassword:");
				String password = scan.nextLine();
				boolean result = userCaller.createUser(newUsername, password);
				if (result) {
					System.out.println("\nUser " + newUsername + " created successfully.");
				} else {
					System.out.println("\nError, try again with other username.");
				}
				clear();
				System.out.print(loginMenu);
				break;

			case 'Q':
			case '3':
				isLogging = false;
				System.out.println(c.GREEN_BOLD_BRIGHT + "Thanks for using our progress tracking app. Have a great day!" + c.RESET);

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
		for(int i = 0; i < 50; i++)
			sb.append("\n");
		String banner = c.PURPLE + c.WHITE_BACKGROUND + "\r\n"
				+ "  _________      __   _____ _                     _______             _             \r\n"
				+ " |__   __\\ \\    / /  / ____| |                   |__   __|           | |            \r\n"
				+ "    | |   \\ \\  / /  | (___ | |__   _____      __    | |_ __ __ _  ___| | _____ _ __ \r\n"
				+ "    | |    \\ \\/ /    \\___ \\| '_ \\ / _ \\ \\ /\\ / /    | | '__/ _` |/ __| |/ / _ \\ '__|\r\n"
				+ "    | |     \\  /     ____) | | | | (_) \\ V  V /     | | | | (_| | (__|   <  __/ |   \r\n"
				+ "    |_|      \\/     |_____/|_| |_|\\___/ \\_/\\_/      |_|_|  \\__,_|\\___|_|\\_\\___|_|   \r\n"
				+ "                                                                                    \r\n"
				+ c.RESET;
		sb.append(banner);
		
		System.out.println(sb);
	}
	
	public static void loggedMenu(User user, Scanner scan) {

		AlbumDaoSql albumCaller = new AlbumDaoSql();
		ProgressDaoSql progressCaller = new ProgressDaoSql();

		int ans;
		try {
			do {
				menu(user);

				ans = scan.nextInt();

				List<Progress> progList = progressCaller.getAllUserTrackers(user.getUser_id());

				switch (ans) {
				case 1:
					addAlbum(scan, albumCaller);
					break;

				case 2:
					// Add Progress
					// Assumed that user already has an id
					int userId = user.getUser_id();
					System.out.println("What's the album id?");
					System.out.println("----------------------------------------------------------------------------");

					List<Album> albList = albumCaller.getAllAlbums();

					System.out.println("\nID  -  Artist, 'Album'");
					albList.forEach(album -> {
						if (album.getAlbum_id() < 10)
							System.out.printf(" %s  -  %s\n", album.getAlbum_id(), album.getAlbum());
						else
							System.out.printf("%s  -  %s\n", album.getAlbum_id(), album.getAlbum());
					});

					int albumId = scan.nextInt();

					int choice;
					String progressChoice;
					String[] progressStatus = { "not completed", "in-progress", "completed", "" };



					progressMenu();

					choice = scan.nextInt();

					String message = "Invalid progress entered";
					boolean stillChoosing = true;
					while (stillChoosing) {
						switch (choice) {
						case 6:
							progressChoice = progressStatus[0];
							Progress progressAdded = new Progress(userId, albumId, progressChoice);
							boolean progressAddResult = progressCaller.addProgress(progressAdded);
							if (progressAddResult) {
								System.out.println(progressAdded);
								System.out.println("Progress tracker successfully added");
							} else {
								System.out.println(message);
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
								System.out.println("Progress tracker successfully added");
							} else {
								System.out.println(message);
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
								System.out.println("Progress tracker successfully added");
							} else {
								System.out.println(message);
								throw new TrackingException(message);
							}
							stillChoosing = false;
							break;

						default:

							stillChoosing = false;
							throw new TrackingException(message);

							//break;
						}
					}

					break;

				case 3:
					// Update Progress
					int userId2 = user.getUser_id();
					System.out.println("What's the album id to update?");
					System.out.println("----------------------------------------------------------------------------");

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
								System.out.println("Progress tracker successfully updated");
							} else {
								System.out.println("Could not update progress tracker");
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
								System.out.println("Progress tracker successfully updated");
							} else {
								System.out.println("Could not update progress tracker");
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
								System.out.println("Progress tracker successfully updated");
							} else {
								System.out.println("Could not update progress tracker");
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
					// View Albums and their trackers
					System.out.println("Your progress trackers and albums");
					System.out.println("----------------------------------------------------------------------------");

					viewAlbums(progList);

					System.out.println("\n");

					break;

				case 5:
					System.out.println("You have logged out\n");
					break;

				default:
					System.out.println("Invalid input, try again!\n");
					scan.nextLine();
				}
			} while (ans != 5);
			// System.exit(0);
		} catch (InputMismatchException e) {
			System.out.println("Invalid input, must enter a number");
		} catch (TrackingException e) {

		} catch (NoSuchElementException e) {
			System.out.println("Input was not recognized");
		}

	}

	public static void menu(User user) {

		System.out.println("\n==============================================");
		System.out.println("  Hello, " + user.getUsername());
		System.out.println("| Welcome to the Album Progress Tracker!     |");
		System.out.println("|                                            |");
		System.out.println("| Please choose from the following options:  |");
		System.out.println("|                                            |");
		System.out.println("| 1: Add Album                               |");
		System.out.println("| 2: Add Progress                            |");
		System.out.println("| 3: Update Progress                         |");
		System.out.println("| 4: List Albums                             |");
		System.out.println("| 5: LOGOUT                                  |");
		System.out.println("|                                            |");
		System.out.println("==============================================");
	}
	public static void progressMenu() {

		System.out.println("Please choose your progress:			  ");
		System.out.println("                                          ");
		System.out.println("6 - Not Started                           ");
		System.out.println("7 - In Progress                           ");
		System.out.println("8 - Completed                            \n");
	}
	public static void progressUpdateMenu() {

		System.out.println("Please choose your updated progress:      ");
		System.out.println("                                          ");
		System.out.println("6 - Not Started                           ");
		System.out.println("7 - In Progress                           ");
		System.out.println("8 - Completed                            \n");
	}

	public static void addAlbum(Scanner scan, AlbumDaoSql albumCaller) {
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
		}
		List<Album> albums = albumCaller.getAllAlbums();
		progList.forEach(progress -> {

			Album progressAlbum =
					albums.stream().filter(album -> album.getAlbum_id() == progress.getTrack_id())
					      .findFirst().get();
			if (progressAlbum.getAlbum_id() < 10)	System.out.printf("\n %s - %s -> %s", progressAlbum.getAlbum_id(),
					progressAlbum.getAlbum(), progress.getProgress());
			else System.out.printf("\n%s - %s -> %s", progressAlbum.getAlbum_id(),
					progressAlbum.getAlbum(), progress.getProgress());
			System.out.println("\nprogressAlbum.getRating() = " + progressAlbum.getRating());
		});
	}

}
