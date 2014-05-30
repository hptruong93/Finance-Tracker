package dataAnalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import purchases.Purchase;
import queryAgent.QueryAgent;
import utilities.Mapper;
import utilities.Util;

public class Analyzer {
	
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
	private Object result;

	public Analyzer() {
		criteria = new ArrayList<Criterion>();
		fields = new ArrayList<String>();
		fields.addAll(FIELD_LIST);
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		try {
			Analyzer a = new Analyzer();
			a.clearFields();
//			a.addField("description", "cost");
			a.setFunction("cost", "SUM");
			a.addConstraint(Restrictions.eq("location", "provigo"));
			
			a.query();
			List<Object> out = (List<Object>) a.result;
			for (Object b : out) {
				Object[] instead = (Object[]) b;
				for (Object o : instead) {
					System.out.println("Object is " + o);
				}
				System.out.println("end");
			}
		} finally {
			QueryAgent.closeFactory();
		}
	}

	public void query() {
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
				
				Query q = session.createQuery("SELECT " + Util.join(appendedFields, ", ") + " FROM Purchase as p LEFT JOIN p.purchaseSet");
				return q.list();
			}
		};
		result = test.query();
	}

	public void setDefaultField() {
		fields.clear();
		fields.addAll(FIELD_LIST);
	}

	public boolean addField(String name) {
		if (FIELD_LIST.contains(name)) {
			return fields.add(name);
		} else {
			return false;
		}
	}

	public boolean addField(String... names) {
		boolean output = true;
		for (String name : names) {
			output = output && addField(name);
		}
		return output;
	}

	public void clearFields() {
		fields.clear();;
	}
	
	public void setFunction(String field, String function) {
		fields.clear();
		fields.add(function.toUpperCase() + "(p." + field + ")");
	}

	public void setFunction(String field, String function, String option) {
		fields.clear();
		fields.add(function.toUpperCase() + "(" + option.toUpperCase() + " p." + field + ")");
	}

	public void addConstraint(Criterion newComer) {
		this.criteria.add(newComer);
	}

	public void removeConstraint(int index) {
		criteria.remove(index);
	}
	
	public void clearConstraints() {
		criteria.clear();
	}
	
	public Object getResult() {
		return this.result;
	}
}