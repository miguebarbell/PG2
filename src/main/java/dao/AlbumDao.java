package dao;

import java.util.List;

public interface AlbumDao {

	Album getAlbumId(int a_id);

	List<Album> getAllAlbums();

	Integer addAlbum(Album album);

	boolean addByCode(Integer code);
}
