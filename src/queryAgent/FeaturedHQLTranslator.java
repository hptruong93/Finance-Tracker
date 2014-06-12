package queryAgent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import utilities.DateUtility;
import utilities.functional.Function;

public class FeaturedHQLTranslator extends HQLTranslator {
	
	private static final Map<String,Function<Void, ?>> FEATURED_VALUES;
	
	static {
		HashMap<String, Function<Void, ?>> temp = new HashMap<String, Function<Void, ?>>();
		
		temp.put("#TODAY_DATE", new Function<Void, Integer>() {
			@Override
			public Integer function(Void input) {
				return DateUtility.getTodayDate();
			}});
		temp.put("#THIS_MONTH", new Function<Void, Integer>() {
			@Override
			public Integer function(Void input) {
				return DateUtility.getThisMonth();
			}});
		temp.put("#THIS_YEAR", new Function<Void, Integer>() {
			@Override
			public Integer function(Void input) {
				return DateUtility.getThisYear();
			}});
		
		FEATURED_VALUES = Collections.unmodifiableMap(temp);
	}
	
	protected FeaturedHQLTranslator() {
		
	}
	
	@Override
	public Object valueTranslate(String field, String value) {
		Object toReturn = super.valueTranslate(field, value);
		if (toReturn == null) {
			if (FEATURED_VALUES.containsKey(toReturn)) {
				return FEATURED_VALUES.get(field).function(null);
			} else {
				return null;
			}
		}
		return toReturn;
	}
}
