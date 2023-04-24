package dao;

import java.util.List;

public interface AlbumDao {
	
	public Album getAlbumId(int a_id);
	
	public List<Album> getAllAlbums();
	
	public boolean addAlbum(Album album);
	
	
/*public List<Employees> getAllEmployees();
	
	public Employees getEmployeeById(int id);
	
	public boolean addEmployee(Employees emp);
	
	public boolean deleteEmployee(int empId);
	
	public boolean updateEmployee(Employees emp); */

}
