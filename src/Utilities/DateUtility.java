package utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DateUtility {

	/**
	 * Attempt to parse a string to a date. Support the following format
	 * 
	 * Date: dd
	 * Month: MM, MMM, MMMM
	 * Year: YYYY
	 * All possible combination of the above formats
	 * are accepted. The following orders are accepted:
	 * date - month - year
	 * year - month - date
	 * 
	 * Spaces are ignored. "/" can be replaced by "-" or "."
	 * @param input input string to parse
	 * @return parsed Date. null if cannot parse
	 */
	public static java.sql.Date parseDate(String input) {
		String[] joiner = new String[]{"-", "/", "."};
		String[] date = new String[] {"dd"};
		String[] month = new String[] {"MM", "MMM", "MMMM"};
		String[] year = new String[] {"yyyy"};
		
		ArrayList<String> possibleFormat = new ArrayList<String>();
		for (String j : joiner) {
			for (String d : date) {
				for (String m : month) {
					for (String y : year) {
						possibleFormat.add(d + j + m + j + y);
						possibleFormat.add(y + j + m + j + d);
					}
				}
			}
		}
		
		for (String format : possibleFormat) {
			try {
				DateFormat parser = new SimpleDateFormat(format, Locale.ENGLISH);
				parser.setLenient(false);
				java.util.Date tempOut = parser.parse(input);
				return new java.sql.Date(tempOut.getTime());
			} catch (Exception e) {
			}
		}
		
		return null;
	}

	/**
	 * Private constructor so that no instance is created
	 */
	private DateUtility() {
		throw new IllegalStateException("Cannot create an instance of static class Util");
	}
}
