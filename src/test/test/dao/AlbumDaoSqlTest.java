package dao;

import org.junit.jupiter.api.Test;

class AlbumDaoSqlTest {

	@Test
	void addRating() {
		AlbumDaoSql albumDaoSql = new AlbumDaoSql();
		System.out.println("albumDaoSql.addRating(Ratings.AVERAGE, 1, 1) = " + albumDaoSql.addRating(Ratings.GOOD, 1, 1));
		System.out.println("albumDaoSql.addRating(Ratings.AVERAGE, 1, 2) = " + albumDaoSql.addRating(Ratings.AVERAGE, 1, 2));
	}
}
