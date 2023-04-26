package dao;

import org.junit.jupiter.api.Test;

class AlbumTest {

	@Test
	void getRating() {
		AlbumDaoSql albumDaoSql = new AlbumDaoSql();
		int idAlbumToTest = 1;
		albumDaoSql.addRating(Ratings.AVERAGE, 1, idAlbumToTest);
		albumDaoSql.addRating(Ratings.BAD, 2, idAlbumToTest);
		Album album = albumDaoSql.getAlbumId(idAlbumToTest);
		assert 1.5 == album.getRating();
		albumDaoSql.addRating(Ratings.THE_BEST, 2, idAlbumToTest);
		album = albumDaoSql.getAlbumId(idAlbumToTest);
		assert 3 == album.getRating();


	}
}
