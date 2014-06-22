package utilities;

import java.io.File;

import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JsonRootNode;

public class JSONUtility {

	/**
	 * Convert JsonRootnode to string representation
	 * @param node JSON root node
	 * @return string representation of the json node
	 */
	public static String jsonToString(JsonRootNode node) {
		JsonFormatter JSON_FORMATTER = new PrettyJsonFormatter();
		return JSON_FORMATTER.format(node);
	}
	
	/**
	 * Write a JSON to file
	 * @param item the json which content will be written to file
	 * @param file the target file
	 */
	public static void dumpToFile(JsonRootNode item, File file) {
		FileUtility.writeJson(item, file);
	}
	
	/**
	 * Private constructor so that no instance is created
	 */
	private JSONUtility() {}
}
