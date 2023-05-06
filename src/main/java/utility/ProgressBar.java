package utility;

/**
 * Solution from
 * https://stackoverflow.com/users/9676376/marko-%c5%a0tumberger
 */
public class ProgressBar {
	public static String progressBar(int currentValue, int maxValue) {
	    int progressBarLength = 33;
	    if (progressBarLength < 9 || progressBarLength % 2 == 0) {
	        throw new ArithmeticException("formattedPercent.length() = 9! + even number of chars (one for each side)");
	    }
	    int currentProgressBarIndex = (int) Math.ceil(((double) progressBarLength / maxValue) * currentValue);
	    String formattedPercent = String.format(" %5.1f %% ", (100 * currentProgressBarIndex) / (double) progressBarLength);
	    int percentStartIndex = ((progressBarLength - formattedPercent.length()) / 2);

	    StringBuilder sb = new StringBuilder();
	    sb.append("[");
	    for (int progressBarIndex = 0; progressBarIndex < progressBarLength; progressBarIndex++) {
	        if (progressBarIndex <= percentStartIndex - 1
	        ||  progressBarIndex >= percentStartIndex + formattedPercent.length()) {
	            sb.append(currentProgressBarIndex <= progressBarIndex ? " " : "=");
	        } else if (progressBarIndex == percentStartIndex) {
	            sb.append(formattedPercent);
	        }
	    }
	    sb.append("]");
	    return sb.toString();
	}
}
