package utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JsonRootNode;

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
	 * Conver JsonRootnode to string representation
	 * @param node JSON root node
	 * @return string representation of the json node
	 */
	public static String jsonToString(JsonRootNode node) {
		JsonFormatter JSON_FORMATTER = new PrettyJsonFormatter();
		return JSON_FORMATTER.format(node);
	}
}
