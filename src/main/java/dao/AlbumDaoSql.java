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
		
      try( PreparedStatement pstmt = conn.prepareStatement("select * from albums where album_id = ?")){
			
			pstmt.setInt(1, a_id);
			
			ResultSet rs = pstmt.executeQuery();
			Album album = null;
			
			if(rs.next()) {
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
		
		List<Album> albList = new ArrayList<Album>();
		
		try( Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM albums");){
			
			while(rs.next()) {
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
	public boolean addAlbum(Album album) {
		
		try( PreparedStatement pstmt = conn.prepareStatement("INSERT into albums(album)values(?)")){
			
			pstmt.setString(1, album.getAlbum());
			
			int count = pstmt.executeUpdate();
			
			if(count > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("album add failed");
		}
		return false;
	}

}
