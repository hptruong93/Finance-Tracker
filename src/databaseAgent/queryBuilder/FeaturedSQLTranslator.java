package databaseAgent.queryBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import purchases.Type;
import utilities.DateUtility;
import utilities.functional.Function;

public class FeaturedSQLTranslator extends SQLTranslator {
	
	private static final Map<String,Function<Void, Object[]>> FEATURED_VALUES;
	private static final String FEATURE_TYPE = "#FEATURED";
	
	static {
		HashMap<String, Function<Void, Object[]>> temp = new HashMap<String, Function<Void, Object[]>>();
		
		temp.put("#TODAY_DATE", new Function<Void, Object[]>() {
			@Override
			public Object[] function(Void input) {
				return new Integer[]{DateUtility.getTodayDate()};
			}});
		temp.put("#THIS_MONTH", new Function<Void, Object[]>() {
			@Override
			public Object[] function(Void input) {
				return new Integer[]{DateUtility.getThisMonth()};
			}});
		temp.put("#THIS_YEAR", new Function<Void, Object[]>() {
			@Override
			public Object[] function(Void input) {
				return new Integer[]{DateUtility.getThisYear()};
			}});
		temp.put("#FOOD", new Function<Void, Object[]>() {
			@Override
			public Object[] function(Void input) {
				return Type.PURCHASE_TYPES_TREE.findType("food").getBottomTypes().toArray();
			}});
		
		FEATURED_VALUES = Collections.unmodifiableMap(temp);
		
		
	}
	
	protected FeaturedSQLTranslator() {
	}
	
	@Override
	public Object[] valueTranslate(String field, String value) {
		if (FEATURED_VALUES.containsKey(value)) {
			return FEATURED_VALUES.get(value).function(null);
		} else {
			return super.valueTranslate(field, value);
		}
	}
	
	@Override
	public Object[] valueParse(String value, String type) {
		if (type.startsWith(FEATURE_TYPE)) {
			return FEATURED_VALUES.get(value).function(null);
		} else {
			return super.valueParse(value, type);
		}
	}
}
