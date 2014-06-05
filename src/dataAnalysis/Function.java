package dataAnalysis;

import java.util.Arrays;
import java.util.HashSet;

public class Function {
	public static final String SUM = "SUM";
	public static final String AVERAGE = "AVG";
	public static final String MIN = "MIN";
	public static final String MAX = "MAX";
	public static final String COUNT = "COUNT";
	private static final HashSet<String> SUPPORTED_FUNCTIONS = new HashSet<String>(Arrays.asList(SUM, AVERAGE, MIN, MAX, COUNT));
	
	protected static boolean supportedFunction(String name) {
		return SUPPORTED_FUNCTIONS.contains(name);
	}
}
