package queryAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.Session;

import utilities.Log;
import utilities.StringUtility;

public class QueryManager extends QueryAgent<Object> {

	private static final int DEFAULT_MAX_COUNT = 100;
	private static final String DEFAULT_MODE = "SELECT";
	
	protected String mode;
	
	protected int restrictionCount;
	protected int maxResult = DEFAULT_MAX_COUNT;
	protected int lastQueryCount;
	protected List<String> fields;
	
	private ArrayList<String> constraintStack;
	protected List<String> groupBy;
	protected List<RestrictionFragment> criteria;
	protected List<RestrictionFragment> having;
	protected List<String> orderBy;

	public QueryManager() {
		mode = DEFAULT_MODE;
		restrictionCount = -1;
		lastQueryCount = -1;
		fields = new ArrayList<String>();
		fields.addAll(QueryBuilder.FIELD_LIST);
		
		groupBy = new ArrayList<String>();
		criteria = new ArrayList<RestrictionFragment>();
		having = new ArrayList<RestrictionFragment>();
		orderBy = new ArrayList<String>();
		constraintStack = new ArrayList<String>();
	}

	@Override
	public Object queryActivity(Session session) {
		String hq = "SELECT ";
		String queryFields = StringUtility.join(fields, ", ");
		String whereClause = "";
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
			havingClause += havingField + ", ";
		}
		havingClause = StringUtility.removeLast(havingClause, ", ".length());
		
		hq += queryFields;
		hq += " FROM Purchase as p LEFT JOIN p.purchaseSet";
		if (whereClause.length() != 0) {
			hq += " WHERE " + whereClause;
		}
		if (groupByClause.length() != 0) {
			hq += " GROUP BY " + groupByClause;
		}
		if (havingClause.length() != 0) {
			hq += " HAVING " + havingClause;
		}
		if (orderBy.size() != 0) {
			String ordered = StringUtility.join(orderBy, ", ");
			hq += " ORDER BY " + ordered;
		}

		Log.info(this, hq);
		
		Query query = session.createQuery(hq);
		for (RestrictionFragment rf : criteria) {
			for (Entry<Integer, Object> entry : rf.getVariables().entrySet()) {
				query.setParameter("var" + entry.getKey(), entry.getValue());
			}
		}
		for (RestrictionFragment rf : having) {
			for (Entry<Integer, Object> entry : rf.getVariables().entrySet()) {
				query.setParameter("var" + entry.getKey(), entry.getValue());
			}
		}
		
		query.setMaxResults(maxResult);
		List<?> result = query.list();
		lastQueryCount = result.size();
		return result;
//		return new ArrayList<Integer>(Arrays.asList(1, 2));
	}

	public void setDefaultField() {
		fields.clear();
		fields.addAll(QueryBuilder.FIELD_LIST);
	}

	public boolean addField(final String name) {
		boolean okToAdd = QueryBuilder.validSelect(name);
		
		if (okToAdd) {
			return fields.add(TranslatorFactory.getTranslator(TranslatorFactory.STANDARD_TRANSLATOR).fieldTranslate(name));
		}
		return false;
	}

	public boolean addField(String... names) {
		boolean output = true;
		for (String name : names) {
			output = output && addField(name);
		}
		return output;
	}

	public void removeField(int index) {
		fields.remove(index);
	}
	
	public boolean removeField(String name) {
		return fields.remove(name);
	}
	
	public void clearFields() {
		fields.clear();
	}

	public int addConstraint(RestrictionFragment condition) {
		this.criteria.add(condition);
		restrictionCount++;
		constraintStack.add("c" + (criteria.size() - 1));
		return constraintStack.size() - 1;
	}
	
	public int addGroupBy(String groupBy) {
		this.groupBy.add(groupBy);
		restrictionCount++;
		constraintStack.add("g" + (this.groupBy.size() - 1));
		return constraintStack.size() - 1;
	}
	
	public int addHaving(RestrictionFragment having) {
		this.having.add(having);
		restrictionCount++;
		constraintStack.add("h" + (this.having.size() - 1));
		return constraintStack.size() - 1;
	}
	
	public int addOrderBy(String input) {
		restrictionCount++;
		orderBy.add(input);
		constraintStack.add("o" + (orderBy.size() - 1));
		return constraintStack.size() - 1;
	}
	
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
	
	public List<String> getFields() {
		return fields;
	}
	
	public List<String> getConstraints() {
		List<String> output = new ArrayList<String>();
		
		for (String stack : constraintStack) {
			int count = Integer.parseInt(stack.substring(1));
			
			if (stack.charAt(0) == 'c') {
				output.add(criteria.get(count).toString());
			} else if (stack.charAt(0) == 'g') {
				output.add(groupBy.get(count));
			} else if (stack.charAt(0) == 'h') {
				output.add(having.get(count).toString());
			} else if (stack.charAt(0) == 'o') {
				output.add(orderBy.get(count));
			}
		}
		
		return output;
	}
	
	public void removeAllConstraints() {
		groupBy.clear();
		criteria.clear();
		having.clear();
		restrictionCount = -1;
		constraintStack.clear();
	}
	
	public int getMaxResult() {
		return maxResult;
	}
	
	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}
}