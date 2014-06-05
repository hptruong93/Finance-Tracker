package dataAnalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import purchases.Purchase;
import queryAgent.QueryAgent;
import utilities.Log;
import utilities.Mapper;
import utilities.Util;
import utilities.Verifier;

public class DataQuery {
	
	public static final String DESCRIPTION = "description";
	public static final String QUANTITY = "quantity";
	public static final String UNIT = "unit";
	public static final String COST = "cost";
	public static final String LOCATION = "purchaseSet.location";
	public static final String DATE = "purchaseSet.date";
	
	private static final List<String> PURCHASE_SET_FIELDS = new ArrayList<String>(Arrays.asList("location", "date"));
	private static final List<String> FIELD_LIST = new ArrayList<String>(Arrays.asList("description", "quantity", "unit", "cost", "purchaseSet.location", "purchaseSet.date"));
	private static final Set<String> FULL_FIELD_NAME;
	
	static {
		HashSet<String> temp = new HashSet<String>();
		temp.add("purchase.description");
		temp.add("purchase.type");
		temp.add("purchase.quantity");
		temp.add("purchase.unit");
		temp.add("purchase.cost");
		temp.add("purchase.purchaseSet");
		temp.add("purchase.purchaseSet.location");
		temp.add("purchase.purchaseSet.date");
		
		FULL_FIELD_NAME = Collections.unmodifiableSet(temp);
	}
	
	private List<Criterion> criteria;
	private List<String> fields;

	public DataQuery() {
		criteria = new ArrayList<Criterion>();
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
				
				for (Criterion c : criteria) {
					cr.add(c);
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
			String[] splitVar = fullFunction.replace(")", "").split("(");
			try {
				String function = splitVar[0];
				if (!Function.supportedFunction(function)) {
					return false;
				}
				
				String fieldName = null;
				if (StringUtils.countMatches(splitVar[1], " ") == 1) {
					String[] splitOption = splitVar[1].split(" ");
					fieldName = splitOption[1];
					String option = splitOption[0];
					setFunction(function, fieldName, option);
				} else if (validField(splitVar[1])) {
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
		fields.clear();
		fields.add(function.toUpperCase() + "(p." + field + ")");
	}

	public void setFunction(String function, String field, String option) {
		fields.clear();
		fields.add(function.toUpperCase() + "(" + option.toUpperCase() + " p." + field + ")");
	}

	public static boolean validQueryField(String queryField) {
		if (StringUtils.countMatches(queryField, "(") == 1 
				&& StringUtils.countMatches(queryField, ")") == 1) {//Must be a function then
			String[] splitted = queryField.replace(")", "").split("(");
			try {
				String field;
				if (StringUtils.countMatches(splitted[1], " ") == 1) {//This query has option for the function as well
					String[] splitOption = splitted[1].split(" ");
					field = splitOption[1];
				} else {
					field = splitted[1];
				}
				
				return Function.supportedFunction(splitted[0]) && validField(field);
			} catch (Exception e) {
				return false;
			}
		} else {//Just a plain field
			return validField(queryField);
		}
	}
	
	public void addConstraint(Criterion newComer) {
		this.criteria.add(newComer);
	}

	public void removeConstraint(int index) {
		criteria.remove(index);
	}
	
	public void removeAllConstraints() {
		criteria.clear();
	}
}