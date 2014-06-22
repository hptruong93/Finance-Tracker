package databaseAgent.dataAnalysis;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import utilities.IJsonable;
import utilities.JSONUtility;
import utilities.functional.Mapper;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import databaseAgent.QueryManager;
import databaseAgent.queryBuilder.PlainBuilder;
import databaseAgent.queryBuilder.QueryBuilder;
import databaseAgent.queryBuilder.SQLTranslator;
import databaseAgent.queryBuilder.TranslatorFactory;
import databaseAgent.queryComponents.RestrictionFragment;
import databaseAgent.queryComponents.TableFragment;

public class Feature implements IJsonable {

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
//		JsonNode t = FileUtility.readJSON(new File(FileUtility.joinPath("data", "featured.json")));
//		for (JsonNode sub : t.getArrayNode("features")) {
//			Feature feature = new Feature();
//			feature.loadConfig(sub);
//			tempStorage.add(feature);
//		}

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
				List<String> select = new Mapper<JsonNode, String>() {
					@Override
					public String map(JsonNode input) {
						return input.getText();
					}
				}.map(sub.getArrayNode("select"));
				
				String table = sub.getStringValue("table");
				String alias = sub.getStringValue("alias");
				from.add(new TableFragment(select, table, alias));
			}
			
			for (JsonNode sub : node.getArrayNode("criteria")) {
//				String condition = sub.getStringValue("condition");
//				RestrictionFragment rf = builder.buildConstraint(null, condition, null);
				RestrictionFragment rf = new RestrictionFragment(sub);
				criteria.add(rf);
			}

			for (JsonNode sub : node.getArrayNode("group_by")) {
				groupBy.add(builder.buildGroupBy(sub.getText()));
			}

			for (JsonNode sub : node.getArrayNode("having")) {
				String condition = sub.getStringValue("condition");
//				RestrictionFragment rf = builder.buildConstraint(null, condition, null);
				RestrictionFragment rf = new RestrictionFragment(sub);
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
//				String field = sub.getStringValue("field");
//				String condition = sub.getStringValue("condition");
//				String value = sub.getStringValue("value");
//				RestrictionFragment rf = builder.buildConstraint(field, condition, value);
				RestrictionFragment rf = new RestrictionFragment(sub);
				criteria.add(rf);
			}

			for (JsonNode sub : node.getArrayNode("group_by")) {
				groupBy.add(builder.buildGroupBy(sub.getText()));
			}

			for (JsonNode sub : node.getArrayNode("having")) {
//				String field = sub.getStringValue("field");
//				String condition = sub.getStringValue("condition");
//				String value = sub.getStringValue("value");
//				RestrictionFragment rf = builder.buildConstraint(field, condition, value);
				RestrictionFragment rf = new RestrictionFragment(sub);
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
	
	public static void main(String[] args) {
		Feature t = DEFAULT_FEATURES.get(DEFAULT_FEATURES.size() - 1);
		System.out.println(t.criteria.get(0).toString());
			String n = JSONUtility.jsonToString(t.jsonize());
			System.out.println(n);
	}
	
	@Override
	public JsonRootNode jsonize() {
		JsonStringNode name = JsonNodeFactories.string(this.name);
		JsonStringNode description = JsonNodeFactories.string(this.description);
		JsonStringNode mode = JsonNodeFactories.string(isAdvanced ? "advanced" : "normal");
		JsonNode fields, from;
		if (isAdvanced) {
			fields = JsonNodeFactories.array(new Mapper<String, JsonStringNode>() {
				@Override
				public JsonStringNode map(String input) {
					return JsonNodeFactories.string(input);
				}
			}.map(this.fields));
			from = JsonNodeFactories.array(new Mapper<TableFragment, JsonRootNode>(){
				@Override
				public JsonRootNode map(TableFragment input) {
					return input.jsonize();
				}}.map(this.from));
		} else {
			fields = JsonNodeFactories.array(new Mapper<String, JsonStringNode>() {
				@Override
				public JsonStringNode map(String input) {
					return JsonNodeFactories.string(translator.fieldDetranslate(input));
				}
			}.map(this.fields));
			from = JsonNodeFactories.array(new ArrayList<JsonStringNode>());
		}
		
		
		JsonNode criteria = JsonNodeFactories.array(new Mapper<RestrictionFragment, JsonRootNode>(){
			@Override
			public JsonRootNode map(RestrictionFragment input) {
				return input.jsonize();
			}}.map(this.criteria));
		
		JsonNode groupBy = JsonNodeFactories.array(new Mapper<String, JsonStringNode>(){
			@Override
			public JsonStringNode map(String input) {
				return JsonNodeFactories.string(input);
			}}.map(this.groupBy));
		
		JsonNode having = JsonNodeFactories.array(new Mapper<RestrictionFragment, JsonRootNode>(){
			@Override
			public JsonRootNode map(RestrictionFragment input) {
				return input.jsonize();
			}}.map(this.having));
		
		JsonNode orderBy = JsonNodeFactories.array(new Mapper<String, JsonStringNode>(){
			@Override
			public JsonStringNode map(String input) {
				return JsonNodeFactories.string(input);
			}}.map(this.orderedBy));
		
		JsonRootNode output = JsonNodeFactories.object(
				JsonNodeFactories.field("name", name),
				JsonNodeFactories.field("description", description),
				JsonNodeFactories.field("mode", mode),
				JsonNodeFactories.field("fields", fields),
				JsonNodeFactories.field("from", from),
				JsonNodeFactories.field("criteria", criteria),
				JsonNodeFactories.field("group_by", groupBy),
				JsonNodeFactories.field("having", having),
				JsonNodeFactories.field("order_by", orderBy));
		
		return output;
	}
}
