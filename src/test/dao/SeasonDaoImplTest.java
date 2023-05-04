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
		int save = dao.save(season);
		assertTrue(save != 0);
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
		System.out.println(ratingBySeasonId);
		assert ratingBySeasonId == 2.5;
		System.out.println("dao.getRatingBySeasonId(1) = " + dao.getRatingBySeasonId(1));
		System.out.println("dao.getRatingBySeasonId(2) = " + dao.getRatingBySeasonId(2));
	}

	@Test
	@DisplayName("Get the progress for a season")
	void testGetProgressForSeason() {
		Float user1season1 = dao.getProgressByUserIdAndSeasonId(1, 1);
		System.out.println(user1season1);
		assert user1season1 == 0.7f;
		Float user2season1 = dao.getProgressByUserIdAndSeasonId(2, 1);
		System.out.println(user2season1);
		assert user2season1 == 0.5f;
		Float user2season2 = dao.getProgressByUserIdAndSeasonId(2, 2);
		System.out.println(user2season2);
		assert user2season2 == null;
		Float user1season3 = dao.getProgressByUserIdAndSeasonId(1, 3);
		System.out.println(user1season3);
		assert user1season3 == 0.75f;
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
