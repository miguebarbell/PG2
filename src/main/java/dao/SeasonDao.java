package dao;

import java.util.List;

public interface SeasonDao {
	boolean save(Season season);
	List<Track> getEpisodesBySeasonId(int seasonId);
	Float getRatingBySeasonId(int seasonId);
	List<Season> getSeasonsByTvshowId(int tvShowId);
}
