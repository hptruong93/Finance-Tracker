package queryAgent.queryComponents;

import java.util.Arrays;
import java.util.List;

import utilities.IJsonable;
import utilities.StringUtility;
import utilities.functional.Filter;
import utilities.functional.Mapper;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;


public class TableFragment implements IJsonable {
	
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
	public JsonRootNode jsonize() {
		JsonRootNode select = JsonNodeFactories.array(new Mapper<String, JsonStringNode>() {
			@Override
			public JsonStringNode map(String input) {
				return JsonNodeFactories.string(input);
			}}.map(this.select));
		JsonStringNode table = JsonNodeFactories.string(this.tableName);
		JsonStringNode alias = JsonNodeFactories.string(this.alias);
		
		JsonRootNode output = JsonNodeFactories.object(
				JsonNodeFactories.field("select", select),
				JsonNodeFactories.field("table", table),
				JsonNodeFactories.field("alias", alias)
				);
		return output;
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
