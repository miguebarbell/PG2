package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectionManager;

public class ProgressDaoSql implements ProgressDao {

	private final Connection conn = ConnectionManager.getConnection();

	@Override
	public List<Progress> getAllUserTrackers(int u_id) {


		List<Progress> progList = new ArrayList<>();

		try (Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT * FROM progress where user_id = " + u_id)) {


			while (rs.next()) {
				int uid = rs.getInt("user_id");
				int aid = rs.getInt("track_id");
				String prog = rs.getString("progress");


				Progress nProg = new Progress(uid, aid, prog);
				progList.add(nProg);

			}

			return progList;

		} catch (SQLException e) {
			System.out.println("Could not retrieve list of trackers for user");
		}
		return null;
	}

	@Override
	public boolean updateProgress(Progress progress) {

		try (PreparedStatement pstmt = conn.prepareStatement(
				"update progress set user_id = ?, track_id = ?, progress = ? where track_id = ?")) {

			pstmt.setInt(1, progress.getUser_id());
			pstmt.setInt(2, progress.getTrack_id());
			pstmt.setString(3, progress.getProgress());
			pstmt.setInt(4, progress.getTrack_id());

			int i = pstmt.executeUpdate();

			if (i > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean addProgress(Progress progress) {
		String queryForAnExistingProgress = "SELECT * FROM progress WHERE user_id = ? AND track_id = ?";
		String updateProgress = "update progress set progress = ? where user_id = ? and track_id = ?";
		String insertSql = "insert into progress(user_id, track_id, progress)values(?,?,?)";
		try (PreparedStatement pstmt = conn.prepareStatement(insertSql);
		     PreparedStatement qstmt = conn.prepareStatement(queryForAnExistingProgress);
		     PreparedStatement ustmt = conn.prepareStatement(updateProgress)) {
			qstmt.setInt(1, progress.getUser_id());
			qstmt.setInt(2, progress.getTrack_id());
			if (qstmt.executeQuery().next()) {
				//update
				ustmt.setString(1, progress.getProgress());
				ustmt.setInt(2, progress.getUser_id());
				ustmt.setInt(3, progress.getTrack_id());
				int rows = ustmt.executeUpdate();
				if (rows > 0) {
					return true;
				}
			}			else {
				pstmt.setInt(1, progress.getUser_id());
				pstmt.setInt(2, progress.getTrack_id());
				pstmt.setString(3, progress.getProgress());
				int count = pstmt.executeUpdate();
				if (count > 0) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("add progress failed");
		}
		return false;
	}

	public List<Album> getAllAlbumsWithTrackerByUserId(int userId) {
		ArrayList<Album> results = new ArrayList<>();
		String sql = """
				SELECT * FROM tvshows WHERE show_id IN
				(SELECT show_id FROM seasons WHERE season_id IN
				(SELECT distinct  season_id FROM tracks WHERE track_id IN (
						SELECT track_id from progress where user_id = ?)));
						""";
		try (	PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setInt(1,userId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				results.add(new Album(resultSet.getInt(1), resultSet.getString(2)));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return results;
	}
}
