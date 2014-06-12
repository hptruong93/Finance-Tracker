package queryAgent.dataAnalysis;

import java.util.ArrayList;
import java.util.List;

import queryAgent.HQLTranslator;
import queryAgent.QueryBuilder;
import queryAgent.QueryManager;
import queryAgent.RestrictionFragment;
import queryAgent.TranslatorFactory;
import argo.jdom.JsonNode;


public class Feature {
	protected List<RestrictionFragment> criteria;
	protected List<RestrictionFragment> having;
	protected List<String> orderedBy;
	protected List<String> groupBy;
	protected List<String> fields;
	protected HQLTranslator translator;
	protected QueryBuilder builder;
	protected String name;
	protected String description;
	
	public Feature() {
		criteria = new ArrayList<RestrictionFragment>();
		having = new ArrayList<RestrictionFragment>();
		orderedBy = new ArrayList<String>();
		groupBy = new ArrayList<String>();
		fields = new ArrayList<String>();
		
		this.translator = TranslatorFactory.getTranslator(TranslatorFactory.FEATURED_TRANSLATOR);
		builder = new QueryBuilder(this.translator);
	}
	
	public void loadConfig(JsonNode node) {
		clearConfig();
		
		name = node.getStringValue("name");
		description = node.getStringValue("description");
		
		for (JsonNode sub : node.getArrayNode("fields")) {
			fields.add(translator.fieldTranslate(sub.getText()));
		}
		
		for (JsonNode sub : node.getArrayNode("criteria")) {
			String field = sub.getStringValue("field");
			String condition = sub.getStringValue("condition");
			String value = sub.getStringValue("value");
			RestrictionFragment rf = builder.buildConstraint(field, condition, value);
			criteria.add(rf);
		}
		
		for (JsonNode sub : node.getArrayNode("group_by")) {
			groupBy.add(builder.buildGroupBy(sub.getText()));
		}

		for (JsonNode sub : node.getArrayNode("having")) {
			String field = sub.getStringValue("field");
			String condition = sub.getStringValue("condition");
			String value = sub.getStringValue("value");
			RestrictionFragment rf = builder.buildConstraint(field, condition, value);
			having.add(rf);
		}
		
		for (JsonNode sub : node.getArrayNode("order_by")) {
			String field = sub.getStringValue("field");
			String option = sub.getStringValue("option");
			orderedBy.add(builder.buildOrderBy(field, option));
		}
	}
	
	public final void clearConfig() {
		criteria.clear();
		having.clear();
		groupBy.clear();
		orderedBy.clear();
		fields.clear();
	}
	
	public final void apply(QueryManager queryManager) {
		queryManager.removeAllConstraints();
		for (RestrictionFragment rf : criteria) {
			queryManager.addConstraint(rf);
		}
		
		for (RestrictionFragment rf : having) {
			queryManager.addHaving(rf);
		}
		
		for (String group : groupBy) {
			queryManager.addGroupBy(group);
		}
		
		if (!fields.isEmpty()) {
			queryManager.clearFields();
			for (String field : fields) {
				queryManager.addField(field);
			}
		} else {
			queryManager.setDefaultField();
		}
		
		for (String order : orderedBy) {
			queryManager.addOrderBy(order);
		}
	}
}
