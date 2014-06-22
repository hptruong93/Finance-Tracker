package utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class GeneralUtility {
	
	/**
	 * Provide a string describing the stack trace of the exception.
	 * @param ex exception
	 * @return a string describing the stack trace of this exception
	 */
	public static String printStackTrace(Exception ex) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		ex.printStackTrace(printWriter);
		return result.toString(); 
	}
	
	/**
	 * Private constructor so that no instance is created
	 */
	private GeneralUtility() {}
}
