package utilities.functional;

import java.util.Arrays;
import java.util.List;

/**
 * Verify if all item in a list satisfy certain condition
 * @author HP
 *
 * @param <T> list type
 */
public abstract class Verifier<T> {
	
	/**
	 * Verify if all item in the list satisfy a condition checked by verifyAction
	 * @param list list of items to check
	 * @return if all item in the list satisfy a condition checked by verifyAction
	 */
	public boolean verify(List<T> list) {
		for (T item : list) {
			if (!verifyAction(item)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Verify if all item in the array satisfy a condition checked by verifyAction
	 * @param array array of items to check
	 * @return if all item in the array satisfy a condition checked by verifyAction
	 */
	public boolean verify(T... array) {
		return verify(Arrays.asList(array));
	}
	
	/**
	 * Abstract method describe the checking condition
	 * @param item an item to check
	 * @return if the item satisfies the check condition
	 */
	public abstract boolean verifyAction(T item);
}
