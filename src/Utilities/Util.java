package utilities;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Provide static interface to utilities
 * 
 * @author HP
 * 
 */
public class Util {

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
			temp[i] = upperCase(temp[i]);
		}
		return join(temp, "");
	}

	/**
	 * Convert the first character of a string to upper case
	 * @param input a string
	 * @return the same string with first character capitalized
	 */
	public static String upperCase(String input) {
		String[] temp = input.split(" ");
		for (int i = 0; i < temp.length; i++) {
			temp[i] = Character.toUpperCase(temp[i].charAt(0)) + temp[i].substring(1);
		}
		return join(temp, " ");
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
	
	/**
	 * Invoke a method from a class
	 * 
	 * @param object
	 *            the object that will call the method. Null if static method
	 * @param callingClass
	 *            the class that will be called. This MUST be the same as the
	 *            object class
	 * @param methodName
	 *            String representing the name of the method
	 * @param param
	 *            parameters that will be passed into the method
	 * @return the result of the method. Exception will occur if the method
	 *         returns void (i.e. does not return anything)
	 */
	public static Object invoke(Object object, Class<?> callingClass, String methodName, Object[] param) {
		try {
			for (Method method : callingClass.getDeclaredMethods()) {
				if (method.getName().equals(methodName)) {
					Class<?>[] types = method.getParameterTypes();
					for (int i = 0; i < types.length; i++) {
						Class<?> current = param[i].getClass();
						if (!types[i].isAssignableFrom(current)) {
							throw new IllegalArgumentException("Invalid input parameters");
						}
					}
					
					return method.invoke(object, param);
				}
			}
			
			return null;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new IllegalStateException("Cannot invoke method...");
	}

	/**
	 * Private constructor so that no instance is created
	 */
	private Util() {
		throw new IllegalStateException("Cannot create an instance of static class Util");
	}
}
