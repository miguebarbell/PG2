package dao;

import java.util.ArrayList;
import java.util.List;

public class Album {

	private int album_id;
	private String album;
	private String url;
	private String description;
	private final List<RatingType> ratings = new ArrayList<>();
	private final List<Track> tracks = new ArrayList<>();

	public Album(int album_id, String album) {
		this.album_id = album_id;
		this.album = album;
	}

	public Album(String album) {
		this.album = album;
	}

	public Album() {

	}

	/**
	 * @return the album_id
	 */
	public int getAlbum_id() {
		return album_id;
	}

	/**
	 * @param album_id the album_id to set
	 */
	public void setAlbum_id(int album_id) {
		this.album_id = album_id;
	}

	/**
	 * @return the album
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * @param album the album to set
	 */
	public void setAlbum(String album) {
		this.album = album;
	}

	public Float getRating() {
		return (float) (ratings.stream()
		                       .mapToInt(Enum::ordinal)
		                       .sum()) / ratings.size();
	}

	public void addRating(RatingType rating) {
		ratings.add(rating);
	}

	@Override
	public String toString() {
		return "Album [album_id=" + album_id + ", album=" + album + "]";
	}

}
