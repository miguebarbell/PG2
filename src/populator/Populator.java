package populator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


import connection.ConnectionManager;

public class Populator {
	private static Connection conn = ConnectionManager.getConnection();

	public static void reset() {
		
		try (Statement statement = conn.createStatement();) {
			statement.execute("DROP SCHEMA IF EXISTS progresstracker;");
			statement.execute("CREATE SCHEMA progresstracker;");
			statement.execute("USE progresstracker;");
			statement.execute("CREATE TABLE users (" + "user_id INT auto_increment PRIMARY KEY,"
					+ "username varchar(255) unique," + "password varchar(255));");
			statement.execute(
					"CREATE TABLE albums (" + "album_id INT auto_increment PRIMARY KEY," + "album varchar(255));");
			statement.execute("CREATE TABLE progress (" + "user_id INT NOT NULL," + "album_id INT NOT NULL,"
					+ "progress varchar(255)," + "foreign key (user_id) references users(user_id),"
					+ "foreign key (album_id) references albums(album_id));");
			statement.execute("INSERT into users(username, password) values ('sam2', md5('root'));");
			statement.execute("INSERT into users(username, password) values ('charina1', md5('root'));");
			statement.execute("insert into albums values(NULL, \"Lauryn Hill, 'The Miseducation of Lauryn Hill'\");");
			statement.execute("insert into albums values(NULL, \"Bob Dylan, 'Blood on the Tracks'\");");
			statement.execute("insert into albums values(NULL, \"Prince and the Revolution, 'Purple Rain'\");");
			statement.execute("insert into albums values(NULL, \"Fleetwood Mac, 'Rumours'\");");
			statement.execute("insert into albums values(NULL, \"Nirvana, 'Nevermind'\");");
			statement.execute("insert into albums values(NULL, \"The Beatles, 'Abbey Road'\");");
			statement.execute("insert into albums values(NULL, \"Stevie Wonder, 'Songs in the Key of Life'\");");
			statement.execute("insert into albums values(NULL, \"Joni Mitchell, 'Blue'\");");
			statement.execute("insert into albums values(NULL, \"The Beach Boys, 'Pet Sounds'\");");
			statement.execute("insert into albums values(NULL, \"Marvin Gaye, 'What's Going On'\");");
			

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}
}
