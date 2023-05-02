package dao;

import org.junit.jupiter.api.Test;

import java.util.List;

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
	@Test
	void recommendations() {
		List<AlbumRankingDTO> recommendations = dao.getRecomendations(1, 5);
		recommendations.forEach(recomendation -> {
			System.out.printf("%s. %s%n", recomendation.recommendation(), recomendation.title());
		});
		assert recommendations.get(0).recommendation().equals(1);
	}
}
