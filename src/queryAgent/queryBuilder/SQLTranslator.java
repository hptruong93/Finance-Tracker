package queryAgent.queryBuilder;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import queryAgent.queryComponents.TableFragment;
import utilities.DateUtility;
import utilities.StringUtility;
import utilities.functional.Filter;
import utilities.functional.Function;

public class SQLTranslator {
	private HashMap<String, String> varMapper;
	
	private static final HashMap<String, Class<?>> TYPES;
	private static final Map<String, Function<String, Object>> PARSEABLE_TYPES;
	
	static {
		TYPES = new HashMap<String, Class<?>>();
		
		//Properties
		TYPES.put("id", Integer.class);
		TYPES.put("location", String.class);
		TYPES.put("date", Date.class);
		TYPES.put("description", String.class);
		TYPES.put("type", String.class);
		TYPES.put("quantity", Integer.class);
		TYPES.put("unit", String.class);
		TYPES.put("cost", Float.class);
		TYPES.put("purchase_set_id", Integer.class);
		
		//Functions. Void class indicates that this functions returns the same type of the field
		TYPES.put("DATE", Integer.class);
		TYPES.put("MONTH", Integer.class);
		TYPES.put("YEAR", Integer.class);
		TYPES.put("SUM", Void.class);
		TYPES.put("AVG", Void.class);
		TYPES.put("MIN", Void.class);
		TYPES.put("MAX", Void.class);
		TYPES.put("COUNT", Integer.class);
		
		HashMap<String, Function<String, Object>> temp1 = new HashMap<String, Function<String, Object>>();
		temp1.put("java.sql.Date", new Function<String, Object>(){
			@Override
			public Object function(String input) {
				return DateUtility.parseDate(input);
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
		Object output = null;
		Map<String, String> parsed = parseQueryField(field);
		String function = parsed.get("function");
		field = StringUtility.getComponent(parsed.get("field"), "\\.", -1);
		
		Class<?> toParse;
		if (function == null) {
			 toParse = TYPES.get(field);
		} else {
			toParse = TYPES.get(function);
			if (toParse == Void.class) {
				toParse = TYPES.get(field);
			}
		}
		
		try {
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
		
		if (output != null) {
			return new Object[]{output};
		} else {
			return null;
		}
	}
	
	public Object valueParse(String value, String type) {
		System.out.println(type);
		return PARSEABLE_TYPES.get(type).function(value);
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
			out.put("condition", new String[] {"<", ">"});
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
			out.put("condition", new String[]{"IN"});
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