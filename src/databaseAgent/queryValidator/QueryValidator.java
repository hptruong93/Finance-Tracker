package databaseAgent.queryValidator;

import java.util.Map;

import databaseAgent.queryBuilder.QueryBuilder;
import utilities.StringUtility;
import utilities.functional.Mapper;

public class QueryValidator {

	public static boolean validSelect(String selectField) {
		selectField = selectField.split(" (?i)as ")[0];
		Map<String, String> parsed = QueryBuilder.parseQueryField(selectField);
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
			}.map(QueryBuilder.FIELD_LIST).contains(StringUtility.getComponent(field, "\\.", -1)))) {
				return false;
			}
	
			if (function != null) {
				if (!QueryBuilder.SUPPORTED_FUNCTIONS.contains(function)) {
					return false;
				}
	
				if (option != null) {
					if (function.toUpperCase().equals("COUNT")) {
						return QueryBuilder.SUPPORTED_COUNT_OPTION.contains(option);
					}
				}
			}
			return true;
		}
	}

}
