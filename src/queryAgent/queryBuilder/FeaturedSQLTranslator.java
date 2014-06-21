package queryAgent.queryBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import purchases.Type;
import utilities.DateUtility;
import utilities.functional.Function;

public class FeaturedSQLTranslator extends SQLTranslator {
	
	private static final Map<String,Function<Void, Object[]>> FEATURED_VALUES;
	
	
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
	
	public static void main(String[] args) {
		FeaturedSQLTranslator tr = new FeaturedSQLTranslator();
		QueryBuilder b = new QueryBuilder(tr);
		System.out.println(b.buildConstraint("type", "EQUAL", "#FOOD"));
	}
	
	@Override
	public Object[] valueTranslate(String field, String value) {
		if (FEATURED_VALUES.containsKey(value)) {
			return FEATURED_VALUES.get(value).function(null);
		} else {
			return super.valueTranslate(field, value);
		}
	}
}
