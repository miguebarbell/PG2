package dao;

import org.junit.jupiter.api.Test;

import java.util.List;

class AlbumDaoSqlTest {

	AlbumDaoSql dao = new AlbumDaoSql();

	@Test
	void searchByTitle() {
		dao.searchByTitle("Mandalorian");
		dao.searchByTitle("How I meet your mother");
		dao.searchByTitle("Below Deck");
	}
	@Test
	void api() {
		dao.addByCode(60735);
		assert dao.getAllAlbums().stream().anyMatch(album -> album.getAlbum().equals("The Flash"));
	}
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
		System.out.println("dao.addAlbum(new Album()) = " + dao.addAlbum(new Album("www")));
	}

	@Test
	void getRatingByAlbumId() {
		System.out.println(dao.getRatingByAlbumId(1));
		System.out.println(dao.getRatingByAlbumId(2));
		System.out.println(dao.getRatingByAlbumId(3));
	}
}
