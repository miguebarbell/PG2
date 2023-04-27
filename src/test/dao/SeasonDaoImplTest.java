package dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeasonDaoImplTest {

	SeasonDaoImpl dao = new SeasonDaoImpl();
	TrackDaoImpl trackDao = new TrackDaoImpl();

	@Test
	@Order(1)
	@DisplayName("Add new Season to Beef, tv show id 1")
	void save() {
		Season season = new Season("Season 2", 1);
		boolean save = dao.save(season);
		assertTrue(save);
	}

	@Test
	void getEpisodesBySeasonId() {
		List<Track> episodesBySeasonId = dao.getEpisodesBySeasonId(1);
		episodesBySeasonId.forEach(track -> System.out.println(track.getTitle()));
	}

	@Test
	@DisplayName("Give and retrieve rating for season id 3 (Season 2 The Boys")
	void getRatingBySeasonId() {
		trackDao.addRating(RatingType.AVERAGE, 20, 1);
		trackDao.addRating(RatingType.GOOD, 21, 1);
		Float ratingBySeasonId = dao.getRatingBySeasonId(3);
		assert ratingBySeasonId == 2.5;
	}

	@Test
	@Order(2)
	@DisplayName("Retrieve the new seasons from BEEF")
	void getSeasonsByTvshowId() {
		List<Season> seasonsByTvshowId = dao.getSeasonsByTvshowId(1);
		assertTrue(seasonsByTvshowId.size() > 1);
		seasonsByTvshowId.forEach(season ->
				assertEquals(1, (int) season.getTvshow_id()));
	}
}
