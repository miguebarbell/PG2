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
		Integer ratingByTrackId = dao.getRatingByTrackId(1);
		assert ratingByTrackId == 3;
	}

	@Test
	@Order(1)
	@DisplayName("Adding rating of GOOD to episode id 1")
	void addRating() {
		boolean ratingBolean = dao.addRating(RatingType.GOOD, 1, 1);
		assertTrue(ratingBolean);
	}

	@Test
	void save() {
		Track track = new Track("This is a new Episode in BEEF Season 1", 11, 1);
		boolean save = dao.save(track);
		assertTrue(save);
	}
}
