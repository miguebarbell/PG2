package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectionManager;

public class AlbumDaoSql implements AlbumDao {

	private Connection conn = ConnectionManager.getConnection();

	@Override
	public Album getAlbumId(int a_id) {

		try (PreparedStatement pstmt = conn.prepareStatement("select * from albums where album_id = ?")) {

			pstmt.setInt(1, a_id);

			ResultSet rs = pstmt.executeQuery();
			Album album = null;

			if (rs.next()) {
				int album_id = rs.getInt("album_id");
				List<Ratings> ratings = getRatingByAlbumId(album_id);
				String album_name = rs.getString("album");


				album = new Album(album_id, album_name);
				ratings.forEach(album::addRating);


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
				List<Ratings> ratings = getRatingByAlbumId(id);

				String albumN = rs.getString("album");

				Album album = new Album(id, albumN);
				ratings.forEach(album::addRating);
				albList.add(album);

			}

		} catch (SQLException e) {
			System.out.println("Could not retrieve list of albums");
		}
		return albList;
	}

	@Override
	public boolean addAlbum(Album album) {

		try (PreparedStatement pstmt = conn.prepareStatement("INSERT into albums(album)values(?)")) {

			pstmt.setString(1, album.getAlbum());

			int count = pstmt.executeUpdate();

			if (count > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("album add failed");
		}
		return false;
	}

	@Override
	public boolean addRating(Ratings rating, Integer userId, Integer albumId) {
		String updateSql = "UPDATE ratings SET rating = ? WHERE user_id = ? AND album_id = ?";
		String searchSql = "SELECT * FROM ratings WHERE user_id = ? AND album_id = ?";
		String sql = "INSERT INTO ratings(user_id, rating, album_id) values (?,?,?)";
		try (
				PreparedStatement insertStmt = conn.prepareStatement(sql);
				PreparedStatement searchStmt = conn.prepareStatement(searchSql);
				PreparedStatement updateStmt = conn.prepareStatement(updateSql)
		) {
			searchStmt.setInt(1, userId);
			searchStmt.setInt(2, albumId);
			ResultSet searchSet = searchStmt.executeQuery();
			if (searchSet.next()) {
				// update
				updateStmt.setInt(1, rating.ordinal());
				updateStmt.setInt(2, userId);
				updateStmt.setInt(3, albumId);
				int rows = updateStmt.executeUpdate();
				if (rows > 0) {
					return true;
				}
			} else {
				insertStmt.setInt(1, userId);
				insertStmt.setInt(2, rating.ordinal());
				insertStmt.setInt(3, albumId);
				int count = insertStmt.executeUpdate();
				if (count > 0) {
					return true;
				}
			}
		} catch (SQLException e) {
			return false;
		}
		return false;
	}

	List<Ratings> getRatingByAlbumId(Integer albumId) {
		String sql = "SELECT * FROM ratings WHERE track_id = ?";
		List<Ratings> ratings = new ArrayList<>();
		try (
				PreparedStatement pstmt = conn.prepareStatement(sql)
		) {
			pstmt.setInt(1, albumId);
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				ratings.add(Ratings.values()[resultSet.getInt(3)]);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return ratings;
	}

}
