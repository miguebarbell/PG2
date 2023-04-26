package dao;

public enum Ratings {
	REALLY_BAD(0),
	BAD(1),
	AVERAGE(2),
	GOOD(3),
	THE_BEST(4);
	private final int value;

	Ratings(int value) {
		this.value = value;
	}


}
