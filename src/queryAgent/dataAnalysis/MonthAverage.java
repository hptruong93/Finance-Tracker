package queryAgent.dataAnalysis;

import queryAgent.QueryBuilder;
import utilities.DateUtility;
import argo.jdom.JsonNode;

public class MonthAverage extends Feature {
	public MonthAverage() {
		super();
		loadConfig(null);
	}
	
	public static void main(String[] args) {
		QueryBuilder b = new QueryBuilder();
		System.out.println(b.buildConstraint("MONTH(date)", "EQUAL", "" + DateUtility.getThisMonth()));
	}
	
	@Override
	public void loadConfig(JsonNode node) {
		name = "Month average";
		description = "Get the average cost of the month";
		
		fields.add("AVG(cost)");
		criteria.add(builder.buildConstraint("MONTH(date)", "EQUAL", "" + DateUtility.getThisMonth()));
	}
}
