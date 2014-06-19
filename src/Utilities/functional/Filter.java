package utilities.functional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Filter items in a list satisfy certain condition
 * @author HP
 *
 * @param <T> list type
 */
public abstract class Filter<T> {
	
	/**
	 * Filter all items in the list satisfy a condition
	 * @param list list of items to filter
	 * @return all item in the list that satisfy a condition checked by filter
	 */
	public List<T> filter(List<T> list) {
		List<T> output = new ArrayList<T>();
		for (T item : list) {
			if (filter(item)) {
				output.add(item);
			}
		}
		return output;
	}
	
	/**
	 * Filter all items in the list satisfy a condition
	 * @param array array of items to filter
	 * @return all item in the list that satisfy a condition checked by filter
	 */
	public List<T> filter(T... array) {
		return filter(Arrays.asList(array));
	}
	
	/**
	 * Abstract method describe the checking condition
	 * @param item an item to check
	 * @return if the item satisfies the check condition
	 */
	public abstract boolean filter(T item);
}
