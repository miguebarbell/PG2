package dao;

public class Progress {

	private int user_id;
	private int track_id;
	private String progress;

	public Progress(int user_id, int track_id, String progress) {
		//FIXME: progress should be an integer
		this.user_id = user_id;
		this.track_id = track_id;
		this.progress = progress;
	}

	public Progress() {

	}

	/**
	 * @return the user_id
	 */
	public int getUser_id() {
		return user_id;
	}

	/**
	 * @param user_id the user_id to set
	 */
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	/**
	 * @return the album_id
	 */
	public int getTrack_id() {
		return track_id;
	}

	/**
	 * @param track_id the album_id to set
	 */
	public void setTrack_id(int track_id) {
		this.track_id = track_id;
	}

	/**
	 * @return the progress
	 */
	public String getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(String progress) {
		this.progress = progress;
	}

	@Override
	public String toString() {
		return "Progress [user_id=" + user_id + ", track_id=" + track_id + ", progress=" + progress + "]";
	}


}
