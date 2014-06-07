package utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtility {

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
	private ReflectionUtility() {
		throw new IllegalStateException("Cannot create an instance of static class Util");
	}
}
