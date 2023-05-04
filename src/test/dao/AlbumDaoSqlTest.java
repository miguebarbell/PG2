package dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class AlbumDaoSqlTest {

	AlbumDaoSql dao = new AlbumDaoSql();

	@Test
	void searchByTitle() {
		String firstSearch = "Mandalorian";
		dao.searchByTitle(firstSearch).forEach(title -> {
			assert title.name().contains(firstSearch);
		});
		String secondSearch = "How I meet your mother";
		dao.searchByTitle(secondSearch).forEach(title -> {
			assert title.name().contains(secondSearch);
		});
		String thirdSearch = "Below Deck";
		dao.searchByTitle(thirdSearch).forEach(title -> {
			assert title.name().contains(thirdSearch);
		});
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

	@Test
	void recommendations() {
		List<AlbumDTO> recommendations = dao.getRecommendations(1, 5);
		recommendations.forEach(recomendation -> {
			System.out.printf("%s. %s%n", recomendation.recommendation(), recomendation.title());
		});
		assert recommendations.get(0).recommendation().equals(1);
	}

	@Test
	@DisplayName("Get progress for albums")
	void testGetProgress() {
		Float user1album1 = dao.getProgressByUserIdAndAlbumId(1, 1);
		System.out.println(user1album1);
		assert user1album1 == 0.7f;
		Float user1album2 = dao.getProgressByUserIdAndAlbumId(1, 2);
		System.out.println(user1album2);
		assert user1album2 == 0.25f;
		Float user1album3 = dao.getProgressByUserIdAndAlbumId(1, 3);
		System.out.println(user1album3);
		assert user1album3 == null;
		Float user2album1 = dao.getProgressByUserIdAndAlbumId(2, 1);
		System.out.println(user2album1);
		assert user2album1 == 0.5f;
	}

	@Test
	@DisplayName("Get the progress of all the albums")
	void testGetAllProgressByUser() {
		List<AlbumDTO> user1 = dao.getProgressByUserId(1);
		List<AlbumDTO> user2 = dao.getProgressByUserId(2);
		assert user1.get(0).title().equals("BEEF");
		assert user1.get(0).progress() == 0.7f;
		assert user2.get(0).title().equals("BEEF");
		assert user2.get(0).progress() == 0.5f;
		assert user1.get(1).title().equals("The Boys");
		assert user1.get(1).progress() == 0.25f;
		assert user2.get(1).title().equals("The Boys");
		assert user2.get(1).progress() == 0f;
	}
}
