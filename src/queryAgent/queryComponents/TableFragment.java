package queryAgent.queryComponents;

import java.util.Arrays;
import java.util.List;

import utilities.StringUtility;
import utilities.functional.Filter;


public class TableFragment {
	
	private List<String> select;
	private String tableName;
	private String alias;
	
	public TableFragment(String tableName, String alias) {
		this.tableName = tableName;
		this.alias = alias;
	}
	
	public TableFragment(List<String> select, String tableName, String alias) {
		this(tableName, alias);
		this.select = select;
	}

	public TableFragment(String select, String tableName, String alias) {
		this(Arrays.asList(select), tableName, alias);
	}
	
	public void join(TableFragment other, String joinType, String joiningCondition, String newAlias) {
		this.tableName = "(" + this.toString() + ") " + joinType + " (" + other.toString() + ") "
				+ "ON (" +  joiningCondition + ")";
		alias = newAlias;
	}

	public String getFinalizedFromClause() {
		if (getSelectQuery().length() == 0) {
			alias = null;
		}
		return this.toString();
	}
	
	public String getTable() {
		return tableName;
	}
	
	public String getSelect() {
		return StringUtility.join(select, ", ");
	}
	
	public String getAlias() {
		return alias;
	}
	
	private String getSelectQuery() {
		String selectPart = "";
		if (select != null && !select.isEmpty()) {
			List<String> temp = new Filter<String>() {
				@Override
				public boolean filter(String item) {
					if (item == null) {
						return false;
					} else {
						return item.replaceAll(" ", "").length() != 0;
					}
				}
			}.filter(select);
			
			if (!temp.isEmpty()) {
				selectPart = "(SELECT " + StringUtility.join(temp, ", ") + " FROM ";
			}
		}
		return selectPart;
	}
	
	@Override
	public String toString() {
		String selectQuery = getSelectQuery();
		String tablePart = "";
		if (alias != null && alias.length() != 0) {
			if (selectQuery.isEmpty()) {
				tablePart = "(" + tableName + ")" + " as " + alias;
			} else {
				tablePart = "(" + tableName + "))" + " as " + alias;
			}
		} else {
			if (selectQuery.isEmpty()) {
				tablePart = tableName;
			} else {
				tablePart = tableName + ")";
			}
		}
		return selectQuery + tablePart;
	}
	
	@Override
	public TableFragment clone() {
		return new TableFragment(this.select, this.tableName, this.alias);
	}
}
