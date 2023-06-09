package dao;

import java.util.List;

public interface SeasonDao {
	Integer save(Season season);
	List<Track> getEpisodesBySeasonId(int seasonId);
	Float getRatingBySeasonId(int seasonId);
	List<Season> getSeasonsByTvshowId(int tvShowId);
	Float getProgressByUserIdAndSeasonId(int userId, int seasonId);
}
