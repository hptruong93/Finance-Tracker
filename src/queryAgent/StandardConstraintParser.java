package queryAgent;

import java.sql.Date;
import java.util.HashMap;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import utilities.DateUtility;

public class StandardConstraintParser {
	protected static final HashMap<String, Class<?>> TYPES;
	
	public StandardConstraintParser() {
	}

	static {
		TYPES = new HashMap<String, Class<?>>();
		TYPES.put("id", Integer.class);
		TYPES.put("purchaseSet.location", String.class);
		TYPES.put("purchaseSet.date", Date.class);
		TYPES.put("description", String.class);
		TYPES.put("type", String.class);
		TYPES.put("quantity", Integer.class);
		TYPES.put("unit", String.class);
		TYPES.put("cost", Float.class);
		TYPES.put("purchase_set_id", Integer.class);
	}

	protected Object parseValue(String field, String values) {
		Object output = null;
		Class<?> toParse = TYPES.get(field);
		try {
			if (toParse == Date.class) {
				output = DateUtility.parseDate(values);
			} else if (toParse == Integer.class) {
				output = Integer.parseInt(values);
			} else if (toParse == Float.class) {
				output = Float.parseFloat(values);
			} else if (toParse == String.class) {
				output = values;
			}
		} catch (Exception e) {
			output = null;
		}
		return output;
	}
	
	/**
	 * Parse the information to give out a hibernate criterion. This only takes
	 * into account simple conditions
	 * 
	 * Special processing for certain information: EQUAL with multiple values
	 * will be joined by OR NOT_EQUAL with multiple values will be joined by AND
	 * LIKE and ILIKE with multiple values will be joined by OR
	 * 
	 * @param field
	 *            name of the field
	 * @param condition
	 *            condition type
	 * @param value
	 *            value of the condition
	 * @return a hibernate criterion. Null if there is any error in input
	 */
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
					or.add(Restrictions.eq(field, parseValue(field, values[i])));
				}
				out = or;
				break;
			case "NOT_EQUAL":
				Conjunction and = Restrictions.conjunction();
				for (int i = 0; i < values.length; i++) {
					and.add(Restrictions.ne(field, parseValue(field, values[i])));
				}
				out = and;
				break;
			case "GREATER_THAN":
				out = Restrictions.gt(field, parseValue(field, values[0]));
				break;
			case "LESS_THAN":
				out = Restrictions.lt(field, parseValue(field, values[0]));
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
