package utilities;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Loggin feature. All print statement should go here to log to file. This is a
 * static class. No instance should be created
 * 
 * @author VDa
 * 
 */
public class Log {

	private static final int STD_OUT = 0;
	private static final int FILE = 1;
	private static final int MODE = STD_OUT;

	private static final SimpleDateFormat DEFAULT_TIME = new SimpleDateFormat("HH:mm:ss");
	private static final SimpleDateFormat DEFAULT_DATE = new SimpleDateFormat("dd/MM/yyyy",
			Locale.ENGLISH);
	private static final File LOG_FILE;

	static {
		if (MODE == FILE) {
			 LOG_FILE = new File("Finance Tracker.log");
		} else {
			LOG_FILE = null;
		}
	}

	/**
	 * Private constructor to prevent creation
	 */
	private Log() {
		throw new IllegalStateException("Cannot create an instance of static class Log");
	}

	/**
	 * Write log event of an exception Convert the exception stack trace into
	 * string first.
	 * 
	 * @param e
	 *            exception caught
	 */
	public static void exception(Throwable e) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		writeLog(result.toString());
	}

	public static void info(Class<?> className, String content) {
		if (className != null) {
			writeLog("[" + className.getName() + "] ---> " + content);
		}
	}
	
	public static void info(Object object, String content) {
		if (object != null) {
			writeLog("[" + object.getClass().getName() + "] -- [" + object + "] ---> " + content);
		}
	}
	
	/**
	 * Write content to a log file using FileUtility
	 * 
	 * @param content
	 *            content that will be written
	 */
	public static void writeLog(String content) {
		if (MODE == FILE) {
			try {
				LOG_FILE.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Calendar now = Calendar.getInstance();
		StringBuffer toWrite = new StringBuffer("");
		toWrite.append(DEFAULT_TIME.format(new Date(now.getTimeInMillis()))).append(" - ");
		toWrite.append(DEFAULT_DATE.format(new Date(now.getTimeInMillis())));
		toWrite.append(": ").append(content);

		if (MODE == FILE) {
			FileUtility.writeToFile(toWrite, LOG_FILE, true);
		} else if (MODE == STD_OUT) {
			System.out.println(toWrite);
		}
	}
}