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
		int userId = user.getUser_id();
		List<Album> albList = albumDaoSql.getAllAlbums();
		int tvShowId = -1;
		do {
			System.out.println(c.CYAN + "ADD PROGRESS" + c.RESET);
			System.out.println("\nID  -  Title");
			albList.forEach(album -> {

				Float ratingByAlbumId = albumDaoSql.getRatingByAlbumId(album.getAlbum_id());
				String oneDecimalRating = ratingByAlbumId == null ? "---" : String.format("%.1f", ratingByAlbumId);
				if (album.getAlbum_id() < 10)
					System.out.printf(" %s.  [%s]  %s\n",
							album.getAlbum_id(),
							oneDecimalRating,
							album.getAlbum());
				else
					System.out.printf("%s.  [%s]  %s\n",
							album.getAlbum_id(),
							oneDecimalRating,
									album.getAlbum());
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
				Float ratingBySeasonId = seasonDao.getRatingBySeasonId(seasons.get(i).getSeason_id());
				System.out.printf("%s. [%s] %s%n",
						i + 1,
						ratingBySeasonId == null ? "---" : String.format("%.1f", ratingBySeasonId),
						seasons.get(i).getTitle());
			}
			System.out.println("Select your season");
			seasonId = scan.nextLine();
		} while (!AdminMenu.optionsListValidator(seasonId, seasons));
		String episodeId;
		List<Track> episodes = seasonDao.getEpisodesBySeasonId(seasons.get(Integer.parseInt(seasonId) - 1).getSeason_id());
		do {
			for (int i = 0; i < episodes.size(); i++) {
				Float ratingByTrackId = trackDao.getRatingByTrackId(episodes.get(i).getId());
				System.out.printf("%s. [%s] %s%n",
						i + 1,
						null == ratingByTrackId ? "---" : String.format("%.1f", ratingByTrackId),
						episodes.get(i).getTitle());
			}
			System.out.println("Select your episode");
			episodeId = scan.nextLine();
		} while (!AdminMenu.optionsListValidator(episodeId, episodes));
		String progressChoice;
		String[] progressStatus = {"not completed", "in-progress", "completed", ""};
		Integer episodeIdToTrack = episodes.get(Integer.parseInt(episodeId) - 1).getId();
		String progressOrRating;
		List<String> options = List.of("q", "1", "2");
		do {
			System.out.println("1. Add a progress.\n2. Add a rating.\nq to Cancel");
			progressOrRating = scan.nextLine();
		} while (!options.contains(progressOrRating));

		if (progressOrRating.equals("q")) return;
		else if (progressOrRating.equals("1")) {
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
						Progress progressAdded = new Progress(userId, episodeIdToTrack, progressChoice);
						boolean progressAddResult = progressDaoSql.addProgress(progressAdded);
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
						boolean progressAddResult2 = progressDaoSql.addProgress(progressAdded2);
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
						boolean progressAddResult3 = progressDaoSql.addProgress(progressAdded3);
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
		} else {
			System.out.println("ADD A RATING");
			String rating;
			List<String> ratingOptions = List.of("0", "1", "2", "3", "4", "q");
			do {
				System.out.println("0. Really Bad.\n1. Bad.\n2. Average.\n3. Good.\n4. The Best\nq to cancel.");
				rating = scan.nextLine();
			} while (!ratingOptions.contains(rating));
			if (rating.equals("q")) {
				System.out.println("Cancelling");
				return;
			}
			RatingType[] values = RatingType.values();
			trackDao.addRating(values[Integer.parseInt(rating)], episodeIdToTrack, userId);
		}
	}
}
