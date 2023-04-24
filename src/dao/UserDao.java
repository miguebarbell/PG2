package dao;

public interface UserDao {

	//public boolean addUser(User user);

	public User loginUser(User user);

	public User getUsername(String username);

	public User getUserId(int u_id);

	boolean createUser(String username, String password);
}
