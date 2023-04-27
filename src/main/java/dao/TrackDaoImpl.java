package dao;

import connection.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrackDaoImpl implements TrackDao {

	private final Connection conn = ConnectionManager.getConnection();

	@Override
	public Integer getRatingByTrackId(int trackId) {
		String sql = "SELECT rating FROM ratings WHERE track_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, trackId);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}


		@Override
	public boolean addRating(RatingType rating, int trackId, int userId) {
		//FIXME: update rating if the user already has one for the same track
		String sql = "INSERT INTO ratings(user_id, rating, track_id) VALUES (?, ?,?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.setInt(2, rating.ordinal());
			ps.setInt(3, trackId);
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
	public boolean save(Track track) {
		String sql = "INSERT INTO tracks(season_id, number, title) values (?,?,?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, track.getSeasonID());
			ps.setInt(2, track.getNumber());
			ps.setString(3, track.getTitle());
			int rows = ps.executeUpdate();
			if (rows > 0) {
				return true;
			}
		} catch (SQLException e) {
			return false;
		}
		return false;
	}
}
