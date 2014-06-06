package dataAnalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;

import purchases.Purchase;
import queryAgent.QueryAgent;
import utilities.Log;
import utilities.Mapper;
import utilities.Util;
import utilities.Verifier;

public class DataQuery {
	
	public static final List<String> SUPPORTED_CONDITION = Collections.unmodifiableList( 
			new ArrayList<String>(Arrays.asList("BETWEEN", "EQUAL", "NOT_EQUAL", "GREATER_THAN", "LESS_THAN", 
					"LIKE", "ILIKE", "IS_EMPTY",	"IS_NOT_EMPTY", "IS_NOT_NULL", "IS_NULL")));
	
	private static final Set<String> SUPPORTED_COUNT_OPTION = Collections.unmodifiableSet( 
			new HashSet<String>(Arrays.asList("DISTINCT", "ALL")));
	
	public static final Set<String> SUPPORTED_FUNCTIONS = Collections.unmodifiableSet( 
			new HashSet<String>(Arrays.asList("SUM", "AVG", "MIN", "MAX", "COUNT")));
	
	private static final List<String> PURCHASE_SET_FIELDS = new ArrayList<String>(Arrays.asList("location", "date"));
	public static final List<String> FIELD_LIST = 
			Collections.unmodifiableList(new ArrayList<String>(Arrays.asList
					("id", "description", "type", "quantity", "unit", "cost", "purchaseSet.location", "purchaseSet.date")));
	private static final Set<String> FULL_FIELD_NAME;
	
	static {
		HashSet<String> temp = new HashSet<String>(Mapper.appender("purchase.", "").map(FIELD_LIST));
		FULL_FIELD_NAME = Collections.unmodifiableSet(temp);
	}
	
	private int restrictionCount;
	private Map<Integer, Criterion> criteria;
	private Map<Integer, Junction> junctions;
	private List<String> fields;

	public DataQuery() {
		restrictionCount = -1;
		criteria = new HashMap<Integer, Criterion>();
		junctions = new HashMap<Integer, Junction>();
		fields = new ArrayList<String>();
		fields.addAll(FIELD_LIST);
	}

	public Object query() {
		QueryAgent<Object> test = new QueryAgent<Object>() {
			@Override
			public Object queryActivity(Session session) {
				Criteria cr = session.createCriteria(Purchase.class, "purchase");
				for (String fullName : FULL_FIELD_NAME) {
					String[] splitted = fullName.split("\\.");
					cr.createAlias(fullName, splitted[splitted.length - 1]);
				}
				
				for (Criterion c : criteria.values()) {
					cr.add(c);
				}
				
				for (Junction j : junctions.values()) {
					cr.add(j);
				}
				
				List<String> appendedFields = new Mapper<String, String>() {
					@Override
					public String map(String input) {
						if (input.contains("(")) {
							return input;
						} else {
							return "p." + input;	
						}
					}
				}.map(fields);
				
				String toQuery = "SELECT " + Util.join(appendedFields, ", ") + " FROM Purchase as p LEFT JOIN p.purchaseSet";
				Log.info(this, toQuery);
				Query q = session.createQuery(toQuery);
				return q.list();
			}
		};
		return test.query();
	}

	public void setDefaultField() {
		fields.clear();
		fields.addAll(FIELD_LIST);
	}

	public boolean addField(final String name) {
		boolean okToAdd = validField(name);
		
		if (okToAdd) {
			String toAdd = name;
			if (PURCHASE_SET_FIELDS.contains(name)) {
				toAdd = "purchaseSet." + toAdd;
			}
			return fields.add(toAdd);
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

	public static boolean validField(final String name) {
		return !(new Verifier<String>(){
			@Override
			public boolean verifyAction(String item) {
				String[] parts = item.split("\\.");
				return !parts[parts.length - 1].equals(name);
			}
		}.verify(FIELD_LIST));
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

	public boolean setFunction(String fullFunction) {
		if (StringUtils.countMatches(fullFunction, "(") != 1 ||
				StringUtils.countMatches(fullFunction, ")") != 1) {
			return false;
		} else {
			String[] splitVar = fullFunction.replace(")", "").split("\\(");
			try {
				String function = splitVar[0];
				if (!SUPPORTED_FUNCTIONS.contains(function)) {
					return false;
				}
				
				String fieldName = null;
				if (StringUtils.countMatches(splitVar[1], " ") == 1) {
					String[] splitOption = splitVar[1].split(" ");
					fieldName = splitOption[1];
					String option = splitOption[0];
					System.out.println("HERE instead");
					if (SUPPORTED_COUNT_OPTION.contains(option) && function.toUpperCase().equals("COUNT")) {
						System.out.println("OK");
						setFunction(function, fieldName, option);
					} else {
						return false;
					}
					
				} else if (validField(splitVar[1])) {
					System.out.println("HERE");
					fieldName = splitVar[1];
					setFunction(function, fieldName);
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	public void setFunction(String function, String field) {
		fields.add(function.toUpperCase() + "(p." + field + ")");
	}

	public void setFunction(String function, String field, String option) {
		fields.add(function.toUpperCase() + "(" + option.toUpperCase() + " p." + field + ")");
	}

	public static boolean validQueryField(String queryField) {
		String field, option, function;
		if (StringUtils.countMatches(queryField, "(") == 1 
				&& StringUtils.countMatches(queryField, ")") == 1) {//Must be a function then
			String[] splitted = queryField.replace(")", "").split("\\(");
			function = splitted[0];
			try {
				if (StringUtils.countMatches(splitted[1], " ") == 1) {//This query has option for the function as well
					String[] splitOption = splitted[1].split(" ");
					option = splitOption[0];
					field = splitOption[1];
					
					if (!SUPPORTED_COUNT_OPTION.contains(option) || !function.toUpperCase().equals("COUNT")) {
						return false;
					}
				} else {
					field = splitted[1];
				}
				
				return SUPPORTED_FUNCTIONS.contains(function) && validField(field);
			} catch (Exception e) {
				return false;
			}
		} else {//Just a plain field
			return validField(queryField);
		}
	}
	
	public int addCriterion(Criterion newComer) {
		restrictionCount++;
		this.criteria.put(restrictionCount, newComer);
		return restrictionCount;
	}

	public int addJunction(Junction newComer) {
		restrictionCount++;
		this.junctions.put(restrictionCount, newComer);
		return restrictionCount;
	}

	public void removeConstraint(int index) {
		if (criteria.remove(index) == null) {
			junctions.remove(index);
		}
	}
	
	public void removeAllConstraints() {
		criteria.clear();
		junctions.clear();
	}
}