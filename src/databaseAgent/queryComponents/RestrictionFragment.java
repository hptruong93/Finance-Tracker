package databaseAgent.queryComponents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utilities.IJsonable;
import utilities.functional.Mapper;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import databaseAgent.queryBuilder.QueryBuilder;
import databaseAgent.queryBuilder.SQLTranslator;
import databaseAgent.queryBuilder.TranslatorFactory;
import databaseAgent.queryComponents.RestrictionTree.RestrictionNode;

public class RestrictionFragment implements IJsonable {
	
	public static final Pattern VAR_PATTERN = Pattern.compile(":var[0-9\\.]+");
	private String restriction;
	private Map<String, Object> variables;
	
	public RestrictionFragment(String restriction, Map<String, Object> variables) {
		this.restriction = restriction;
		this.variables = Collections.unmodifiableMap(variables);
	}
	
	public static void main(String[] args) {
		
		/*
		 * {
					"condition": {
						"field": "MONTH(purchase_set.date)",
						"condition": "=",
						"value": ":var1"
					},
					"values": [
						{
							"id": "1",
							"value": "6",
							"type": "java.lang.Integer"
						}
					]
				}
		 */
		JsonNode condition = JsonNodeFactories.object(
				JsonNodeFactories.field("field", JsonNodeFactories.string("type")),
				JsonNodeFactories.field("condition", JsonNodeFactories.string("<>")),
				JsonNodeFactories.field("value", JsonNodeFactories.string(":var1")));
				
		JsonNode values = JsonNodeFactories.object(
				JsonNodeFactories.field("id", JsonNodeFactories.string("1")),
				JsonNodeFactories.field("value", JsonNodeFactories.string("6")),
				JsonNodeFactories.field("type", JsonNodeFactories.string("#FEATURED:FOOD")));
				
		JsonRootNode x = JsonNodeFactories.object(
				JsonNodeFactories.field("condition", condition),
				JsonNodeFactories.field("values", JsonNodeFactories.array(values)));
		
		RestrictionFragment rf = new RestrictionFragment(x);
		System.out.println(rf.toString());
	}
	
	public RestrictionFragment(JsonNode node) {
		RestrictionNode nodeForm = new RestrictionNode(node.getNode("condition"));
		
		HashMap<String, Object> varTemp = new HashMap<String, Object>();
		
		SQLTranslator translator = TranslatorFactory.getTranslator(TranslatorFactory.FEATURED_TRANSLATOR);
		List<JsonNode> values = node.getArrayNode("values");
		for (JsonNode sub : values) {
			String id = sub.getStringValue("id");
			Object[] val = translator.valueParse(sub.getStringValue("value"), sub.getStringValue("type"));
			if (val.length == 1) {
				varTemp.put(id, val[0]);
			} else {
				RestrictionNode original = nodeForm.findVar(id);
				RestrictionNode sample = original.cloneLeaf();
				if (sample == null) {
					throw new IllegalStateException("Node is not leaf...");
				}
				
				original.changeVar(id + ".0");
				varTemp.put(id + ".0", val[0]);
				
				String joiner = node.getNode("condition").getStringValue("condition");
				joiner = SQLTranslator.TRANSLATED_CONDITION.getKey(joiner);
				joiner = SQLTranslator.CONDITION_JOINER.get(joiner);
				
				for (int i = 1; i < val.length; i++) {
					RestrictionNode toBeJoined = sample.cloneLeaf();
					toBeJoined.changeVar(id + "." + i);
					original.join(toBeJoined, joiner);
					varTemp.put(id + "." + i, val[i]);
				}
			}
		}
		
		variables = Collections.unmodifiableMap(varTemp);
		this.restriction = nodeForm.toString();
	}
	
	public void join(RestrictionFragment other, String joiner) {
		this.restriction = QueryBuilder.joinCondition(new String[] {this.restriction, other.restriction}, joiner);
		variables.putAll(other.variables);
	}
	
	public void and(RestrictionFragment other) {
		join(other, "AND");
	}
	
	public void or(RestrictionFragment other) {
		join(other, "OR");
	}
	
	public void xor(RestrictionFragment other) {
		join(other, "XOR");
	}
	
	public RestrictionFragment not() {
		if (restriction.startsWith("NOT(") && restriction.endsWith(")")) {
			restriction = restriction.substring(4, restriction.length() - 1);
		} else {
			restriction = "NOT(" + restriction + ")";
		}
		return this;
	}
	
	public String getRestriction() {
		return this.restriction;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}
	
	@Override
	public String toString() {
		String output = restriction.replaceAll("p\\.", "").replaceAll(QueryBuilder.PURCHASE_SET_TABLE + "\\.", "");
		Matcher matcher = VAR_PATTERN.matcher(output);
		
		ArrayList<String> varPlaces = new ArrayList<String>();
		
		while (matcher.find()) {
			String number = output.substring(matcher.start() + ":var".length(), matcher.end());
			varPlaces.add(number);
		}
		
		for (String id : varPlaces) {
			output = output.replace(":var" + id, variables.get(id).toString());
		}
		
		return output;
	}

	@Override
	public JsonRootNode jsonize() {
		JsonRootNode values = JsonNodeFactories.array(new Mapper<Entry<String, Object>, JsonRootNode>() {
			@Override
			public JsonRootNode map(Entry<String, Object> input) {
				return JsonNodeFactories.object(
						JsonNodeFactories.field("id", JsonNodeFactories.string(input.getKey())),
						JsonNodeFactories.field("value", JsonNodeFactories.string(input.getValue().toString())),
						JsonNodeFactories.field("type", JsonNodeFactories.string(input.getValue().getClass().getName()))
						);
			}
		}.map(this.variables.entrySet()));
		
		JsonRootNode output = JsonNodeFactories.object( 
				JsonNodeFactories.field("condition", RestrictionTree.parse(restriction).jsonize()),
				JsonNodeFactories.field("values", values));
		
		return output;
	}
}
