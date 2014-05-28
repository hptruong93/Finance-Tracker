package Utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
}