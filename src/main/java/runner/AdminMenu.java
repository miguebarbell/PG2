package runner;

import dao.TvSerieDTO;

import java.util.List;

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
        if (tvTitleChoice.equals("q")) return true;
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
            System.out.println("Please enter the title to search, write q to exit");
            newTvShowTitle = Runner.scan.nextLine();
            if (newTvShowTitle.equals("q")) return;
        }
        while (blankValidator(newTvShowTitle, "Bad title"));
        List<TvSerieDTO> results = Runner.albumCaller.searchByTitle(newTvShowTitle);
        String tvTitleChoice;
        do {
            for (int i = 0; i < results.size(); i++) {
                System.out.printf("%s. %s (%s), %s Seasons %sEpisodes%n", i + 1,
                        results.get(i).name(),
                        results.get(i).firstAirDate(),
                        results.get(i).numberOfSeasons(),
                        results.get(i).numberOfEpisodes());
            }
            System.out.println("Select your title to add");
            tvTitleChoice = Runner.scan.nextLine();
        } while (!optionsListValidator(tvTitleChoice, results));
        if (tvTitleChoice.equals("q")) {
            Runner.loggedMenu();
            return;
        }
        TvSerieDTO tvserie = results.get(Integer.parseInt(tvTitleChoice) - 1);
        Runner.albumCaller.addByCode(tvserie.id());
        System.out.println("Added %s to the database".formatted(tvserie.name()));
      try {
          Thread.sleep(1500);
      } catch (InterruptedException e) {
          System.out.println("Interrupted");
      }
    }
}
