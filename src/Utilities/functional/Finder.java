package utilities.functional;

import java.util.Arrays;
import java.util.List;

/**
 * Find an element in a list
 * @author HP
 *
 * @param <T> list type
 */
public abstract class Finder<T> {
	
	/**
	 * Find an element in a list based on the findCondition.
	 * @param list list of item to be searched
	 * @return the first item found, or null if nothing found
	 */
	public T find(List<T> list) {
		for (T item : list) {
			if (findCondition(item)) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * Find an element in an array based on the findCondition.
	 * @param array array of item to be searched
	 * @return the first item found, or null if nothing found
	 */
	public T find(T...array) {
		return find(Arrays.asList(array));
	}
	
	/**
	 * Abstract method describing the finding condition
	 * @param item item that will be checked
	 * @return if check condition is satisfied
	 */
	public abstract boolean findCondition(T item);
}
