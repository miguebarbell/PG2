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
		TrackDaoImpl trackDao = new TrackDaoImpl();
		return (float) getEpisodesBySeasonId(seasonId)
				.stream()
				.map(episode -> trackDao.getRatingByTrackId(episode.getId()))
				.filter(Objects::nonNull)
				.mapToInt(Integer::intValue)
				.average()
				.orElse(0.0f);
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
