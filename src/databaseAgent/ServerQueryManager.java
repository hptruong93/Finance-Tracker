package databaseAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.Session;

import utilities.Log;
import utilities.StringUtility;
import utilities.functional.Mapper;
import databaseAgent.queryBuilder.QueryBuilder;
import databaseAgent.queryComponents.RestrictionFragment;
import databaseAgent.queryComponents.TableFragment;

public class ServerQueryManager extends QueryManager {

	private static final int DEFAULT_MAX_COUNT = 100;
	private static final int MAX_QUERY_COUNT = 500;
	private static final String DEFAULT_MODE = "SELECT";
	
	protected String mode;
	
	protected int restrictionCount;
	protected int maxResult = DEFAULT_MAX_COUNT;
	protected int lastQueryCount;
	protected List<String> fields;
	
	private ArrayList<String> constraintStack;
	protected List<TableFragment> from;
	protected List<String> groupBy;
	protected List<RestrictionFragment> criteria;
	protected List<RestrictionFragment> having;
	protected List<String> orderBy;

	public ServerQueryManager() {
		mode = DEFAULT_MODE;
		restrictionCount = -1;
		lastQueryCount = -1;
		fields = new ArrayList<String>();
		
		from = new ArrayList<TableFragment>();
		groupBy = new ArrayList<String>();
		criteria = new ArrayList<RestrictionFragment>();
		having = new ArrayList<RestrictionFragment>();
		orderBy = new ArrayList<String>();
		constraintStack = new ArrayList<String>();
		
		setDefaultField();
		setDefaultFrom();
	}

	@Override
	public Object queryActivity(Session session) {
		String sql = "SELECT ";
		String queryFields = StringUtility.join(fields, ", ");
		String whereClause = "", fromClause = "";
		
		for (TableFragment tf : from) {
			fromClause += tf.toString() + ", ";
		}
		fromClause = StringUtility.removeLast(fromClause, ", ".length());
		
		for (RestrictionFragment rf : criteria) {
			whereClause += rf.getRestriction() + " AND ";
		}
		whereClause = StringUtility.removeLast(whereClause, " AND ".length());
		
		String groupByClause = "";
		for (String groupByField : groupBy) {
			groupByClause += groupByField + ", ";
		}
		groupByClause = StringUtility.removeLast(groupByClause, ", ".length());
		
		String havingClause = "";
		for (RestrictionFragment havingField : having) {
			havingClause += havingField.getRestriction() + ", ";
		}
		havingClause = StringUtility.removeLast(havingClause, ", ".length());
		
		sql += queryFields;
		sql += " FROM " + fromClause;
		
		if (whereClause.length() != 0) {
			sql += " WHERE " + whereClause;
		}
		if (groupByClause.length() != 0) {
			sql += " GROUP BY " + groupByClause;
		}
		if (havingClause.length() != 0) {
			sql += " HAVING " + havingClause;
		}
		if (orderBy.size() != 0) {
			String ordered = StringUtility.join(orderBy, ", ");
			sql += " ORDER BY " + ordered;
		}

		Log.info(this, sql);
		
		Query query = session.createSQLQuery(sql);
		for (RestrictionFragment rf : criteria) {
			for (Entry<String, Object> entry : rf.getVariables().entrySet()) {
				query.setParameter("var" + entry.getKey(), entry.getValue());
			}
		}
		for (RestrictionFragment rf : having) {
			for (Entry<String, Object> entry : rf.getVariables().entrySet()) {
				query.setParameter("var" + entry.getKey(), entry.getValue());
			}
		}
		
		query.setMaxResults(maxResult);
		List<?> result = query.list();
		lastQueryCount = result.size();
		return result;
	}

	@Override
	public void addFrom(TableFragment newComer) {
		this.from.add(newComer);
	}
	
	@Override
	public void setFrom(TableFragment newComer) {
		this.from.clear();
		this.from.add(newComer);
	}
	
	@Override
	public void setFrom(List<TableFragment> newComers) {
		this.from.clear();
		this.from.addAll(newComers);
	}
	
	@Override
	public void setDefaultFrom() {
		from.clear();
		from.add(new TableFragment(QueryBuilder.DEFAULT_DATA_TABLE, null));
	}
	
	@Override
	public void setDefaultField() {
		fields.clear();
		fields.addAll(QueryBuilder.FIELD_LIST);
	}

	@Override
	public boolean addExplicitField(final String name) {
		return fields.add(name);
	}
	
	@Override
	public boolean addField(final String name) {
		return fields.add(name);
	}

	@Override
	public boolean addField(String... names) {
		boolean output = true;
		for (String name : names) {
			output = output && addField(name);
		}
		return output;
	}

	@Override
	public boolean removeField(int index) {
		fields.remove(index);
		return true;
	}
	
	@Override
	public boolean removeField(String name) {
		return fields.remove(name);
	}
	
	@Override
	public void clearFields() {
		fields.clear();
	}

	@Override
	public int addConstraint(RestrictionFragment condition) {
		this.criteria.add(condition);
		restrictionCount++;
		constraintStack.add("c" + (criteria.size() - 1));
		return constraintStack.size() - 1;
	}
	
	@Override
	public int addGroupBy(String groupBy) {
		this.groupBy.add(groupBy);
		restrictionCount++;
		constraintStack.add("g" + (this.groupBy.size() - 1));
		return constraintStack.size() - 1;
	}
	
	@Override
	public int addHaving(RestrictionFragment having) {
		this.having.add(having);
		restrictionCount++;
		constraintStack.add("h" + (this.having.size() - 1));
		return constraintStack.size() - 1;
	}
	
	@Override
	public int addOrderBy(String input) {
		restrictionCount++;
		orderBy.add(input);
		constraintStack.add("o" + (orderBy.size() - 1));
		return constraintStack.size() - 1;
	}
	
	@Override
	public boolean removeConstraint(int index) {
		try {
			int count = Integer.parseInt(constraintStack.get(index).substring(1));
			if (constraintStack.get(index).charAt(0) == 'c') {
				criteria.remove(count);
			} else if (constraintStack.get(index).charAt(0) == 'g') {
				groupBy.remove(count);
			} else if (constraintStack.get(index).charAt(0) == 'h') {
				having.remove(count);
			} else if (constraintStack.get(index).charAt(0) == 'o') {
				orderBy.remove(count);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	public void removeAllConstraints() {
		groupBy.clear();
		criteria.clear();
		having.clear();
		restrictionCount = -1;
		constraintStack.clear();
	}

	/***************************Getters and setters**********************************/

	@Override
	public String getFromString() {
		return StringUtility.join(new Mapper<TableFragment, String>() {
			@Override
			public String map(TableFragment input) {
				return input.toString();
			}}.map(from), ", ");
	}
	
	@Override
	public List<String> getConstraintStrings() {
		List<String> output = new ArrayList<String>();
		
		for (String stack : constraintStack) {
			int count = Integer.parseInt(stack.substring(1));
			
			if (stack.charAt(0) == 'c') {
				output.add(criteria.get(count).toString());
			} else if (stack.charAt(0) == 'g') {
				output.add("GROUP BY " + groupBy.get(count));
			} else if (stack.charAt(0) == 'h') {
				output.add("HAVING " + having.get(count).toString());
			} else if (stack.charAt(0) == 'o') {
				output.add("ORDER BY " + orderBy.get(count));
			}
		}
		
		return output;
	}
	
	@Override
	public int getMaxResult() {
		return maxResult;
	}
	
	@Override
	public boolean setMaxResult(int maxResult) {
		if (maxResult > MAX_QUERY_COUNT) {
			return false;
		} else {
			this.maxResult = maxResult;
			return true;
		}
	}

	@Override
	public List<String> getFields() {
		return fields;
	}
	
	@Override
	public List<TableFragment> getFrom() {
		return this.from;
	}

	@Override
	public List<String> getGroupBy() {
		return this.groupBy;
	}

	@Override
	public List<RestrictionFragment> getCriteria() {
		return this.criteria;
	}

	@Override
	public List<RestrictionFragment> getHaving() {
		return this.having;
	}

	@Override
	public List<String> getOrderBy() {
		return this.orderBy;
	}
}