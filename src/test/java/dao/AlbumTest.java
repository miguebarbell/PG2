package dao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {

	@Test
	void getRating() {
		Album album = new Album("Metallica");
		album.addRating(Ratings.GOOD); //3
		album.addRating(Ratings.AVERAGE); //2
		assert album.getRating() == 2.5;
	}
}
