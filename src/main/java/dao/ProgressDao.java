package dao;

import java.util.List;

public interface ProgressDao {
	List<Progress> getAllUserTrackers(int u_id);
	boolean updateProgress(Progress progress);
	boolean addProgress(Progress progress);
}
