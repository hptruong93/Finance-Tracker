package utilities;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DateUtility {

	private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy - MM - dd", Locale.ENGLISH);
	
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
	public static java.sql.Date parseSQLDate(String input) {
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

	public static java.util.Date parseDate(String date) {
		return new java.util.Date(parseSQLDate(date).getTime());
	}
	
	public static String dateToString(java.util.Date date) {
		return DEFAULT_DATE_FORMAT.format(date);
	}
	
	public static Date getTodaySQL() {
		return new java.sql.Date(Calendar.getInstance().getTimeInMillis());
	}
	
	public static java.util.Date getToday() {
		return new java.util.Date(Calendar.getInstance().getTimeInMillis());
	}
	
	public static int getTodayDate() {
		Calendar temp = Calendar.getInstance();
		return temp.get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getThisMonth() {
		Calendar temp = Calendar.getInstance();
		return temp.get(Calendar.MONTH) + 1;
	}
	
	public static int getThisYear() {
		Calendar temp = Calendar.getInstance();
		return temp.get(Calendar.YEAR);
	}
	
	public static Calendar getPureCalendar(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		return calendar;
	}
	
	public static Date startThisWeek() {
		Calendar calendar = getPureCalendar(Calendar.getInstance());

		// get start of this week in milliseconds
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
		return new Date(calendar.getTimeInMillis());
	}
	
	public static Date endThisWeek() {
		Calendar calendar = getPureCalendar(Calendar.getInstance());

		// get start of this week in milliseconds
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
		return new Date(calendar.getTimeInMillis());
	}
	
	public static Date startThisMonth() {
		Calendar calendar = getPureCalendar(Calendar.getInstance());

		// get start of the month
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return new Date(calendar.getTimeInMillis());
	}
	
	public static Date endThisMonth() {
		Calendar calendar = getPureCalendar(Calendar.getInstance());
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new Date(calendar.getTimeInMillis());
	}
	
	public static Date startThisYear() {
		Calendar calendar = getPureCalendar(Calendar.getInstance());

		// get start of the month
		calendar.set(Calendar.DAY_OF_YEAR, 1);
		return new Date(calendar.getTimeInMillis());
	}
	
	public static Date endThisYear() {
		Calendar calendar = getPureCalendar(Calendar.getInstance());

		// get start of the month
		calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
		return new Date(calendar.getTimeInMillis());
	}
	
	public static Date today() {
		Calendar today = getPureCalendar(Calendar.getInstance());
		return new Date(today.getTimeInMillis());
	}
	
	/**
	 * Private constructor so that no instance is created
	 */
	private DateUtility() {
		throw new IllegalStateException("Cannot create an instance of static class Util");
	}
}
