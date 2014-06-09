package utilities;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Provide static interface to utilities
 * 
 * @author HP
 * 
 */
public class StringUtility {

	/**
	 * Join an iterable of string to one string with joiner. If an element
	 * of the iterable contains nothing or only space, it will be ignored
	 * @param fields iterable of string elements that will be joined.
	 * @param joiner delimiter between each element
	 * @return One string resulted from the elements joined with joiner
	 */
	public static String join(Iterable<String> fields, String joiner) {
		StringBuilder builder = new StringBuilder();
		Iterator<String> iter = fields.iterator();
		
		while (iter.hasNext()) {
			String next = iter.next();
			
			boolean valid = next.replaceAll(" ", "").length() != 0; 
			
			if (valid) {
				builder.append(next);
			}
			
			if (!iter.hasNext()) {
				break;
			}

			if (valid) {
				builder.append(joiner);
			}
		}
		return builder.toString();
	}
	
	/**
	 * Join an array of string to one string with joiner. If an element
	 * of the array contains nothing or only space, it will be ignored
	 * @param data array of string elements that will be joined.
	 * @param joiner delimiter between each element
	 * @return One string resulted from the elements joined with joiner
	 */
	public static String join(String[] data, String joiner) {
		return join(Arrays.asList(data), joiner);
	}

	/**
	 * Transform string from the format "quick-brown-fox" into "quickBrownFox"
	 * @param input string in the format "quick-brown-fox"
	 * @return string in the format "quickBrownFox"
	 */
	public static String joinDash(String input) {
		String[] temp = input.split("-");
		for (int i = 1; i < temp.length; i++) {
			temp[i] = upperCaseFirstChar(temp[i]);
		}
		return join(temp, "");
	}

	/**
	 * Convert the first character of a string to upper case
	 * @param input a string
	 * @return the same string with first character capitalized
	 */
	public static String upperCaseFirstChar(String input) {
		String[] temp = input.split(" ");
		for (int i = 0; i < temp.length; i++) {
			temp[i] = Character.toUpperCase(temp[i].charAt(0)) + temp[i].substring(1);
		}
		return join(temp, " ");
	}

	/**
	 * Remove last characters of a string
	 * @param input the string that will have last characters removed
	 * @param amount the amount of characters that will be removed
	 * @return the string with amount of last characters removed 
	 */
	public static String removeLast(String input, int amount) {
		if (input.length() >= amount) {
			return input.substring(0, input.length() - amount);
		} else {
			return input;
		}
	}
	
	/**
	 * Private constructor so that no instance is created
	 */
	private StringUtility() {
		throw new IllegalStateException("Cannot create an instance of static class Util");
	}
}