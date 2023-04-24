package dao;

public class Progress {
	
	private int user_id;
	private int album_id;
	private String progress;
	
	public Progress(int user_id, int album_id, String progress) {
		this.user_id = user_id;
		this.album_id = album_id;
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
		return "Progress [user_id=" + user_id + ", album_id=" + album_id + ", progress=" + progress + "]";
	}
	
	
}
