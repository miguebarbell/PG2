package dao;

public interface TrackDao {
	Integer getRatingByTrackId(int trackId);
	boolean addRating(RatingType rating, int trackId, int userId);
	boolean save(Track track);

}
