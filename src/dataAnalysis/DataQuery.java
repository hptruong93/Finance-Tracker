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
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import purchases.Purchase;
import queryAgent.QueryAgent;
import utilities.functional.Verifier;

public class DataQuery {

	public static final String PURCHASE_SET_TABLE = "purchaseSet";
	
	public static final List<String> SUPPORTED_CONDITION = Collections.unmodifiableList( 
			new ArrayList<String>(Arrays.asList("BETWEEN", "EQUAL", "NOT_EQUAL", "GREATER_THAN", "LESS_THAN", 
					"LIKE", "ILIKE", "IS_EMPTY",	"IS_NOT_EMPTY", "IS_NOT_NULL", "IS_NULL")));
	
	private static final Set<String> SUPPORTED_COUNT_OPTION = Collections.unmodifiableSet( 
			new HashSet<String>(Arrays.asList("DISTINCT", "ALL")));
	
	public static final Set<String> SUPPORTED_FUNCTIONS = Collections.unmodifiableSet( 
			new HashSet<String>(Arrays.asList("SUM", "AVG", "MIN", "MAX", "COUNT")));
	
	public static final List<String> PURCHASE_SET_FIELDS = Collections.unmodifiableList(
			new ArrayList<String>(Arrays.asList("location", "date")));
	
	public static final List<String> FIELD_LIST = 
			Collections.unmodifiableList(new ArrayList<String>(Arrays.asList
					("id", "description", "type", "quantity", "unit", "cost", 
							PURCHASE_SET_TABLE + ".location", PURCHASE_SET_TABLE + ".date")));
	
	private static final int DEFAULT_MAX_COUNT = 100;
	
	private int restrictionCount;
	private int maxResult = DEFAULT_MAX_COUNT;
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
		QueryAgent<Object> query = new QueryAgent<Object>() {
			@Override
			public Object queryActivity(Session session) {
				Criteria cr = session.createCriteria(Purchase.class, "p").createAlias("p.purchaseSet", "purchaseSet");
				for (Criterion c : criteria.values()) {
					cr.add(c);
				}

				for (Junction j : junctions.values()) {
					cr.add(j);
				}

				final ProjectionList p = Projections.projectionList();
				for (String input : fields) {
					HashMap<String, String> parsed = parseQueryField(input);
					String field = parsed.get("field");
					if (PURCHASE_SET_FIELDS.contains(field)) {
						field = "purchaseSet." + field;
					}

					String option = parsed.get("option");
					String function = parsed.get("function");

					if (function == null) {
						p.add(Projections.property(field));
					} else {
						switch (function) {
						case "SUM":
							p.add(Projections.sum(field));
							break;
						case "COUNT":
							if (option == null) {
								p.add(Projections.count(field));
							} else if (option.equals("DISTINCT")) {
								p.add(Projections.countDistinct(field));
							}
							break;
						case "AVG":
							p.add(Projections.avg(field));
							break;
						case "MIN":
							p.add(Projections.min(field));
							break;
						case "MAX":
							p.add(Projections.max(field));
							break;
						default:
							throw new IllegalStateException("Invalid field?? Check insertion in the first place."); 
						}
					}
				}						

				cr.setProjection(p);
				cr.setMaxResults(maxResult);
				return cr.list();

			}
		};
		return query.query();
	}

	public void setDefaultField() {
		fields.clear();
		fields.addAll(FIELD_LIST);
	}

	public boolean addField(final String name) {
		boolean okToAdd = validField(name);
		
		if (okToAdd) {
			return fields.add(name);
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
		HashMap<String, String> parsed = parseQueryField(fullFunction);
		if (parsed.get("field") == null || parsed.get("function") == null) {
			return false;
		} else {
			if (parsed.get("option") == null) {
				setFunction(parsed.get("function"), parsed.get("field"));
			} else {
				setFunction(parsed.get("function"), parsed.get("field"), parsed.get("option"));
			}
			return true;
		}
	}
	
	public void setFunction(String function, String field) {
		fields.add(function.toUpperCase() + "(" + field + ")");
	}

	public void setFunction(String function, String field, String option) {
		fields.add(function.toUpperCase() + "(" + option.toUpperCase() + field + ")");
	}

	public static boolean validQueryField(String queryField) {
		HashMap<String, String> parsed = parseQueryField(queryField);
		return parsed.get("field") != null;
	}
	
	private static HashMap<String, String> parseQueryField(String queryField) {
		HashMap<String, String> output = new HashMap<String, String>();
		String field, function, option;
		
		int countOpen = StringUtils.countMatches(queryField, "(");
		int countClose = StringUtils.countMatches(queryField, ")");
		
		if (countOpen != 1 || countClose != 1) {
			if (validField(queryField)) {
				output.put("field", queryField);
			} else {
				return output;
			}
		} else {
			String[] splitVar = queryField.replace(")", "").split("\\(");
			try {
				function = splitVar[0];
				if (!SUPPORTED_FUNCTIONS.contains(function)) {
					return output;
				}
				
				field = null;
				if (StringUtils.countMatches(splitVar[1], " ") == 1) {
					String[] splitOption = splitVar[1].split(" ");
					field = splitOption[1];
					option = splitOption[0];
					if (SUPPORTED_COUNT_OPTION.contains(option) && function.toUpperCase().equals("COUNT")) {
						output.put("function", function);
						output.put("field", field);
						output.put("option", option);
					} else {
						return output;
					}
				} else if (validField(splitVar[1])) {
					field = splitVar[1];
					output.put("function", function);
					output.put("field", field);
				} else {
					return output;
				}
			} catch (Exception e) {
				return output;
			}
		}
		return output;
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
	
	public int getMaxResult() {
		return maxResult;
	}
	
	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}
}