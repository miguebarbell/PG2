package runner;

import dao.*;
import exceptions.LoginException;
import populator.Populator;
import utility.ConsoleColors;
import utility.ProgressBar;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static runner.ShowMenu.*;
import static runner.AdminMenu.*;

public class Runner {
	// fix static error, then get rid of this object
	public static ConsoleColors c;
	public static ProgressDaoSql progressDaoSql = new ProgressDaoSql();
	public static Scanner scan = new Scanner(System.in);
	public static User user = null;
	public static AlbumDaoSql albumDaoSql = new AlbumDaoSql();

	public static void run(String[] args) {

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
				System.out.println(c.RED + "Invalid input. Please try again." + c.RESET);
				System.out.print(loginMenu);
				continue;
			}

			switch (ans.charAt(0)) {
				case '1':
					System.out.println(c.GREEN_BOLD + "\nPlease log in!" + c.RESET);
					System.out.print("username:" + c.YELLOW);
					try {
						String username = scan.nextLine();
						System.out.print(c.RESET + "password:" + c.YELLOW);
						String password = scan.nextLine();
						System.out.println(c.RESET);

						User loggedUser = new User(username, password);
						user = userCaller.loginUser(loggedUser);
						if (user != null) {
							// call menu function
							clear();
							System.out.println(c.GREEN + "You have successfully logged in " + user.getUsername()
							                   + "!" + c.RESET);
//						try {
							loggedMenu();
							System.out.print(loginMenu);
//						}
//						catch (TrackingException e) {
//							e.printStackTrace();
//						}
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
				case '2':
					System.out.println(c.YELLOW + "\nPlease try to use a unique username and a difficult password.\n"
					                   + c.RESET + "We store your password with MD5 message-digest algorithm, 128bit hash value.");
					System.out.print("\nusername:" + c.YELLOW);
					String newUsername = scan.nextLine();
					System.out.print(c.RESET + "password:" + c.YELLOW);
					String password = scan.nextLine();
					System.out.print(c.RESET);

					boolean result = userCaller.createUser(newUsername, password);
					if (result) {
						System.out.println(c.GREEN + "\nUser " + newUsername + " created successfully." + c.RESET);
					} else {
						System.out.println(c.RED + "\nError, try again with another username." + c.RESET);
					}
					scan.nextLine();
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

	public static void clear() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 50; i++)
			sb.append("\n");
		String banner = c.PURPLE_BOLD + c.WHITE_BACKGROUND + "\r\n"
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

	public static void loggedMenu() {
		//2 separate menus for user and admin
		//chooseOption() will remerge the two

		boolean doLoop = true;
		int ans = 0;
		do {
			if(user == null) return;
			if (user.getUserType() == UserTypes.USER)
				printUserMenu(user);
			else
				printAdminMenu(user);

			try {
				ans = Integer.parseInt(scan.nextLine().trim());
			} catch (NumberFormatException e) {
				System.out.println(c.RED + "Input must be a valid integer" + c.RESET);
			}
			System.out.print(c.RESET);

			if (user.getUserType() == UserTypes.USER) {
				if (ans >= 1 && ans <= 4) { //valid
					chooseOption(ans + 1);
				}
				if (ans == 4) {
					doLoop = false; //log out
					user = null;
				}
			} else {
				if (ans >= 1 && ans <= 5) { //valid
					chooseOption(ans);
				}
				if (ans == 5) {
					doLoop = false; //log out
					user = null;
				}
			}
			ans = 0;
		} while (doLoop);

	}

	public static void chooseOption(int ans) {

		clear();
		switch (ans) {
			case 0:
				break;
			case 1:
				automatic(); //AdminMenu.java
				break;

			case 2:
				addProgress(); //ShowMenu.java
				break;

			case 3: //Make sure to use a valid API key in OpenAI.java
				List<AlbumDTO> recommendations = albumDaoSql.getRecommendations(user.getUser_id(), 5);
				System.out.println("We asked ChatGPT for a recommendation of 5 shows based on the ratings that you currently have.");
				recommendations.forEach(albumDTO -> {
					System.out.printf("%s. %s%n", albumDTO.recommendation(), albumDTO.title());
				});
				scan.nextLine();
				break;

			case 4:
				// Showing the show and number of users who have finished watching a show and still watching a show.
				List<AlbumCompletedDTO> usersCompletedOrInProgress = albumDaoSql.getUsersCompleted();

				System.out.println(ConsoleColors.CYAN_BOLD + "Global User Data" + ConsoleColors.RESET);
				System.out.printf("%-30s | %-6s | %-22s | %-16s\n", "Title", "Rating", ConsoleColors.YELLOW + "Watching (In Progress)" + ConsoleColors.RESET, ConsoleColors.GREEN + "Completed" + ConsoleColors.RESET);
				System.out.println("----------------------------------------------------------------------------");


				for(AlbumCompletedDTO ac: usersCompletedOrInProgress) {
					StringBuilder title = new StringBuilder(ac.album());
					title.setLength(30);
					String showRating = albumDaoSql.getRatingByAlbumId(ac.showId()) == null ? "----" : String.valueOf(Math.ceil(albumDaoSql.getRatingByAlbumId(ac.showId())*100)/100);
					System.out.printf("%-30s | [%-4s] | %s%-22s%s | %s%-16s%s\n", title.toString().replace('\u0000', ' '), showRating,
							ConsoleColors.YELLOW, ac.usersWatching(), ConsoleColors.RESET, ConsoleColors.GREEN, ac.usersCompletd(), ConsoleColors.RESET);
				}
				System.out.println(ConsoleColors.RESET);



				String banner;
				List<Album> albumsWithProgress = progressDaoSql.getAllAlbumsWithTrackerByUserId(user.getUser_id());
				if (albumsWithProgress.isEmpty()) {
					banner = ConsoleColors.RED + "You don't have any episode completed yet" + ConsoleColors.RESET;
				} else {
					banner = ConsoleColors.CYAN_BOLD + "Displaying " + user.getUsername() + "'s progress by Show and Season" + ConsoleColors.RESET;
				}
				System.out.println(banner);
				albumsWithProgress.forEach(album -> {
					StringBuilder title = new StringBuilder(album.getAlbum());
					title.setLength(30);
					String showRating = albumDaoSql.getRatingByAlbumId(album.getAlbum_id()) == null ? "----" : String.valueOf(Math.ceil(albumDaoSql.getRatingByAlbumId(album.getAlbum_id())*100)/100);
					System.out.printf("[%-4s] | %-30s\n", showRating, title.toString().replace('\u0000', ' '));
					System.out.println("----------------------------------------------------------------------------");
//					System.out.println("-------------------------------------------------------------------");
					List<Season> seasonsByTvshowId = seasonDao.getSeasonsByTvshowId(album.getAlbum_id());
					seasonsByTvshowId.forEach(season -> {
						Float progressBySeason =
								seasonDao.getProgressByUserIdAndSeasonId(user.getUser_id(), season.getSeason_id());

						if (null != progressBySeason) {
							String seasonRating = seasonDao.getRatingBySeasonId(season.getSeason_id()) == null ? "----" : String.valueOf(Math.ceil(seasonDao.getRatingBySeasonId(season.getSeason_id())*100)/100);
							if(progressBySeason == 1)
								System.out.printf("[%-4s] | %s%-15s -> %s%s\n", seasonRating, ConsoleColors.GREEN, season.getTitle(), ProgressBar.progressBar((int)(progressBySeason * 100), 100), ConsoleColors.RESET);
							else
								System.out.printf("[%-4s] | %-15s -> %s\n", seasonRating, season.getTitle(), ProgressBar.progressBar((int)(progressBySeason * 100), 100));
						}
					});
					System.out.println(ConsoleColors.CYAN + "\n\n" + ConsoleColors.RESET);
				});

				scan.nextLine();
				break;

			case 5:
				System.out.println("You have logged out\n");
				break;

			default:
				System.out.println("Invalid input, try again!\n");
		}
	}

	public static void printUserMenu(User user) {
		clear();

		System.out.println(c.CYAN + "Hello, " + user.getUsername() + c.RESET);
		System.out.println("1: Add/Update Progress/Rating");
		System.out.println("2: Recommend me a show");
		System.out.println("3: List Albums");
		System.out.println("4: Logout");
		System.out.print("Please choose an option (1-4):" + c.YELLOW);
	}

	public static void printAdminMenu(User user) {
		clear();

		System.out.println(c.CYAN + "Hello, " + user.getUsername() + c.RESET);
		System.out.println("You are logged in as an " + c.WHITE_UNDERLINED + "admin." + c.RESET);
		System.out.println(c.WHITE_BOLD + "1: Add Show" + c.RESET);
		System.out.println("2: Add/Update Progress/Rating");
		System.out.println("3: Recommend me a show");
		System.out.println("4: List Shows");
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

			if (progressAlbum == null) return;

			if (progressAlbum.getAlbum_id() < 10) System.out.printf("\n %s - %s -> %s", progressAlbum.getAlbum_id(),
					progressAlbum.getAlbum(), progress.getProgress());
			else System.out.printf("\n%s - %s -> %s", progressAlbum.getAlbum_id(),
					progressAlbum.getAlbum(), progress.getProgress());
			System.out.println("\nRating = " + progressAlbum.getRating());
		});
	}

}
