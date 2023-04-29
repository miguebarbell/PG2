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
			listOfResults.add(new TvSerieDTO(name, overview, firstAirDate, numberOfSeasons, numberOfEpisodes));
		});
		return listOfResults;
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
}
