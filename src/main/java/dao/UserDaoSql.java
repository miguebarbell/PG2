package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.ConnectionManager;
import encoder.HasherImpl;

public class UserDaoSql implements UserDao{

	private Connection conn = ConnectionManager.getConnection();

	@Override
	public User getUsername(String username) {

		try( PreparedStatement pstmt = conn.prepareStatement("select * from users where username = ?")){

			pstmt.setString(2, username);

			ResultSet rs = pstmt.executeQuery();
			User user = null;

			if(rs.next()) {
				int user_id = rs.getInt("user_id");
				String usname = rs.getString("username");
				String password = rs.getString("password");
				UserTypes userType = rs.getInt("user_type") == 0 ? UserTypes.ADMIN : UserTypes.USER;

				user = new User(user_id, usname, password, userType);

			}

			rs.close();
			// use getter for the username
			return user;

		} catch (SQLException e) {
			System.out.println("Could not find username");
		}

		return null;
	}

	@Override
	public User getUserId(int u_id) {

  try( PreparedStatement pstmt = conn.prepareStatement("select * from users where user_id = ?")){

			pstmt.setInt(1, u_id);

			ResultSet rs = pstmt.executeQuery();
			User user = null;

			if(rs.next()) {
				int user_id = rs.getInt("user_id");
				String usname = rs.getString("username");
				String password = rs.getString("password");

				UserTypes userType = rs.getInt("user_type") == 0 ? UserTypes.ADMIN : UserTypes.USER;

				user = new User(user_id, usname, password, userType);
//				user = new User(user_id, usname, password);

			}

			rs.close();
			// use getter for the username
			return user;

		} catch (SQLException e) {
			System.out.println("Could not find user id" + u_id);
		}

		return null;
	}

	@Override
	public boolean createUser(String username, String password) {

		try {

				conn.createStatement().execute("INSERT into users(username, password) values ('" + username + "', md5('" + password +
				                               "'))");
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	@Override
	public User loginUser(User user) {

    try( PreparedStatement pstmt = conn.prepareStatement("select * from users where username = ? and password = md5(?) ")){


			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getPassword());

			ResultSet rs = pstmt.executeQuery();
			User user1 = null;

			if(rs.next()) {
				int usId = rs.getInt("user_id");
				String usname = rs.getString("username");
				String password = rs.getString("password");
				UserTypes userType = rs.getInt("user_type") == 0 ? UserTypes.ADMIN : UserTypes.USER;

				user1 = new User(usId, usname, password, userType);

			}

			rs.close();
			if(user1 != null) {
			System.out.println("Account found" + "\n" + "Welcome " + user.getUsername());
			}
			return user1;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not find user's account, please register");
		}

		return null;
	}





}
