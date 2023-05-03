package dao;

public interface TrackDao {
	Float getRatingByTrackId(int trackId);
	boolean addRating(RatingType rating, int trackId, int userId);
	Integer save(Track track);

}
