package queryAgent.dataAnalysis;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;

import queryAgent.QueryManager;
import queryAgent.FeaturedConstraintParser;



public class Feature extends QueryManager {
	
	private static final FeaturedConstraintParser parser = new FeaturedConstraintParser();
	
	private String[] variable;
	private List<Criterion> criteria;
	
	public Feature(String[] variable, String[][] conditionParam) {
		criteria = new ArrayList<Criterion>();
		for (String[] condition : conditionParam) {
			criteria.add(parser.parseConstraint(condition[0], condition[1], condition[2]));
		}
		this.variable = variable;
	}
	
	public void applyFeature(QueryManager dataQuery) {
		dataQuery.clearFields();
		dataQuery.addField(variable);
		dataQuery.removeAllConstraints();
	}
}
