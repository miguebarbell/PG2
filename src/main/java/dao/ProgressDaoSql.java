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


     List<Progress> progList = new ArrayList<Progress>();

		try( Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM progress where user_id = " + u_id)){


			while(rs.next()) {
				int uid = rs.getInt("user_id");
				int aid = rs.getInt("album_id");
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

		try ( PreparedStatement pstmt = conn.prepareStatement("update progress set user_id = ?, album_id = ?, progress = ? where album_id = ?")) {

			pstmt.setInt(1, progress.getUser_id());
			pstmt.setInt(2, progress.getTrack_id());
			pstmt.setString(3, progress.getProgress());
			pstmt.setInt(4, progress.getTrack_id());

			int i = pstmt.executeUpdate();

			if(i > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean addProgress(Progress progress) {

		try( PreparedStatement pstmt = conn.prepareStatement(
				"insert into progress(user_id, track_id, progress)values(?,?,?)")){
			pstmt.setInt(1, progress.getUser_id());
			pstmt.setInt(2, progress.getTrack_id());
			pstmt.setString(3, progress.getProgress());

			int count = pstmt.executeUpdate();
			if(count > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("add progress failed");
		}
		return false;
	}

}
