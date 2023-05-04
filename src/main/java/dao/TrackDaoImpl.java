package dao;

import connection.ConnectionManager;

import java.sql.*;

public class TrackDaoImpl implements TrackDao {

	private final Connection conn = ConnectionManager.getConnection();

	@Override
	public Float getRatingByTrackId(int trackId) {
		String sql = "SELECT rating FROM ratings WHERE track_id = ?";
		Float rating = 0f;
		int count = 0;
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, trackId);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				rating += resultSet.getInt(1);
				count++;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return count > 0 ? rating / count : null;
	}


	@Override
	public boolean addRating(RatingType rating, int trackId, int userId) {
		String queryForAndExistingRating = "SELECT * from ratings WHERE track_id = ? AND user_id = ?";
		String sql = "INSERT INTO ratings(user_id, rating, track_id) VALUES (?, ?,?)";
		String updateSql = "UPDATE ratings SET rating = ? WHERE track_id = ? AND user_id = ?";
		try (PreparedStatement qps = conn.prepareStatement(queryForAndExistingRating);
		     PreparedStatement ps = conn.prepareStatement(sql);
		     PreparedStatement ustmt = conn.prepareStatement(updateSql)) {
			qps.setInt(1, trackId);
			qps.setInt(2, userId);
			ResultSet resultSet = qps.executeQuery();
			if (resultSet.next()) {
				ustmt.setInt(1, rating.ordinal());
				ustmt.setInt(2, trackId);
				ustmt.setInt(3, userId);
				int rows = ustmt.executeUpdate();
				return rows > 0;
			} else {
				ps.setInt(1, userId);
				ps.setInt(2, rating.ordinal());
				ps.setInt(3, trackId);
				int rows = ps.executeUpdate();
				return rows > 0;
			}
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public Integer save(Track track) {
		String sql = "INSERT INTO tracks(season_id, number, title) values (?,?,?)";
		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, track.getSeasonID());
			ps.setInt(2, track.getNumber());
			ps.setString(3, track.getTitle());
			int rows = ps.executeUpdate();
			ResultSet generatedKeys = ps.getGeneratedKeys();
			return generatedKeys.next() ? generatedKeys.getInt(1) : 0;
		} catch (SQLException e) {
			return 0;
		}
	}

}
