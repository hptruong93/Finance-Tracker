package utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Functional programing style for Java
 * @author HP
 *
 * @param <D> domain class for the map operation
 * @param <R> range class for the map operation
 */
public abstract class Mapper<D, R> {
	
	/**
	 * Map an Iterable from Domain class to Range class using map function 
	 * @param items Iterable of items in Domain class
	 * @return list of items in Range class
	 */
	public List<R> map(Iterable<D> items) {
		ArrayList<R> output = new ArrayList<R>();
		for (D s : items) {
			output.add(map(s));
		}
		
		return output;
	}
	
	/**
	 * Map an array from Domain class to Range class using map function
	 * @param items array of items in Domain class
	 * @return array of items in Range class
	 */
	public List<R> map(D[] items) {
		return map(Arrays.asList(items));
	}
	
	/**
	 * Abstract function mapping of one element from domain to range.
	 * @param input element in Domain class
	 * @return element in Range class
	 */
	public abstract R map(D input);
	
	/*****************************************************************
	                 Below are the commonly used mappers.
	*****************************************************************/
	
	/**
	 * Mapper for appending string in front and at the end of strings 
	 * @param pre string that will be inserted in front
	 * @param post string that will be appended at the back
	 * @return Mapper for appending string in front and at the end of strings 
	 */
	public static Mapper<String, String> appender(final String pre, final String post) {
		return new Mapper<String, String>() {
			@Override
			public String map(String input) {
				return pre + input + post;
			}
		};
	}
	
	/**
	 * Mapper for copying the list of string
	 * @return Mapper for coppying string list
	 */
	public static Mapper<String, String> coppier() {
		return new Mapper<String, String>() {
			@Override
			public String map(String input) {
				return input;
			}
		};
	}
}
