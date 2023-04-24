package dao;

import java.util.List;

public interface ProgressDao {
	
	public List<Progress> getAllUserTrackers(int u_id);
	
	public boolean updateProgress(Progress progress);
	
	public boolean addProgress(Progress progress);
	
	
/*public List<Employees> getAllEmployees();
	
	public Employees getEmployeeById(int id);
	
	public boolean addEmployee(Employees emp);
	
	public boolean deleteEmployee(int empId);
	
	public boolean updateEmployee(Employees emp); */

}
