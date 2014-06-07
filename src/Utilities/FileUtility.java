package utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JdomParser;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;

/**
 * Provide file reading and writing utilities
 * This is a static class. No instance should be created
 * @author VDa
 *
 */
public class FileUtility {

	/**
	 * Private constructor so that no instance is created
	 */
	private FileUtility() {
		throw new IllegalStateException("Cannot create an instance of static class FileUtility");
	}
	
	/**
	 * Read a plain text file.
	 * @param file file that will be read
	 * @return StringBuffer the read result.
	 */
	public static StringBuffer readFromFile(File file) {
		StringBuffer output = new StringBuffer("");
		
		FileReader fr = null;
		
		try {
			fr = new FileReader(file);
			
			BufferedReader br = new BufferedReader(fr);
			
			while (true) {
	            String in = br.readLine();
	            if (in == null) {
	               break;
	            }
	            output.append(in).append("\n");
	        }
			
			br.close();
			
		} catch (IOException e) {
			Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, e);
		} finally {
			try {
				fr.close();
			} catch (IOException e) {
				Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, e);
			}
		}
		return output;
	}

	/**
	 * Write a content to a file
	 * @param content content that will be written to file
	 * @param file target file
	 * @param append will the content be appended to the file or overwritten old data in file (if exists)
	 */
	public static void writeToFile(StringBuffer content, File file, boolean append) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, append);

			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(new String(content));
			bw.flush();
			bw.close();
		} catch (IOException ex) {
			Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				fw.close();
			} catch (IOException ex) {
				Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	/**
	 * Read a JSON file and return a JSON object
	 * 
	 * @param file
	 *            the file that will be read
	 * @return the root node of the JSON object
	 */
	public static JsonRootNode readJSON(File file) {
		StringBuffer strings = readFromFile(file);
		try {
			return new JdomParser().parse(strings.toString());
		} catch (InvalidSyntaxException e) {
			return null;
		}
	}

	/**
	 * IO combining two paths
	 * @param path1 first path
	 * @param path2 second path
	 * @return a path created by joining first path and second path
	 */
	public static String joinPath(String path1, String path2) {
		File file1 = new File(path1);
		File file2 = new File(file1, path2);
		return file2.getPath();
	}

	/**
	 * IO combining paths
	 * @param paths array of paths
	 * @return a path created by joining all the paths
	 */
	public static String joinPath(String... paths) {
		if (paths.length == 0) {
			return "";
		} else {
			String output = paths[0];
			for (int i = 1; i < paths.length; i++) {
				output = joinPath(output, paths[i]);
			}
			return output;
		}
	}
}