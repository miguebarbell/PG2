package dao;

import connection.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SeasonDaoImpl implements SeasonDao {

	private Connection conn = ConnectionManager.getConnection();

	@Override
	public boolean save(Season season) {
		String sql = "INSERT INTO seasons(album_id, title) VALUES (?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, season.getTvshow_id());
			ps.setString(2, season.getTitle());
			int rows = ps.executeUpdate();
			if (rows > 0) {
				return true;
			}
		} catch (SQLException e) {
			return false;
		}
		return false;
	}

	@Override
	public List<Track> getEpisodesBySeasonId(int seasonId) {
		String sql = "SELECT * FROM tracks WHERE season_id = ?";
		List<Track> tracks = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, seasonId);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				int newEpisodeId = resultSet.getInt(1);
				int newSeasonId = resultSet.getInt(2);
				int newNumber = resultSet.getInt(3);
				String newSeasonTitle = resultSet.getString(4);
				tracks.add(new Track(newEpisodeId, newSeasonTitle, newNumber, newSeasonId));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return tracks;
	}

	@Override
	public Float getRatingBySeasonId(int seasonId) {
		AtomicReference<Float> rating = new AtomicReference<>(0.0f);
		AtomicInteger count = new AtomicInteger(0);
		TrackDaoImpl trackDao = new TrackDaoImpl();
		List<Track> episodesBySeasonId = getEpisodesBySeasonId(seasonId);
		episodesBySeasonId.stream().forEach(episode -> {
					Float ratingByTrackId = trackDao.getRatingByTrackId(episode.getId());
					if (ratingByTrackId != null) {
						count.incrementAndGet();
						rating.updateAndGet(v -> v + ratingByTrackId);
					}
				}
		);
		return count.get() > 0 ? rating.get() / count.get() : null;
	}

	@Override
	public List<Season> getSeasonsByTvshowId(int tvShowId) {
		String sql = "SELECT * FROM seasons WHERE album_id = ?";
		List<Season> seasons = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, tvShowId);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				int newSeasonId = resultSet.getInt(1);
				int newSeasonTvshowId = resultSet.getInt(2);
				String newSeasonTitle = resultSet.getString(3);
				seasons.add(new Season(newSeasonTitle, newSeasonTvshowId, newSeasonId));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return seasons;
	}
}
