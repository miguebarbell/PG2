package dao;

public class Track {
	private Integer id;
	private String title;
	private Integer number;
	private Integer seasonID;

	public Track(Integer id, String title, Integer number, Integer seasonID) {
		this.id = id;
		this.title = title;
		this.number = number;
		this.seasonID = seasonID;
	}

	public Track(String title, Integer number, Integer seasonID) {
		this.title = title;
		this.number = number;
		this.seasonID = seasonID;
	}

	public Integer getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Integer getNumber() {
		return number;
	}

	public Integer getSeasonID() {
		return seasonID;
	}
}
