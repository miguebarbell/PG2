package dao;

public class Season {
	private final String title;
	private final Integer tvshow_id;
	private Integer season_id;

	public Season(String title, Integer tvshow_id, Integer season_id) {
		this.title = title;
		this.tvshow_id = tvshow_id;
		this.season_id = season_id;
	}

	public Season(String title, Integer tvshow_id) {
		this.title = title;
		this.tvshow_id = tvshow_id;
	}

	public String getTitle() {
		return title;
	}

	public Integer getTvshow_id() {
		return tvshow_id;
	}

	public Integer getSeason_id() {
		return season_id;
	}
}
