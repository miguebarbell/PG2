package dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrackDaoImplTest {
	TrackDaoImpl dao = new TrackDaoImpl();

	@Test
	@Order(2)
	@DisplayName("Rating for episode id 1 should be 3")
	void getRatingByTrackId() {
		Float ratingByTrackId = dao.getRatingByTrackId(1);
		assert ratingByTrackId == 3;
	}

	@Test
	@Order(1)
	@DisplayName("Adding rating of GOOD to episode id 1")
	void addRating() {
		boolean ratingForId1= dao.addRating(RatingType.GOOD, 1, 1);
		boolean ratingForId29 = dao.addRating(RatingType.BAD, 29, 1);
		boolean ratingForId30 = dao.addRating(RatingType.GOOD, 30, 1);
		boolean ratingForId31 = dao.addRating(RatingType.REALLY_BAD, 31, 1);
		boolean ratingForId1User2
				= dao.addRating(RatingType.BAD, 1, 2);
		assertTrue(ratingForId30);
		assertTrue(ratingForId1User2);
		assertTrue(ratingForId1);
		assertTrue(ratingForId29);
		assertTrue(ratingForId31);

	}

	@Test
	void save() {
		Track track = new Track("This is a new Episode in BEEF Season 1", 11, 1);
		boolean save = dao.save(track);
		assertTrue(save);
	}
}
