package runner;

import java.util.List;

import dao.TvSerieDTO;
import utility.ConsoleColors;

public class AdminMenu {
	public static boolean blankValidator(String input, String message) {
		if (input.isBlank()) {
			System.out.println(message);
			return true;
		} else {
			return false;
		}
	}

	public static boolean optionsListValidator(String tvTitleChoice, List<?> results) {
		if (tvTitleChoice.equals("q"))
			return true;
		try {
			results.get(Integer.parseInt(tvTitleChoice) - 1);
			return true;
		} catch (Exception e) {
			System.out.println("Bad option");
			return false;
		}
	}

	public static void manually() {

	}

	public static void automatic() {
		String newTvShowTitle;
		do {
			System.out.print("Search Title (q to exit):" + ConsoleColors.YELLOW);
			newTvShowTitle = Runner.scan.nextLine();
			System.out.print(ConsoleColors.RESET);
			if (newTvShowTitle.equals("q"))
				return;
		} while (blankValidator(newTvShowTitle, "Bad title"));
		List<TvSerieDTO> results = Runner.albumDaoSql.searchByTitle(newTvShowTitle);
		String tvTitleChoice;
		do {
			Runner.clear();
			System.out.printf("%s%-4s%s | %-30s | %-10s | %-10s | %-16s\n", ConsoleColors.CYAN, "ID", ConsoleColors.RESET, "Title", "Date", "Seasons", "Episodes");
			System.out.println("--------------------------------------------------------------------------");
			for (int i = 0; i < results.size(); i++) {
				StringBuilder name = new StringBuilder(results.get(i).name());
				name.setLength(30);
				System.out.printf("%s%-4s%s | %-30s | %-10s | %-10s | %-16s\n", ConsoleColors.CYAN, i + 1, ConsoleColors.RESET, name.toString().replace('\u0000', ' '),
						results.get(i).firstAirDate(), results.get(i).numberOfSeasons(), results.get(i).numberOfEpisodes());
			}
			System.out.print("\nSelect ID to Add (q to exit):" + ConsoleColors.YELLOW);
			tvTitleChoice = Runner.scan.nextLine();
			System.out.print(ConsoleColors.RESET);
		} while (!optionsListValidator(tvTitleChoice, results));
		if (tvTitleChoice.equals("q")) {
			Runner.loggedMenu();
			return;
		}
		TvSerieDTO tvserie = results.get(Integer.parseInt(tvTitleChoice) - 1);
		Runner.albumDaoSql.addByCode(tvserie.id());
		System.out.println("Added %s to the database".formatted(tvserie.name()));
//      try {
//          Thread.sleep(1500);
//      } catch (InterruptedException e) {
//          System.out.println("Interrupted");
//      }
		Runner.scan.nextLine();
	}
}
