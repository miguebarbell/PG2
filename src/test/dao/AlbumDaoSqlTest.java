package dao;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlbumDaoSqlTest {

	AlbumDaoSql dao = new AlbumDaoSql();

	@Test
	void getAlbumId() {
		Album beef = dao.getAlbumId(1);
		assert (beef.getAlbum().equals("BEEF"));
	}

	@Test
	void getAllAlbums() {
		List<Album> allAlbums = dao.getAllAlbums();
		assert (allAlbums
				        .stream()
				        .filter(album -> album.getAlbum().equals("BEEF"))
				        .count() == 1);

		assert (allAlbums
				        .stream()
				        .filter(album -> album.getAlbum().equals("The Boys"))
				        .count() == 1);
	}

	@Test
	void addAlbum() {

	}

	@Test
	void getRatingByAlbumId() {
		System.out.println(dao.getRatingByAlbumId(1));
		System.out.println(dao.getRatingByAlbumId(2));
		System.out.println(dao.getRatingByAlbumId(3));
	}
}
