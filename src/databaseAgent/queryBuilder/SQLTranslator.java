package databaseAgent.queryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.bidimap.UnmodifiableBidiMap;
import org.apache.commons.lang3.StringUtils;

import utilities.DateUtility;
import utilities.StringUtility;
import utilities.functional.Filter;
import utilities.functional.Function;
import databaseAgent.queryComponents.TableFragment;

public class SQLTranslator {
	private Map<String, String> varMapper;

	//Refer to QueryBuilder.FIELD_LIST
	private static final Map<String, String> TYPES;
	private static final Map<String, Function<String, Object>> PARSEABLE_TYPES;
	private static final String SAME_TYPE = "#SAME";
	
	//Refer to QueryBuider.SUPPORTED_CONDITIONS
	protected static final BidiMap<String, String> TRANSLATED_CONDITION;
	
	static {
		TYPES = new HashMap<String, String>();
		
		//Properties
		TYPES.put("id", "java.lang.Integer");
		TYPES.put("location", "java.lang.String");
		TYPES.put("date", "java.sql.Date");
		TYPES.put("description", "java.lang.String");
		TYPES.put("type", "java.lang.String");
		TYPES.put("quantity", "java.lang.Integer");
		TYPES.put("unit", "java.lang.String");
		TYPES.put("cost", "java.lang.Float");
		TYPES.put("purchase_set_id", "java.lang.Integer");
		
		//Functions. #SAME class indicates that this functions returns the same type of the field
		TYPES.put("DATE", "java.lang.Integer");
		TYPES.put("MONTH", "java.lang.Integer");
		TYPES.put("YEAR", "java.lang.Integer");
		TYPES.put("SUM", SAME_TYPE);
		TYPES.put("AVG", SAME_TYPE);
		TYPES.put("MIN", SAME_TYPE);
		TYPES.put("MAX", SAME_TYPE);
		TYPES.put("COUNT", "java.lang.Integer");
		
		HashMap<String, Function<String, Object>> temp1 = new HashMap<String, Function<String, Object>>();
		temp1.put("java.sql.Date", new Function<String, Object>(){
			@Override
			public Object function(String input) {
				return DateUtility.parseSQLDate(input);
			}});
		
		temp1.put("java.lang.Integer", new Function<String, Object>(){
			@Override
			public Object function(String input) {
				return Integer.parseInt(input);
			}});
		
		temp1.put("java.lang.Float", new Function<String, Object>(){
			@Override
			public Object function(String input) {
				return Float.parseFloat(input);
			}});
		
		temp1.put("java.lang.Double", new Function<String, Object>(){
			@Override
			public Object function(String input) {
				return Double.parseDouble(input);
			}});
		
		temp1.put("java.lang.String", new Function<String, Object>(){
			@Override
			public Object function(String input) {
				return input;
			}});
		
		PARSEABLE_TYPES = Collections.unmodifiableMap(temp1);
		BidiMap<String, String> translated = new DualHashBidiMap<String, String>();
		translated.put("BETWEEN", "BETWEEN"); //Can't really translate
		translated.put("EQUAL", "=");
		translated.put("NOT_EQUAL", "<>");
		translated.put("GREATER_THAN", ">");
		translated.put("LESS_THAN", "<");
		translated.put("LIKE", "LIKE");
		translated.put("ILIKE", "ILIKE");
		translated.put("IN", "IN");
		translated.put("IS_EMPTY", "IS EMPTY");
		translated.put("IS_NOT_EMPTY", "IS NOT EMPTY");
		translated.put("IS_NULL", "IS NULL");
		translated.put("IS_NOT_NULL", "IS NOT NULL");
		
		TRANSLATED_CONDITION = UnmodifiableBidiMap.unmodifiableBidiMap(translated);
	}
	
	protected SQLTranslator() {
		varMapper = new HashMap<String, String>();
		for (String field : QueryBuilder.FIELD_LIST) {
			String[] split = field.split("\\.");
			varMapper.put(split[split.length - 1], field);
		}
	}
	
	protected String simplify(String field) {
		return field.replace("p\\.", "").replace(QueryBuilder.PURCHASE_SET_TABLE + "\\.", "");
	}
	
	private String simpleFieldTranslate(String field) {
		return varMapper.get(StringUtility.getComponent(field, "\\.", -1));
	}
	
	private String deTranslate(String field) {
		return StringUtility.getComponent(field, "\\.", -1);
	}
	
	public String fieldTranslate(String field) {
		Map<String, String> parsed = parseQueryField(field);
		String parsedField = parsed.get("field");
		String option = parsed.get("option");
		String function = parsed.get("function");
		field = simpleFieldTranslate(parsedField);
		
		if (function == null) {
			return field;
		} else if (option != null) {
			return function + "(" + option + " " + field + ")";
		} else {
			return function + "(" + field + ")";
		}
	}
	
	public String fieldDetranslate(String field) {
		Map<String, String> parsed = parseQueryField(field);
		String parsedField = parsed.get("field");
		String option = parsed.get("option");
		String function = parsed.get("function");
		field = deTranslate(parsedField);
		
		if (function == null) {
			return field;
		} else if (option != null) {
			return function + "(" + option + " " + field + ")";
		} else {
			return function + "(" + field + ")";
		}
	}
	
	public TableFragment tableTranslate(List<String> fields, String table, String alias) {
		if (fields == null) {
			fields = new ArrayList<String>();
		} else {
			fields = new Filter<String>() {
				@Override
				public boolean filter(String item) {
					return !item.replaceAll(" ", "").isEmpty();
				}
			}.filter(fields);
		}
		return new TableFragment(fields, table, alias);
	}
	
	public Object[] valueTranslate(String field, String value) {
		Map<String, String> parsed = parseQueryField(field);
		String function = parsed.get("function");
		field = StringUtility.getComponent(parsed.get("field"), "\\.", -1);
		
		String toParse;
		if (function == null) {
			 toParse = TYPES.get(field);
		} else {
			toParse = TYPES.get(function);
			if (toParse.equals(SAME_TYPE)) {
				toParse = TYPES.get(field);
			}
		}
		
		return valueParse(value, toParse);
	}
	
	public Object[] valueParse(String value, String type) {
		return new Object[] {PARSEABLE_TYPES.get(type).function(value)};
	}
	
	/**
	 * Translate a condition string into SQL equivalent representation
	 * @param condition condition in String, @see SUPPORTED_CONDITION in QueryBuilder
	 * @param values values that have been parsed by valueTranslate method
	 * @return map with two keys: array of strings "condition" for the HQL conditions parsed
	 *  and "joiner" to join these conditions
	 */
	public Map<String, Object> conditionTranslate(String condition, List<Object> values) {
		Map<String, Object> out = new HashMap<String, Object>();
		out.put("joiner", "AND");
		
		switch (condition) {
		case "BETWEEN":
			out.put("condition", new String[] {">", "<"});
			break;
		case "EQUAL":
			String[] output = new String[values.size()];
			for (int i = 0; i < output.length; i++) {
				output[i] = "=";
			}
			out.put("condition", output);
			out.put("joiner", "OR");
			break;
		case "NOT_EQUAL":
			output = new String[values.size()];
			for (int i = 0; i < output.length; i++) {
				output[i] = "<>";
			}
			out.put("condition", output);
			break;
		case "GREATER_THAN":
			output = new String[values.size()];
			for (int i = 0; i < output.length; i++) {
				output[i] = ">";
			}
			out.put("condition", output);
			break;
		case "LESS_THAN":
			output = new String[values.size()];
			for (int i = 0; i < output.length; i++) {
				output[i] = "<";
			}
			out.put("condition", output);
			break;
		case "LIKE":
			output = new String[values.size()];
			for (int i = 0; i < output.length; i++) {
				output[i] = "LIKE";
			}
			out.put("condition", output);
			out.put("joiner", "OR");
			break;
		case "IN":
			output = new String[values.size()];
			for (int i = 0; i < output.length; i++) {
				output[i] = "=";
			}
			out.put("condition", output);
			out.put("joiner", "OR");
			break;
		case "ILIKE":
			output = new String[values.size()];
			for (int i = 0; i < output.length; i++) {
				output[i] = "ILIKE";
			}
			out.put("condition", output);
			out.put("joiner", "OR");
			break;
		case "IS_EMPTY":
			out.put("condition", new String[] {"IS EMPTY"});
			break;
		case "IS_NOT_EMPTY":
			out.put("condition", new String[] {"IS NOT EMPTY"});
			break;
		case "IS_NOT_NULL":
			out.put("condition", new String[] {"IS NOT NULL"});
			break;
		case "IS_NULL":
			out.put("condition", new String[] {"IS NULL"});
			break;
		default:
			return null;
		}
		return out;
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