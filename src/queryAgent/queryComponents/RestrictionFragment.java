package queryAgent.queryComponents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import queryAgent.queryBuilder.QueryBuilder;
import queryAgent.queryBuilder.SQLTranslator;
import queryAgent.queryBuilder.TranslatorFactory;
import queryAgent.queryComponents.RestrictionTree.RestrictionNode;
import utilities.GeneralUtility;
import utilities.IJsonable;
import utilities.functional.Mapper;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

public class RestrictionFragment implements IJsonable {
	
	private static final Pattern VAR_PATTERN = Pattern.compile(":var([0-9])++");
	private String restriction;
	private Map<Integer, Object> variables;
	
	public RestrictionFragment(String restriction, Map<Integer, Object> variables) {
		this.restriction = restriction;
		this.variables = Collections.unmodifiableMap(variables);
	}
	
	public RestrictionFragment(JsonNode node) {
		restriction = new RestrictionNode(node.getNode("condition")).toString();
		HashMap<Integer, Object> varTemp = new HashMap<Integer, Object>();
		
		List<JsonNode> values = node.getArrayNode("values");
		for (JsonNode sub : values) {
			Integer id = Integer.parseInt(sub.getNumberValue("id"));
				
			SQLTranslator translator = TranslatorFactory.getTranslator(TranslatorFactory.FEATURED_TRANSLATOR);
			Object val = translator.valueParse(sub.getStringValue("value"), sub.getStringValue("type"));
			varTemp.put(id, val);
		}
		variables = Collections.unmodifiableMap(varTemp);
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
	
	public static void main(String[] args) {
		Map<Integer, Object> a = new HashMap<Integer, Object>();
		a.put(1, 7.0f);
		a.put(2, 8.0f);
		RestrictionFragment x = new RestrictionFragment("(cost > :var1) AND (cost < :var2)", a);
		RestrictionFragment y = new RestrictionFragment(x.jsonize());
		System.out.println(GeneralUtility.jsonToString(y.jsonize()));
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

	public Map<Integer, Object> getVariables() {
		return variables;
	}
	
	@Override
	public String toString() {
		String output = restriction.replaceAll("p\\.", "").replaceAll(QueryBuilder.PURCHASE_SET_TABLE + "\\.", "");
		Matcher matcher = VAR_PATTERN.matcher(output + "");
		
		ArrayList<Integer> varPlaces = new ArrayList<Integer>();
		
		while (matcher.find()) {
			String number = output.substring(matcher.start() + ":var".length(), matcher.end());
			varPlaces.add(Integer.parseInt(number));
		}
		
		for (Integer i : varPlaces) {
			output = output.replaceFirst(":var" + i, variables.get(i).toString());
		}
		
		return output;
	}

	@Override
	public JsonRootNode jsonize() {
		JsonRootNode values = JsonNodeFactories.array(new Mapper<Entry<Integer, Object>, JsonRootNode>() {
			@Override
			public JsonRootNode map(Entry<Integer, Object> input) {
				return JsonNodeFactories.object(
						JsonNodeFactories.field("id", JsonNodeFactories.number(input.getKey())),
						JsonNodeFactories.field("value", JsonNodeFactories.string(input.getValue().toString())),
						JsonNodeFactories.field("type", JsonNodeFactories.string(input.getValue().getClass().getName()))
						);
			}
		}.map(this.variables.entrySet()));
		
		JsonRootNode output = JsonNodeFactories.object( 
				JsonNodeFactories.field("condition", RestrictionTree.parse(restriction).jsonize()),
				JsonNodeFactories.field("values", values)
				);
		
		return output;
	}
}
