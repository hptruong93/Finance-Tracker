package queryAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlainBuilder extends QueryBuilder {
	
	@Override
	public String buildOrderBy(String input, String option) {
		return input;
	}
	
	@Override
	public String buildGroupBy(String input) {
		return input;
	}
	
	@Override
	public RestrictionFragment buildConstraint(String field, String condition, String value) {
		Map<Integer, Object> valueMapping = new HashMap<Integer, Object>();
		return new RestrictionFragment(condition, valueMapping);
	}
	
	@Override
	public TableFragment buildTable(List<String> fields, String tableName, String alias) {
		return new TableFragment(new ArrayList<String>(), tableName, null);
	}
}