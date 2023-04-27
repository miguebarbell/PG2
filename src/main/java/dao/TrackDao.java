package dao;

public interface TrackDao {
	Float getRating();
	void addRating(Ratings rating);
	Track save(Track track);

}
