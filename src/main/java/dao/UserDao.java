package dao;

public interface UserDao {

	//public boolean addUser(User user);

	User loginUser(User user);

	User getUsername(String username);

	User getUserId(int u_id);

	boolean createUser(String username, String password);
}
