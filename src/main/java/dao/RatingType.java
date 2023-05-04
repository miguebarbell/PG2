package dao;

public enum RatingType {
	REALLY_BAD(1),
	BAD(2),
	AVERAGE(3),
	GOOD(4),
	THE_BEST(5);
	private final int value;

	RatingType(int value) {
		this.value = value;
	}


}
