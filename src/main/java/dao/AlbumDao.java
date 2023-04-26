package dao;

import java.util.List;

public interface AlbumDao {

	Album getAlbumId(int a_id);

	List<Album> getAllAlbums();

	boolean addAlbum(Album album);
//TODO: add album as admin
//	Album save(Album album);

	boolean addRating(Ratings ratings, Integer userId, Integer albumId);

/*public List<Employees> getAllEmployees();

	public Employees getEmployeeById(int id);

	public boolean addEmployee(Employees emp);

	public boolean deleteEmployee(int empId);

	public boolean updateEmployee(Employees emp); */

}
