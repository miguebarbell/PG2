package populator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


import connection.ConnectionManager;

public class Populator {
	private static Connection conn = ConnectionManager.getConnection();

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

			statement.execute("CREATE TABLE albums (" +
			                  "album_id INT auto_increment PRIMARY KEY," +
			                  "album varchar(255));");


			statement.execute("CREATE TABLE seasons(" +
			                  "season_id INT AUTO_INCREMENT PRIMARY KEY ," +
			                  "album_id INT NOT NULL," +
			                  "title VARCHAR(255) NOT NULL," +
			                  "foreign key (album_id) references albums(album_id))");

			statement.execute("CREATE TABLE tracks(" +
			                  "track_id INT AUTO_INCREMENT PRIMARY KEY , " +
			                  "season_id INT NOT NULL, " +
			                  "number INT NOT NULL,"+
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

			statement.execute("insert into albums(album) values('BEEF');");
			statement.execute("INSERT INTO seasons(album_id, title) values (1, 'Season 1')");
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

			statement.execute("insert into albums(album) values(\"Bob Dylan, 'Blood on the Tracks'\");");
			statement.execute("insert into albums(album) values(\"Prince and the Revolution, 'Purple Rain'\");");
			statement.execute("insert into albums(album) values(\"Fleetwood Mac, 'Rumours'\");");
			statement.execute("insert into albums(album) values(\"Nirvana, 'Nevermind'\");");
			statement.execute("insert into albums(album) values(\"The Beatles, 'Abbey Road'\");");
			statement.execute("insert into albums(album) values(\"Stevie Wonder, 'Songs in the Key of Life'\");");
			statement.execute("insert into albums(album) values(\"Joni Mitchell, 'Blue'\");");
			statement.execute("insert into albums(album) values(\"The Beach Boys, 'Pet Sounds'\");");
			statement.execute("insert into albums(album) values(\"Marvin Gaye, 'What's Going On'\");");


		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}
}
