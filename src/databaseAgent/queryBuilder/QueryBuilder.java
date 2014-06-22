package databaseAgent.queryBuilder;

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
import databaseAgent.queryComponents.RestrictionFragment;
import databaseAgent.queryComponents.TableFragment;

/**
 * Helper function to build HQL queries
 * 
 * @author HP
 * 
 */
public class QueryBuilder {

	public static final String DEFAULT_DATA_TABLE = "purchase as p left join purchase_set on p.purchase_set = purchase_set.id";
	public static final String PURCHASE_SET_TABLE = "purchase_set";

	public static final Set<String> SUPPORTED_CONDITION = Collections.unmodifiableSet(SQLTranslator.TRANSLATED_CONDITION.keySet());

	public static final Set<String> SUPPORTED_COUNT_OPTION = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("DISTINCT", "ALL")));

	public static final Set<String> SUPPORTED_FUNCTIONS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("DATE", "MONTH", "YEAR", "SUM", "AVG",
			"MIN", "MAX", "COUNT")));

	public static final List<String> JOIN_TYPE = Collections.unmodifiableList(Arrays.asList("JOIN", "INNER JOIN", "LEFT JOIN", "RIGHT JOIN"));
	
	public static final List<String> FIELD_LIST = Collections.unmodifiableList(Arrays.asList("p.id", "description", "type", "quantity",
			"unit", "cost", PURCHASE_SET_TABLE + ".location", PURCHASE_SET_TABLE + ".date"));

	
	private SQLTranslator translator;
	private int varID;

	public QueryBuilder() {
		translator = TranslatorFactory.getTranslator(TranslatorFactory.STANDARD_TRANSLATOR);
	}

	public QueryBuilder(SQLTranslator translator) {
		this.translator = translator;
	}
	
	public String simplify(String field) {
		return translator.simplify(field);
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
		
		field = translator.fieldTranslate(field);
		if (function != null) {
			return function + "(" + field + ")";
		} else {
			return field;
		}
	}
	
	public RestrictionFragment buildConstraint(String field, String condition, String value) {
		final String finalField = translator.fieldTranslate(field);
		String[] values = value.split(", ");
		final List<Object> realValues = new ArrayList<Object>(); 
		new Mapper<String, Void>() {
			@Override
			public Void map(String input) {
				realValues.addAll(Arrays.asList(translator.valueTranslate(finalField, input)));
				return null;
			}
		}.map(values);
		String[] conditions = translator.conditionTranslate(condition, realValues);
		String joiner = SQLTranslator.CONDITION_JOINER.get(condition);

		Map<String, Object> valueMapping = new HashMap<String, Object>();
		List<String> combining = new ArrayList<String>();
		
		if (conditions.length == realValues.size()) {//Match one by one
			for (int i = 0; i < conditions.length; i++) {
				String currentCondition = conditions[i];
				varID++;
				valueMapping.put(varID + "", realValues.get(i));
				combining.add(finalField + " " + currentCondition + " :var" + varID);
			}
		} else {//Distributive
			for (String currentCondition : conditions) {
				for (Object realValue : realValues) {
					varID++;
					valueMapping.put(varID + "", realValue);
					combining.add(finalField + " " + currentCondition + " (:var" + varID + ")");
				}
			}
		}
		String finalQuery = joinCondition(combining, joiner);

		return new RestrictionFragment(finalQuery, valueMapping);
	}

	public TableFragment buildTable(List<String> fields, String tableName, String alias) {
		return translator.tableTranslate(fields, tableName, alias);
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

	public static Map<String, String> parseQueryField(String queryField) {
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
