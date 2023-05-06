package populator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


import connection.ConnectionManager;

public class Populator {
	private static final Connection conn = ConnectionManager.getConnection();

	public static void reset() {

		try (Statement statement = conn.createStatement()) {
			statement.execute("DROP SCHEMA IF EXISTS progresstracker;");
			statement.execute("CREATE SCHEMA progresstracker;");
			statement.execute("USE progresstracker;");

			statement.execute("CREATE TABLE users (" +
			                  "user_id INT auto_increment PRIMARY KEY," +
			                  "username varchar(255) unique," +
			                  "password varchar(255)," +
			                  "user_type INT default 1);");

			statement.execute("CREATE TABLE tvshows (" +
			                  "show_id INT auto_increment PRIMARY KEY," +
			                  "`show` varchar(255) UNIQUE);");


			statement.execute("CREATE TABLE seasons(" +
			                  "season_id INT AUTO_INCREMENT PRIMARY KEY ," +
			                  "show_id INT NOT NULL," +
			                  "title VARCHAR(255) NOT NULL," +
			                  "foreign key (show_id) references tvshows(show_id))");

			statement.execute("CREATE TABLE tracks(" +
			                  "track_id INT AUTO_INCREMENT PRIMARY KEY , " +
			                  "season_id INT NOT NULL, " +
			                  "number INT NOT NULL," +
			                  "title VARCHAR(255) NOT NULL, " +
			                  "foreign key (season_id) references seasons(season_id))");

			statement.execute("CREATE TABLE progress (" +
			                  "user_id INT NOT NULL," +
			                  "track_id INT NOT NULL," +
			                  "progress varchar(255)," +
			                  "foreign key (user_id) references users(user_id)," +
			                  "foreign key (track_id) references tracks(track_id));");

			statement.execute("CREATE TABLE ratings(" +
			                  "rating_id INT auto_increment PRIMARY KEY, " +
			                  "user_id INT NOT NULL, " +
			                  "rating INT NOT NULL, " +
			                  "track_id INT NOT NULL, " +
			                  "foreign key (user_id) references users(user_id), " +
			                  "foreign key (track_id) references tracks(track_id))");

			// Users
			statement.execute("INSERT into users(username, password, user_type) values ('miguel', md5('root'), 0);");
			statement.execute("INSERT into users(username, password, user_type) values ('talha', md5('root'), 0);");
			statement.execute("INSERT into users(username, password, user_type) values ('jesus', md5('root'), 0);");
			statement.execute("INSERT into users(username, password) values ('sean', md5('root'));");

			// BEEF TV SHOW
			statement.execute("insert into tvshows(`show`) values('BEEF');");
			statement.execute("INSERT INTO seasons(show_id, title) values (1, 'Season 1')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (1,1,'The Birds Don\\'t Sing, They " +
			                  "Screech in Pain')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (1,2,'The Rapture of Being Alive')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (1,3,'I Am Inhabited By a Cry')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (1,4,'Just Not All at the Same Time')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (1,5,'Such Inward Secret Creatures')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (1,6,'We Draw a Magic Circle')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (1,7,'I Am a Cage')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (1,8,'The Drama of Original Choice')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (1,9,'The Great Fabricator')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (1,10,'Figures of Light')");

			// The Boys
			statement.execute("insert into tvshows(`show`) values('The Boys');");
			statement.execute("INSERT INTO seasons(show_id, title) values (2, 'Season 1')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (2,1,'The Name of the Game')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (2,2,'Cherry')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (2,3,'Get Some')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (2,4,'The Female of the Species')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (2,5,'Good for the Soul')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (2,6,'The Innocents')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (2,7,'The Self-Preservation Society')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (2,8,'You Found Me')");

			statement.execute("INSERT INTO seasons(show_id, title) values (2, 'Season 2')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (3,1,'The Big Ride')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (3,2,'Proper Preparation and Planning')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (3,3,'Over the Hill with the Swords of a" +
			                  " Thousand')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (3,4,'Nothing Like It in the World')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (3,5,'We Gotta Go Now')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (3,6,'The Bloody Door Off')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (3,7,'Butcher, Baker, Candlestick " +
			                  "Maker')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (3,8,'What I Know')");

			statement.execute("INSERT INTO seasons(show_id, title) values (2, 'Season 3')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (4,1,'Payback')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (4,2,'The Only Man in the Sky')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (4,3,'Barbary Coast')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (4,4,'Glorious Five Year Plan')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (4,5,'The Last Time to Look on This " +
			                  "World of Lies')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (4,6,'Herogasm')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (4,7,'Here Comes a Candle to Light You " +
			                  "to Bed')");
			statement.execute("INSERT INTO tracks(season_id, number, title) values (4,8,'The Instant White-Hot Wild')");
			// track some episodes with progress
			// user_id 1
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 1, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 2, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 3, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 4, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 5, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 6, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 7, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 8, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 9, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 10, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 11, 'completed')");


			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 19, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 20, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 21, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 22, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 23, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (1, 24, 'completed')");
			// user_id 2
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (2, 1, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (2, 2, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (2, 3, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (2, 4, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (2, 5, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (2, 6, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (2, 7, 'completed')");
			statement.execute("INSERT INTO progress(user_id, track_id, progress) values (2, 8, 'completed')");
			// give some ratings
			// user_id 1
			statement.execute("INSERT INTO ratings(user_id, track_id, rating) values (1, 1, 5)");
			statement.execute("INSERT INTO ratings(user_id, track_id, rating) values (1, 2, 4)");
			statement.execute("INSERT INTO ratings(user_id, track_id, rating) values (1, 3, 3)");
			statement.execute("INSERT INTO ratings(user_id, track_id, rating) values (1, 4, 3)");
			statement.execute("INSERT INTO ratings(user_id, track_id, rating) values (1, 5, 5)");
			statement.execute("INSERT INTO ratings(user_id, track_id, rating) values (1, 6, 5)");
			statement.execute("INSERT INTO ratings(user_id, track_id, rating) values (1, 7, 4)");
			// user_id 2
			statement.execute("INSERT INTO ratings(user_id, track_id, rating) values (2, 1, 3)");
			statement.execute("INSERT INTO ratings(user_id, track_id, rating) values (2, 2, 2)");
			statement.execute("INSERT INTO ratings(user_id, track_id, rating) values (2, 3, 3)");
			statement.execute("INSERT INTO ratings(user_id, track_id, rating) values (2, 4, 3)");
			statement.execute("INSERT INTO ratings(user_id, track_id, rating) values (2, 5, 5)");
			statement.execute("INSERT INTO ratings(user_id, track_id, rating) values (2, 6, 0)");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}
}
