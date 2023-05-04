package runner;

import dao.*;
import utility.ConsoleColors;

import java.util.List;

import static runner.Runner.*;

public class ShowMenu {
    public static SeasonDaoImpl seasonDao = new SeasonDaoImpl();
    public static TrackDaoImpl trackDao = new TrackDaoImpl();
    public static ConsoleColors c;

    public static void addProgress() {

        int userId = Runner.user.getUser_id();

        List<Album> albList = Runner.albumCaller.getAllAlbums();

        int tvShowId = -1;
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
                tvShowId = Integer.parseInt(scan.nextLine().trim());
                if (tvShowId < -1) {
                    clear();
                    System.out.println(c.RED + "Invalid album ID. Try again." + c.RESET);
                }
            } catch (Exception e) {
                clear();
                System.out.println(c.RED + "Invalid album ID. Try again." + c.RESET);
            }

            System.out.print(c.RESET);
        } while (tvShowId <= 0);

        List<Season> seasons = seasonDao.getSeasonsByTvshowId(tvShowId);
        String seasonId;
        do {
            for (int i = 0; i < seasons.size(); i++) {
                System.out.printf("%s. %s%n", i + 1, seasons.get(i).getTitle());
            }
            System.out.println("Select your season");
            seasonId = scan.nextLine();
        } while (!AdminMenu.optionsListValidator(seasonId, seasons));
        String episodeId;
        List<Track> episodes = seasonDao.getEpisodesBySeasonId(seasons.get(Integer.parseInt(seasonId) - 1).getSeason_id());
        do {
            for (int i = 0; i < episodes.size(); i++) {
                System.out.printf("%s. %s%n", i + 1, episodes.get(i).getTitle());
            }
            System.out.println("Select your episode");
            episodeId = scan.nextLine();
        } while (!AdminMenu.optionsListValidator(episodeId, episodes));
        String progressChoice;
        String[] progressStatus = {"not completed", "in-progress", "completed", ""};

        Integer episodeIdToTrack = episodes.get(Integer.parseInt(episodeId) - 1).getId();
        progressMenu();

        int choice = 0;
        try {
            choice = Integer.parseInt(scan.nextLine().trim());
            if (choice < 0) {
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
                    Progress progressAdded = new Progress(userId,episodeIdToTrack, progressChoice);
                    boolean progressAddResult = progressCaller.addProgress(progressAdded);
                    if (progressAddResult) {
                        System.out.println(progressAdded);
                        System.out.println(c.GREEN + "Progress tracker successfully added" + c.RESET);
                    } else {
                        System.out.println(message);
//						throw new TrackingException(message);
                    }
                    stillChoosing = false;
                    break;
                case 7:
                    progressChoice = progressStatus[1];
                    Progress progressAdded2 = new Progress(userId, episodeIdToTrack, progressChoice);
                    boolean progressAddResult2 = progressCaller.addProgress(progressAdded2);
                    if (progressAddResult2) {
                        System.out.println(progressAdded2);
                        System.out.println(c.GREEN + "Progress tracker successfully added" + c.RESET);
                    } else {
                        System.out.println(message);
//						throw new TrackingException(message);
                    }
                    stillChoosing = false;
                    break;
                case 8:
                    progressChoice = progressStatus[2];
                    Progress progressAdded3 = new Progress(userId, episodeIdToTrack, progressChoice);
                    boolean progressAddResult3 = progressCaller.addProgress(progressAdded3);
                    if (progressAddResult3) {
                        System.out.println(progressAdded3);
                        System.out.println(c.GREEN + "Progress tracker successfully added" + c.RESET);
                    } else {
                        System.out.println(message);
//						throw new TrackingException(message);
                    }
                    stillChoosing = false;
                    break;

                case 0:
                    break;
                default:
                    System.out.println(message);
            }
        }

    }
}
