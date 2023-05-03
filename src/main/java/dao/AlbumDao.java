package dao;

import java.util.List;

public interface AlbumDao {

	Album getAlbumId(int a_id);

	List<Album> getAllAlbums();

	boolean addAlbum(Album album);
	Float getProgressByUserIdAndAlbumId(int userId, int albumId);
}
