package utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class GeneralUtility {
	public static String printStackTrace(Exception ex) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		ex.printStackTrace(printWriter);
		return result.toString(); 
	}
}
