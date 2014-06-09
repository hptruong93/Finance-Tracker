package queryAgent;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import utilities.DateUtility;

public class FeaturedConstraintParser extends StandardConstraintParser {
	
	public static final Set<String> FEATURED_CONDITION = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
			"#TODAY", "#THIS_WEEK", "#START_THIS_WEEK", "#END_THIS_WEEK",
			"#THIS_MONTH", "#START_THIS_MOTNH", "#END_THIS_MONTH",
			"#THIS_YEAR", "#START_THIS_YEAR", "#END_THIS_YEAR")));
	
	private static final Map<String, Object> TRANSLATOR;
	
	static {
		Map<String, Object> temp  = new HashMap<String, Object>();
		temp.put("#TODAY", DateUtility.today());
		temp.put("#THIS_WEEK", null);
		temp.put("#START_THIS_WEEK", DateUtility.startThisWeek());
		temp.put("#END_THIS_WEEK", DateUtility.endThisWeek());
		temp.put("#THIS_MONTH", null);
		temp.put("#START_THIS_MOTNH", DateUtility.startThisMonth());
		temp.put("#END_THIS_MONTH", DateUtility.endThisMonth());
		temp.put("#THIS_YEAR", null);
		temp.put("#START_THIS_YEAR", DateUtility.startThisYear());
		temp.put("#END_THIS_YEAR", DateUtility.endThisYear());
		TRANSLATOR = Collections.unmodifiableMap(temp);
	}
	
	@Override
	protected Object parseValue(String field, String value) {
		Object output = null;
		Class<?> toParse = TYPES.get(field);
		try {
			if (FEATURED_CONDITION.contains(value)) {
				return TRANSLATOR.get(value);
			}
			
			if (toParse == Date.class) {
				output = DateUtility.parseDate(value);
			} else if (toParse == Integer.class) {
				output = Integer.parseInt(value);
			} else if (toParse == Float.class) {
				output = Float.parseFloat(value);
			} else if (toParse == String.class) {
				output = value;
			}
		} catch (Exception e) {
			output = null;
		}
		return output;
	}
	
	@Override
	public Criterion parseConstraint(String field, String condition, String value) {
		Criterion out;
		String[] values;
		if (value.contains(",")) {
			values = value.split(",");
		} else {
			values = new String[] { value };
		}

		try {
			switch (condition) {
			case "BETWEEN":
				out = Restrictions.between(field, parseValue(field, values[0]), parseValue(field, values[1]));
				break;
			case "EQUAL":
				Disjunction or = Restrictions.disjunction();
				for (int i = 0; i < values.length; i++) {
					if (values[i].equals("#THIS_WEEK")) {
						or.add(parseConstraint(field, "BETWEEN", "#START_THIS_WEEK, #END_THIS_WEEK"));
					} else if (values[i].equals("#THIS_MONTH")) {
						or.add(parseConstraint(field, "BETWEEN", "#START_THIS_MONTH, #END_THIS_MONTH"));
					} else if (values[i].equals("#THIS_YEAR")) {
						or.add(parseConstraint(field, "BETWEEN", "#START_THIS_YEAR, #END_THIS_YEAR"));
					} else {
						or.add(Restrictions.eq(field, parseValue(field, values[i])));
					}
				}
				out = or;
				break;
			case "NOT_EQUAL":
				Conjunction and = Restrictions.conjunction();
				for (int i = 0; i < values.length; i++) {
					if (values[i].equals("#THIS_WEEK")) {
						and.add(Restrictions.not(parseConstraint(field, "BETWEEN", "#START_THIS_WEEK, #END_THIS_WEEK")));
					} else if (values[i].equals("#THIS_MONTH")) {
						and.add(Restrictions.not(parseConstraint(field, "BETWEEN", "#START_THIS_MONTH, #END_THIS_MONTH")));
					} else if (values[i].equals("#THIS_YEAR")) {
						and.add(Restrictions.not(parseConstraint(field, "BETWEEN", "#START_THIS_YEAR, #END_THIS_YEAR")));
					} else {
						and.add(Restrictions.ne(field, parseValue(field, values[i])));
					}
				}
				out = and;
				break;
			case "GREATER_THAN":
				if (values[0].equals("#THIS_WEEK")) {
					out = parseConstraint(field, "GREATER_THAN", "#END_THIS_WEEK");
				} else if (values[0].equals("#THIS_MONTH")) {
					out = parseConstraint(field, "GREATER_THAN", "#END_THIS_MONTH");
				} else if (values[0].equals("#THIS_YEAR")) {
					out = parseConstraint(field, "GREATER_THAN", "#END_THIS_YEAR");
				} else {
					out = Restrictions.gt(field, parseValue(field, values[0]));
				}
				break;
			case "LESS_THAN":
				if (values[0].equals("#THIS_WEEK")) {
					out = parseConstraint(field, "LESS_THAN", "#START_THIS_WEEK");
				} else if (values[0].equals("#THIS_MONTH")) {
					out = parseConstraint(field, "LESS_THAN", "#START_THIS_MONTH");
				} else if (values[0].equals("#THIS_YEAR")) {
					out = parseConstraint(field, "LESS_THAN", "#START_THIS_YEAR");
				} else {
					out = Restrictions.lt(field, parseValue(field, values[0]));
				}
				break;
			case "LIKE":
				or = Restrictions.disjunction();
				for (int i = 0; i < values.length; i++) {
					or.add(Restrictions.like(field, parseValue(field, values[i])));
				}
				out = or;
				break;
			case "ILIKE":
				or = Restrictions.disjunction();
				for (int i = 0; i < values.length; i++) {
					or.add(Restrictions.ilike(field, parseValue(field, values[i])));
				}
				out = or;
				break;
			case "IS_EMPTY":
				out = Restrictions.isEmpty(field);
				break;
			case "IS_NOT_EMPTY":
				out = Restrictions.isNotEmpty(field);
				break;
			case "IS_NOT_NULL":
				out = Restrictions.isNotNull(field);
				break;
			case "IS_NULL":
				out = Restrictions.isNull(field);
				break;
			default:
				return null;
			}
			return out;
		} catch (Exception e) {
			return null;
		}
	}
}
