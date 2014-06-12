package queryAgent;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import utilities.DateUtility;
import utilities.StringUtility;

public class HQLTranslator {
	private HashMap<String, String> translator;
	
	private static final HashMap<String, Class<?>> TYPES;
	
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
	}
	
	protected HQLTranslator() {
		translator = new HashMap<String, String>();
		for (String field : QueryBuilder.FIELD_LIST) {
			String[] split = field.split("\\.");
			translator.put(split[split.length - 1], "p." + field);
		}
	}
	
	private String simpleFieldTranslate(String field) {
		return translator.get(StringUtility.getComponent(field, "\\.", -1));
	}
	
	public String fieldTranslate(String field) {
		Map<String, String> parsed = parseQueryField(field);
		String parsedField = parsed.get("field");
		String option = parsed.get("option");
		String function = parsed.get("function");
		field = simpleFieldTranslate(parsedField);	
		
		if (function == null) {
			return simpleFieldTranslate(parsedField);
		} else if (option != null) {
			return function + "(" + option + " " + field + ")";
		} else {
			return function + "(" + field + ")";
		}
	}
	
	public Object valueTranslate(String field, String value) {
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
		return output;
	}
	
	public String[] conditionTranslate(String condition) {
		switch (condition) {
		case "BETWEEN":
			return new String[] {"<", ">"};
		case "EQUAL":
			return new String[] {"="};
		case "NOT_EQUAL":
			return new String[] {"<>"};
		case "GREATER_THAN":
			return new String[] {">"};
		case "LESS_THAN":
			return new String[] {"<"};
		case "LIKE":
			return new String[] {"LIKE"};
		case "ILIKE":
			return new String[] {"ILIKE"};
		case "IS_EMPTY":
			return new String[] {"IS EMPTY"};
		case "IS_NOT_EMPTY":
			return new String[] {"IS NOT EMPTY"};
		case "IS_NOT_NULL":
			return new String[] {"IS NOT NULL"};
		case "IS_NULL":
			return new String[] {"IS NULL"};
		default:
			return null;
		}
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