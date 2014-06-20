package queryAgent.dataAnalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import queryAgent.QueryManager;
import queryAgent.queryBuilder.PlainBuilder;
import queryAgent.queryBuilder.QueryBuilder;
import queryAgent.queryBuilder.SQLTranslator;
import queryAgent.queryBuilder.TranslatorFactory;
import queryAgent.queryComponents.RestrictionFragment;
import queryAgent.queryComponents.TableFragment;
import utilities.FileUtility;
import argo.jdom.JsonNode;

public class Feature {

	public static final List<Feature> DEFAULT_FEATURES;

	protected boolean isAdvanced;
	protected List<TableFragment> from;
	protected List<RestrictionFragment> criteria;
	protected List<RestrictionFragment> having;
	protected List<String> orderedBy;
	protected List<String> groupBy;
	protected List<String> fields;
	protected SQLTranslator translator;
	protected QueryBuilder builder;
	protected String name;
	protected String description;

	public Feature() {
		from = new ArrayList<TableFragment>();
		criteria = new ArrayList<RestrictionFragment>();
		having = new ArrayList<RestrictionFragment>();
		orderedBy = new ArrayList<String>();
		groupBy = new ArrayList<String>();
		fields = new ArrayList<String>();

		this.translator = TranslatorFactory.getTranslator(TranslatorFactory.FEATURED_TRANSLATOR);
	}

	static {
		List<Feature> tempStorage = new ArrayList<Feature>();
		JsonNode t = FileUtility.readJSON(new File(FileUtility.joinPath("data", "featured.json")));
		for (JsonNode sub : t.getArrayNode("content")) {
			Feature feature = new Feature();
			feature.loadConfig(sub);
			tempStorage.add(feature);
		}

		DEFAULT_FEATURES = Collections.unmodifiableList(tempStorage);
	}

	public void loadConfig(JsonNode node) {
		clearConfig();

		name = node.getStringValue("name");
		description = node.getStringValue("description");

		isAdvanced = node.getStringValue("mode").equals("advanced");

		if (isAdvanced) {
			builder = new PlainBuilder();
			for (JsonNode sub : node.getArrayNode("fields")) {
				fields.add(sub.getText());
			}
			
			for (JsonNode sub : node.getArrayNode("from")) {
				String select = sub.getStringValue("select");
				String table = sub.getStringValue("table");
				String alias = sub.getStringValue("alias");
				from.add(new TableFragment(Arrays.asList(select.split(", ")), table, alias));
			}
			
			for (JsonNode sub : node.getArrayNode("criteria")) {
				String condition = sub.getStringValue("condition");
				RestrictionFragment rf = builder.buildConstraint(null, condition, null);
				criteria.add(rf);
			}

			for (JsonNode sub : node.getArrayNode("group_by")) {
				groupBy.add(builder.buildGroupBy(sub.getText()));
			}

			for (JsonNode sub : node.getArrayNode("having")) {
				String condition = sub.getStringValue("condition");
				RestrictionFragment rf = builder.buildConstraint(null, condition, null);
				having.add(rf);
			}

			for (JsonNode sub : node.getArrayNode("order_by")) {
				String field = sub.getStringValue("field");
				orderedBy.add(builder.buildOrderBy(field, null));
			}
		} else {
			builder = new QueryBuilder(this.translator);
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
		
	}

	public void loadConfig(QueryManager manager, String name, String description, boolean isAdvanced) {
		clearConfig();
		this.name = name;
		this.description = description;
		
		this.isAdvanced = isAdvanced;
		this.from.addAll(manager.getFrom());
		this.criteria.addAll(manager.getCriteria());
		this.having.addAll(manager.getHaving());
		this.groupBy.addAll(manager.getGroupBy());
		this.orderedBy.addAll(manager.getOrderBy());
		this.fields.addAll(manager.getFields());
	}
	
	public final void clearConfig() {
		isAdvanced = false;
		from.clear();
		criteria.clear();
		having.clear();
		groupBy.clear();
		orderedBy.clear();
		fields.clear();
	}

	public JsonNode dumpConfigJSON() {
		return null;
	}
	
	public final List<Integer> apply(QueryManager queryManager) {
		List<Integer> constraintID = new ArrayList<Integer>();
		
		queryManager.removeAllConstraints();
		
		if (!isAdvanced) {
			queryManager.setDefaultFrom();
		} else {
			queryManager.setFrom(from);
		}
		
		for (RestrictionFragment rf : criteria) {
			constraintID.add(queryManager.addConstraint(rf));
		}

		for (RestrictionFragment rf : having) {
			constraintID.add(queryManager.addHaving(rf));
		}

		for (String group : groupBy) {
			constraintID.add(queryManager.addGroupBy(group));
		}

		if (!fields.isEmpty()) {
			queryManager.clearFields();
			if (isAdvanced) {
				for (String field : fields) {
					queryManager.addField(field);
				}
			} else {
				for (String field : fields) {
					queryManager.addField(translator.fieldDetranslate(field));
				}
			}
		} else {
			queryManager.setDefaultField();
		}

		for (String order : orderedBy) {
			constraintID.add(queryManager.addOrderBy(order));
		}
		return constraintID;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}
	
	public boolean isAdvanced() {
		return isAdvanced;
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();

		out.append("Feature name: " + this.name + "\n");
		out.append("Feature description: " + description + "\n");
		out.append("SELECT ");
		for (String field : fields) {
			out.append(field + ", ");
		}
		out.delete(out.length() - ", ".length(), out.length());

		if (!criteria.isEmpty()) {
			out.append(" WHERE ");
			for (RestrictionFragment rf : criteria) {
				out.append(rf.toString() + " AND ");
			}
			out.delete(out.length() - " AND ".length(), out.length());
		}

		if (!groupBy.isEmpty()) {
			out.append(" GROUP BY ");
			for (String group : groupBy) {
				out.append(group + ", ");
			}
			out.delete(out.length() - ", ".length(), out.length());
		}

		if (!having.isEmpty()) {
			out.append(" HAVING ");
			for (RestrictionFragment rf : having) {
				out.append(rf.toString() + " AND ");
			}
			out.delete(out.length() - " AND ".length(), out.length());
		}

		if (!orderedBy.isEmpty()) {
			out.append(" ORDER BY ");
			for (String order : orderedBy) {
				out.append(order + ", ");
			}
			out.delete(out.length() - ", ".length(), out.length());
		}

		return out.toString();
	}
}
