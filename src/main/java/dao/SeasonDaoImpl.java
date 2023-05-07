package dao;

import connection.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SeasonDaoImpl implements SeasonDao {

	private final Connection conn = ConnectionManager.getConnection();

	@Override
	public Integer save(Season season) {
		String sql = "INSERT INTO seasons(show_id, title) VALUES (?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, season.getTvshow_id());
			ps.setString(2, season.getTitle());
			int rows = ps.executeUpdate();
			ResultSet generatedKeys = ps.getGeneratedKeys();
			return generatedKeys.next() ? generatedKeys.getInt(1) : 0;
		} catch (SQLException e) {
			return 0;
		}
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
		episodesBySeasonId.forEach(episode -> {
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
		String sql = "SELECT * FROM seasons WHERE show_id = ?";
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

	@Override
	public Float getProgressByUserIdAndSeasonId(int userId, int seasonId) {
		String sql = """
				SELECT q1.season_id as season_id, progress as number, total, progress/total as percentage
				FROM (SELECT season_id, COUNT(number) AS progress
							FROM tracks
							WHERE track_id IN
							(SELECT track_id from progress where user_id = ? and progress = 'completed')
							GROUP BY season_id) q1
								 INNER JOIN
						(SELECT season_id, COUNT(number) as total
						 FROM tracks
						 GROUP BY season_id) q2
						ON q1.season_id = q2.season_id
				WHERE q1.season_id = ?;
				""";
		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setInt(1, userId);
			statement.setInt(2,seasonId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getFloat(4);
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
}
