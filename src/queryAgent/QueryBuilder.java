package queryAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import utilities.StringUtility;
import utilities.functional.Mapper;

/**
 * Helper function to build HQL queries
 * 
 * @author HP
 * 
 */
public class QueryBuilder {

	public static final String PURCHASE_SET_TABLE = "purchaseSet";

	public static final List<String> SUPPORTED_CONDITION = Collections.unmodifiableList(new ArrayList<String>(Arrays.asList("BETWEEN", "EQUAL", "NOT_EQUAL",
			"GREATER_THAN", "LESS_THAN", "LIKE", "ILIKE", "IS_EMPTY", "IS_NOT_EMPTY", "IS_NOT_NULL", "IS_NULL")));

	private static final Set<String> SUPPORTED_COUNT_OPTION = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("DISTINCT", "ALL")));

	public static final Set<String> SUPPORTED_FUNCTIONS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("DATE", "MONTH", "YEAR", "SUM", "AVG",
			"MIN", "MAX", "COUNT")));

	public static final List<String> FIELD_LIST = Collections.unmodifiableList(new ArrayList<String>(Arrays.asList("id", "description", "type", "quantity",
			"unit", "cost", PURCHASE_SET_TABLE + ".location", PURCHASE_SET_TABLE + ".date")));

	private HQLTranslator translator;
	private int varID;

	public QueryBuilder() {
		translator = TranslatorFactory.getTranslator(TranslatorFactory.STANDARD_TRANSLATOR);
	}

	public QueryBuilder(HQLTranslator translator) {
		this.translator = translator;
	}
	
	public static boolean validSelect(String selectField) {
		Map<String, String> parsed = parseQueryField(selectField);
		String field = parsed.get("field");
		String function = parsed.get("function");
		String option = parsed.get("option");

		if (field == null) {
			return false;
		} else {
			if (!(new Mapper<String, String>() {
				@Override
				public String map(String input) {
					String[] split = input.split("\\.");
					return split[split.length - 1];
				}
			}.map(FIELD_LIST).contains(StringUtility.getComponent(field, "\\.", -1)))) {
				return false;
			}

			if (function != null) {
				if (!SUPPORTED_FUNCTIONS.contains(function)) {
					return false;
				}

				if (option != null) {
					if (function.toUpperCase().equals("COUNT")) {
						return SUPPORTED_COUNT_OPTION.contains(option);
					}
				}
			}
			return true;
		}
	}

	public String buildOrderBy(String input, String option) {
		if (option.equals("ASC")) {
			return translator.fieldTranslate(input) + " ASC";
		} else if (option.equals("DESC")) {
			return translator.fieldTranslate(input) + " DESC";
		} else {
			return null;
		}
	}
	
	public String buildGroupBy(String input) {
		Map<String, String> parsed = parseQueryField(input);
		String field = parsed.get("field");
		String function = parsed.get("function");
		String option = parsed.get("option");
		
		if (option != null) {
			return null;
		}
		
		field = "p." + translator.fieldTranslate(field);
		if (function != null) {
			return function + "(" + field + ")";
		} else {
			return field;
		}
	}
	
	public RestrictionFragment buildConstraint(String field, String condition, String value) {
		final String finalField = translator.fieldTranslate(field);
		String[] conditions = translator.conditionTranslate(condition);
		String[] values = value.split(", ");
		List<Object> realValues = new Mapper<String, Object>() {
			@Override
			public Object map(String input) {
				return translator.valueTranslate(finalField, input);
			}
		}.map(values);

		Map<Integer, Object> valueMapping = new HashMap<Integer, Object>();
		List<String> combining = new ArrayList<String>();

		for (int i = 0; i < conditions.length; i++) {
			String currentCondition = conditions[i];
			varID++;
			valueMapping.put(varID, realValues.get(i));
			combining.add(finalField + " " + currentCondition + " :var" + varID);
		}

		String finalQuery = joinCondition(combining, "AND");

		return new RestrictionFragment(finalQuery, valueMapping);
	}

	/**
	 * Generate a join between two tables given two list of fields to compare
	 * 
	 * @param condition
	 *            join condition/field
	 * @param type
	 *            join type: AND, OR
	 * @return String representing the join action
	 */
	public static String joinCondition(Iterable<String> condition, String joiner) {
		Mapper<String, String> mapper = new Mapper<String, String>() {
			@Override
			public String map(String input) {
				if (input.length() == 0) {
					return "";
				} else if (input.startsWith("(") && input.endsWith(")")) {
					if ((input.length() - input.replace("(", "").length()) == 1) {
						return input;
					}
				}
				return "(" + input + ")";
			}
		};
		return StringUtility.join(mapper.map(condition), " " + joiner + " ");
	}

	/**
	 * Join a list of condition using a joiner keyword
	 * 
	 * @param condition
	 *            list of condition
	 * @param joiner
	 *            'AND'/ 'OR'
	 * @return String representing the joint condition
	 */
	public static String joinCondition(String[] condition, String joiner) {
		return joinCondition(Arrays.asList(condition), joiner);
	}

	protected static Map<String, String> parseQueryField(String queryField) {
		HashMap<String, String> output = new HashMap<String, String>();
		String field, function, option;

		int countOpen = StringUtils.countMatches(queryField, "(");
		int countClose = StringUtils.countMatches(queryField, ")");

		if (countOpen != 1 || countClose != 1) {
			output.put("field", queryField);
		} else {
			String[] splitVar = queryField.replace(")", "").split("\\(");
			try {
				function = splitVar[0];
				if (!SUPPORTED_FUNCTIONS.contains(function)) {
					return output;
				}

				field = null;
				if (StringUtils.countMatches(splitVar[1], " ") == 1) {
					String[] splitOption = splitVar[1].split(" ");
					field = splitOption[1];
					option = splitOption[0];

					output.put("function", function);
					output.put("field", field);
					output.put("option", option);
				} else {
					field = splitVar[1];
					output.put("function", function);
					output.put("field", field);
				}
			} catch (Exception e) {
				return output;
			}
		}
		return output;
	}
}
