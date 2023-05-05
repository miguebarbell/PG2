package dao;

import connection.ConnectionManager;
import info.movito.themoviedbapi.*;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.tv.TvEpisode;
import info.movito.themoviedbapi.model.tv.TvSeason;
import info.movito.themoviedbapi.model.tv.TvSeries;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class AlbumDaoSql implements AlbumDao {

	private final Connection conn = ConnectionManager.getConnection();
	Properties props = new Properties();

	@Override
	public Album getAlbumId(int a_id) {

		try (PreparedStatement pstmt = conn.prepareStatement("select * from albums where album_id = ?")) {

			pstmt.setInt(1, a_id);

			ResultSet rs = pstmt.executeQuery();
			Album album = null;

			if (rs.next()) {
				int album_id = rs.getInt("album_id");
				String album_name = rs.getString("album");

				album = new Album(album_id, album_name);

			}

			rs.close();

			return album;

		} catch (SQLException e) {
			System.out.println("Could not find album id" + a_id);
		}

		return null;
	}

	@Override
	public List<Album> getAllAlbums() {

		List<Album> albList = new ArrayList<>();

		try (Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT * FROM albums")) {

			while (rs.next()) {
				int id = rs.getInt("album_id");

				String albumN = rs.getString("album");

				Album album = new Album(id, albumN);
				albList.add(album);

			}

		} catch (SQLException e) {
			System.out.println("Could not retrieve list of albums");
		}
		return albList;
	}

	@Override
	public Integer addAlbum(Album album) {

		try (PreparedStatement pstmt = conn.prepareStatement("INSERT into albums(album)values(?)", Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, album.getAlbum());

			int count = pstmt.executeUpdate();
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.next()) return generatedKeys.getInt(1);
		} catch (SQLException e) {
			System.out.println("TV SHOW: '%s' is already in the database".formatted(album.getAlbum()));
		}
		return 0;
	}

	@Override
	public boolean addByCode(Integer code) {
		SeasonDaoImpl seasonDao = new SeasonDaoImpl();
		TrackDaoImpl trackDao = new TrackDaoImpl();
		try {
			props.load(new FileInputStream("resources/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String key = props.getProperty("key");
		String url = "https://api.themoviedb.org/3/tv/%s?api_key=%s".formatted(code, key);
		TmdbApi tmdbApi = new TmdbApi(key);
		TvSeries newSerie = tmdbApi.getTvSeries().getSeries(code, "en");
		String name = newSerie.getOriginalName();
//		String description = newSerie.getOverview();
		List<TvSeason> seasons = newSerie.getSeasons();
			Integer newAlbumId = addAlbum(new Album(name));
			seasons.forEach(season -> {
//			String seasonOverview = season.getOverview();
				String seasonName = season.getName();
				int seasonNumber = season.getSeasonNumber();
				Integer newSeasonId = seasonDao.save(new Season(seasonName, newAlbumId));
				TvSeason result = tmdbApi.getTvSeasons().getSeason(code, seasonNumber, "en", TmdbTvSeasons.SeasonMethod.values());
				List<TvEpisode> episodes = result.getEpisodes();
				episodes.forEach(episode -> {
					int episodeNumber = episode.getEpisodeNumber();
					String episodeName = episode.getName();
					trackDao.save(new Track(episodeName, episodeNumber, newSeasonId));
				});
			});
			return true;
	}

	@Override
	public List<TvSerieDTO> searchByTitle(String title) {
		try {
			props.load(new FileInputStream("resources/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<TvSerieDTO> listOfResults = new ArrayList<>();
		TmdbApi tmdbApi = new TmdbApi(props.getProperty("key"));
		List<TvSeries> results = tmdbApi.getSearch().searchTv(title, "en", 1).getResults();
		results.forEach(tvSerie -> {
			int id = tvSerie.getId();
			String name = tvSerie.getOriginalName();
			String overview = tvSerie.getOverview();
//			List<Person> createdBy = tvSerie.getCreatedBy();
			String firstAirDate = tvSerie.getFirstAirDate();
//			List<String> originCountry = tvSerie.getOriginCountry();
			TvSeries serie = tmdbApi.getTvSeries().getSeries(id, "en");
			int numberOfEpisodes = serie.getNumberOfEpisodes();
			int numberOfSeasons = serie.getNumberOfSeasons();
			listOfResults.add(new TvSerieDTO(id,name, overview, firstAirDate, numberOfSeasons, numberOfEpisodes));
		});
		return listOfResults;
	}


	public Float getProgressByUserIdAndAlbumId(int userId, int albumId) {
		String sql = """
				SELECT album_id, SUM(total) as completed, SUM(number) as total, SUM(total) / SUM(number) as percentage
				FROM seasons
				INNER JOIN
				(SELECT season_id, COUNT(number) AS number, COUNT(q1.track_id) as total
				FROM tracks t
				LEFT JOIN
				(SELECT track_id
				from progress
				where user_id = ?
				and progress = 'completed') q1
				on q1.track_id = t.track_id
				GROUP BY season_id) j1
				ON seasons.season_id = j1.season_id
				WHERE album_id = ?
				GROUP BY album_id;
						""";
		try (PreparedStatement statement = conn.prepareStatement(sql)) {

			statement.setInt(1, userId);
			statement.setInt(2, albumId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getFloat(4);

			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public List<AlbumDTO> getRecommendations(Integer userId, Integer numberOfSuggestions) {
//		String getMoviesAlreadyRated = """
//				SELECT * FROM albums WHERE album_id IN
//				    (SELECT album_id FROM seasons WHERE season_id IN
//				    (SELECT season_id FROM tracks
//				    INNER JOIN ratings r on tracks.track_id = r.track_id AND r.user_id = ?))
//								""";
		String sqlTvShowsAlreadyRatedAndTheRated = """
				SELECT album, AVG(rating) as rating
				FROM ratings
				         INNER JOIN tracks t ON ratings.track_id = t.track_id AND ratings.user_id = ?
				         INNER JOIN seasons s ON t.season_id = s.season_id
				         INNER JOIN albums a on s.album_id = a.album_id
				GROUP BY album;
								""";
		List<AlbumDTO> showsRated = new ArrayList<>();
		try (PreparedStatement gmarStmt = conn.prepareStatement(sqlTvShowsAlreadyRatedAndTheRated)) {
			gmarStmt.setInt(1, userId);
			ResultSet resultSet = gmarStmt.executeQuery();
			while (resultSet.next()) {
				showsRated.add(new AlbumDTO(resultSet.getString(1), resultSet.getFloat(2), null, null));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return OpenAI.getRecommendations(showsRated, numberOfSuggestions);
	}


	Float getRatingByAlbumId(Integer albumId) {
		AtomicReference<Float> rating = new AtomicReference<>(0f);
		AtomicInteger count = new AtomicInteger();
		SeasonDaoImpl seasonDao = new SeasonDaoImpl();
		List<Season> seasonsByTvshowId = seasonDao.getSeasonsByTvshowId(albumId);
		seasonsByTvshowId.forEach(season -> {
			Float ratingBySeasonId = seasonDao.getRatingBySeasonId(season.getSeason_id());
			if (ratingBySeasonId != null) {
				rating.updateAndGet(v -> v + ratingBySeasonId);
				count.getAndIncrement();
			}
		});
		return count.get() > 0 ? rating.get() / count.get() : null;
	}

	List<AlbumDTO> getProgressByUserId(int userId) {
		List<AlbumDTO> result = new ArrayList<>();
		String sql = """
SELECT album, percentage
FROM albums
         INNER JOIN
     (SELECT album_id, SUM(total) as completed, SUM(number) as total, SUM(total) / SUM(number) as percentage
      FROM seasons
               INNER JOIN
           (SELECT season_id, COUNT(number) AS number, COUNT(q1.track_id) as total
            FROM tracks t
                     LEFT JOIN
                 (SELECT track_id
                  from progress
                  where user_id = ?
                    and progress = 'completed') q1
                 on q1.track_id = t.track_id
            GROUP BY season_id) j1
           ON seasons.season_id = j1.season_id
      GROUP BY album_id) j2
     ON j2.album_id = albums.album_id
								""";
		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setInt(1, userId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				result.add(new AlbumDTO(
						resultSet.getString(1),
						null, null,
						resultSet.getFloat(2)
				));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public List<AlbumCompletedDTO> getUsersCompleted(){
		List<AlbumCompletedDTO> result = new ArrayList<>();
		String query = "SELECT  "
				+ "    q1.album, q1.users_completed, q2.users_watching "
				+ "FROM "
				+ "    (SELECT  "
				+ "        a.album, users_completed "
				+ "    FROM "
				+ "        albums a "
				+ "    LEFT JOIN (SELECT  "
				+ "        a.album_id, COUNT(*) AS users_completed "
				+ "    FROM "
				+ "        (SELECT  "
				+ "        t2.user_id, "
				+ "            t1.album_id, "
				+ "            t2.episodes_watched, "
				+ "            t1.total_episodes "
				+ "    FROM "
				+ "        (SELECT  "
				+ "        a.album_id, COUNT(*) AS total_episodes "
				+ "    FROM "
				+ "        seasons s "
				+ "    INNER JOIN albums a ON s.album_id = a.album_id "
				+ "    INNER JOIN tracks t ON t.season_id = s.season_id "
				+ "    GROUP BY album_id) t1 "
				+ "    INNER JOIN (SELECT  "
				+ "        user_id, a.album_id, COUNT(*) AS episodes_watched "
				+ "    FROM "
				+ "        progress p "
				+ "    INNER JOIN tracks t ON p.track_id = t.track_id "
				+ "    INNER JOIN seasons s ON t.season_id = s.season_id "
				+ "    INNER JOIN albums a ON s.album_id = a.album_id "
				+ "    WHERE "
				+ "        p.progress = 'completed' "
				+ "    GROUP BY user_id , album_id) t2 ON t2.album_id = t1.album_id "
				+ "    WHERE "
				+ "        t2.episodes_watched = t1.total_episodes) t3 "
				+ "    INNER JOIN albums a ON a.album_id = t3.album_id "
				+ "    GROUP BY a.album_id) t4 ON a.album_id = t4.album_id) q1 "
				+ "        INNER JOIN "
				+ "    (SELECT  "
				+ "        a.album, users_watching "
				+ "    FROM "
				+ "        albums a "
				+ "    LEFT JOIN (SELECT  "
				+ "        a.album_id, COUNT(*) AS users_watching "
				+ "    FROM "
				+ "        (SELECT  "
				+ "        t3.user_id, a.album_id, COUNT(*) AS users_watching "
				+ "    FROM "
				+ "        (SELECT  "
				+ "        t2.user_id, "
				+ "            t1.album_id, "
				+ "            t2.episodes_watched, "
				+ "            t1.total_episodes "
				+ "    FROM "
				+ "        (SELECT  "
				+ "        a.album_id, COUNT(*) AS total_episodes "
				+ "    FROM "
				+ "        seasons s "
				+ "    INNER JOIN albums a ON s.album_id = a.album_id "
				+ "    INNER JOIN tracks t ON t.season_id = s.season_id "
				+ "    GROUP BY album_id) t1 "
				+ "    INNER JOIN (SELECT  "
				+ "        user_id, a.album_id, COUNT(*) AS episodes_watched "
				+ "    FROM "
				+ "        progress p "
				+ "    INNER JOIN tracks t ON p.track_id = t.track_id "
				+ "    INNER JOIN seasons s ON t.season_id = s.season_id "
				+ "    INNER JOIN albums a ON s.album_id = a.album_id "
				+ "    WHERE "
				+ "        p.progress = 'completed' "
				+ "    GROUP BY user_id , album_id) t2 ON t2.album_id = t1.album_id "
				+ "    WHERE "
				+ "        t2.episodes_watched < t1.total_episodes) t3 "
				+ "    INNER JOIN albums a ON a.album_id = t3.album_id "
				+ "    GROUP BY a.album_id , t3.user_id UNION SELECT  "
				+ "        t3.user_id, a.album_id, COUNT(*) AS users_watching "
				+ "    FROM "
				+ "        (SELECT  "
				+ "        t2.user_id, "
				+ "            t1.album_id, "
				+ "            t2.in_progress_episodes, "
				+ "            t1.total_episodes "
				+ "    FROM "
				+ "        (SELECT  "
				+ "        a.album_id, COUNT(*) AS total_episodes "
				+ "    FROM "
				+ "        seasons s "
				+ "    INNER JOIN albums a ON s.album_id = a.album_id "
				+ "    INNER JOIN tracks t ON t.season_id = s.season_id "
				+ "    GROUP BY album_id) t1 "
				+ "    INNER JOIN (SELECT  "
				+ "        user_id, a.album_id, COUNT(*) AS in_progress_episodes "
				+ "    FROM "
				+ "        progress p "
				+ "    INNER JOIN tracks t ON p.track_id = t.track_id "
				+ "    INNER JOIN seasons s ON t.season_id = s.season_id "
				+ "    INNER JOIN albums a ON s.album_id = a.album_id "
				+ "    WHERE "
				+ "        p.progress = 'not completed' "
				+ "            OR p.progress = 'in-progress' "
				+ "    GROUP BY user_id , album_id) t2 ON t2.album_id = t1.album_id) t3 "
				+ "    INNER JOIN albums a ON a.album_id = t3.album_id "
				+ "    GROUP BY a.album_id , t3.user_id) t4 "
				+ "    INNER JOIN albums a ON a.album_id = t4.album_id "
				+ "    GROUP BY a.album_id) t5 ON a.album_id = t5.album_id) q2 ON q1.album = q2.album";
		
		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				String album = rs.getString("album");
				int usersCompleted = rs.getInt("users_completed");
				int usersWatching = rs.getInt("users_watching");

				AlbumCompletedDTO ac = new AlbumCompletedDTO(album, usersCompleted, usersWatching);
				result.add(ac);

			}

		} catch (SQLException e) {
			System.out.println("Could not get the number of users who are watching or have completed watching a show");
		}
		return result;
	}
	
}
