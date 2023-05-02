package dao;

import connection.ConnectionManager;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AlbumDaoSql implements AlbumDao {

	private final Connection conn = ConnectionManager.getConnection();

	@Override
	public Album getAlbumId(int a_id) {

		try (PreparedStatement pstmt = conn.prepareStatement("select * from albums where album_id = ?")) {

			pstmt.setInt(1, a_id);

			ResultSet rs = pstmt.executeQuery();
			Album album = null;

			if (rs.next()) {
				int album_id = rs.getInt("album_id");
				String album_name = rs.getString("album");


				album = new Album(album_id, album_name);


			}

			rs.close();

			return album;

		} catch (SQLException e) {
			System.out.println("Could not find album id" + a_id);
		}

		return null;
	}

	@Override
	public List<Album> getAllAlbums() {

		List<Album> albList = new ArrayList<>();

		try (Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT * FROM albums")) {

			while (rs.next()) {
				int id = rs.getInt("album_id");

				String albumN = rs.getString("album");

				Album album = new Album(id, albumN);
				albList.add(album);

			}

		} catch (SQLException e) {
			System.out.println("Could not retrieve list of albums");
		}
		return albList;
	}

	@Override
	public boolean addAlbum(Album album) {

		try (PreparedStatement pstmt = conn.prepareStatement("INSERT into albums(album)values(?)")) {

			pstmt.setString(1, album.getAlbum());

			int count = pstmt.executeUpdate();

			if (count > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("album add failed");
		}
		return false;
	}


	@Override
	public Float getProgressByUserIdAndAlbumId(int userId, int albumId) {
		String sql = """
				SELECT album_id, SUM(total) as completed, SUM(number) as total, SUM(total) / SUM(number) as percentage
				FROM seasons
				INNER JOIN
				(SELECT season_id, COUNT(number) AS number, COUNT(q1.track_id) as total
				FROM tracks t
				LEFT JOIN
				(SELECT track_id
				from progress
				where user_id = ?
				and progress = 'completed') q1
				on q1.track_id = t.track_id
				GROUP BY season_id) j1
				ON seasons.season_id = j1.season_id
				WHERE album_id = ?
				GROUP BY album_id;
						""";
		try (PreparedStatement statement = conn.prepareStatement(sql)) {

			statement.setInt(1, userId);
			statement.setInt(2, albumId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getFloat(4);

			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	List<AlbumDTO> getRecommendations(Integer userId, Integer numberOfSuggestions) {
//		String getMoviesAlreadyRated = """
//				SELECT * FROM albums WHERE album_id IN
//				    (SELECT album_id FROM seasons WHERE season_id IN
//				    (SELECT season_id FROM tracks
//				    INNER JOIN ratings r on tracks.track_id = r.track_id AND r.user_id = ?))
//								""";
		String sqlTvShowsAlreadyRatedAndTheRated = """
				SELECT album, AVG(rating) as rating
				FROM ratings
				         INNER JOIN tracks t ON ratings.track_id = t.track_id AND ratings.user_id = ?
				         INNER JOIN seasons s ON t.season_id = s.season_id
				         INNER JOIN albums a on s.album_id = a.album_id
				GROUP BY album;
								""";
		List<AlbumDTO> showsRated = new ArrayList<>();
		try (PreparedStatement gmarStmt = conn.prepareStatement(sqlTvShowsAlreadyRatedAndTheRated)) {
			gmarStmt.setInt(1, userId);
			ResultSet resultSet = gmarStmt.executeQuery();
			while (resultSet.next()) {
				showsRated.add(new AlbumDTO(resultSet.getString(1), resultSet.getFloat(2), null, null));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return OpenAI.getRecommendations(showsRated, numberOfSuggestions);
	}


	Float getRatingByAlbumId(Integer albumId) {
		AtomicReference<Float> rating = new AtomicReference<>(0f);
		AtomicInteger count = new AtomicInteger();
		SeasonDaoImpl seasonDao = new SeasonDaoImpl();
		List<Season> seasonsByTvshowId = seasonDao.getSeasonsByTvshowId(albumId);
		seasonsByTvshowId.forEach(season -> {
			Float ratingBySeasonId = seasonDao.getRatingBySeasonId(season.getSeason_id());
			if (ratingBySeasonId != null) {
				rating.updateAndGet(v -> v + ratingBySeasonId);
				count.getAndIncrement();
			}
		});
		return count.get() > 0 ? rating.get() / count.get() : null;
	}

	List<AlbumDTO> getProgressByUserId(int userId) {
		List<AlbumDTO> result = new ArrayList<>();
		String sql = """
SELECT album, percentage
FROM albums
         INNER JOIN
     (SELECT album_id, SUM(total) as completed, SUM(number) as total, SUM(total) / SUM(number) as percentage
      FROM seasons
               INNER JOIN
           (SELECT season_id, COUNT(number) AS number, COUNT(q1.track_id) as total
            FROM tracks t
                     LEFT JOIN
                 (SELECT track_id
                  from progress
                  where user_id = ?
                    and progress = 'completed') q1
                 on q1.track_id = t.track_id
            GROUP BY season_id) j1
           ON seasons.season_id = j1.season_id
      GROUP BY album_id) j2
     ON j2.album_id = albums.album_id
								""";
		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setInt(1, userId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				result.add(new AlbumDTO(
						resultSet.getString(1),
						null, null,
						resultSet.getFloat(2)
				));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

}
